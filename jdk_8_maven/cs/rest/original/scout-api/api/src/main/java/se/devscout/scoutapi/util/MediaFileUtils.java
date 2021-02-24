package se.devscout.scoutapi.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.iptc.IptcDirectory;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.primitives.Ints;
import io.dropwizard.servlets.tasks.Task;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.devscout.scoutapi.dao.*;
import se.devscout.scoutapi.model.Activity;
import se.devscout.scoutapi.model.ActivityProperties;
import se.devscout.scoutapi.model.MediaFile;
import se.devscout.scoutapi.model.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class MediaFileUtils {

    private static final Pattern RESIZED_FILE_NAME_PATTERN = Pattern.compile("^(.*)\\.(\\w+)_(\\d+).(\\w+)$");
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaFileUtils.class);

    public static void initMediaFileMetaData(MediaFile mediaFile, InputStream inputStream, String defaultName) throws ImageProcessingException, IOException {
        // Try reading image metadata.
        Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

        IptcDirectory iptcData = metadata.getFirstDirectoryOfType(IptcDirectory.class);
        ExifIFD0Directory exifData = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

        mediaFile.setCaptureDate(Stream
                        .of(
                                iptcData.getDate(IptcDirectory.TAG_DATE_CREATED),
                                exifData.getDate(ExifIFD0Directory.TAG_DATETIME_ORIGINAL),
                                exifData.getDate(ExifIFD0Directory.TAG_DATETIME))
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null)
        );

        mediaFile.setCopyRight(Stream
                        .of(
                                iptcData.getString(IptcDirectory.TAG_COPYRIGHT_NOTICE),
                                exifData.getString(ExifIFD0Directory.TAG_ARTIST),
                                exifData.getString(ExifIFD0Directory.TAG_WIN_AUTHOR))
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null)
        );

        mediaFile.setAuthor(Stream
                        .of(
                                exifData.getString(ExifIFD0Directory.TAG_ARTIST),
                                iptcData.getString(IptcDirectory.TAG_CONTACT))
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null)
        );

        mediaFile.setName(Stream
                        .of(
                                iptcData.getString(IptcDirectory.TAG_HEADLINE),
                                iptcData.getString(IptcDirectory.TAG_CAPTION),
                                iptcData.getString(IptcDirectory.TAG_BY_LINE_TITLE),
                                defaultName)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null)
        );

        Stream
                .of(
                        iptcData.getStringArray(IptcDirectory.TAG_KEYWORDS),
                        iptcData.getStringArray(IptcDirectory.TAG_CATEGORY),
                        iptcData.getStringArray(IptcDirectory.TAG_SUPPLEMENTAL_CATEGORIES))
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .filter(keyword -> !Strings.isNullOrEmpty(keyword))
                .map(MediaFile::getSimplifiedKeyword)
                .forEach(keyword -> mediaFile.getKeywords().add(keyword));
    }

    public static File getOriginalMediaFile(File file) {
        Matcher matcher = RESIZED_FILE_NAME_PATTERN.matcher(file.getName());
        if (matcher.matches()) {
            if (matcher.group(2).equals(matcher.group(4)) && Ints.tryParse(matcher.group(3)) != null) {
                return new File(file.getParent(), matcher.group(1) + "." + matcher.group(2));
            }
        }
        return null;
    }

    public static boolean isResizedMediaFile(File file) {
        return getOriginalMediaFile(file) != null;
    }

    public static File getSuitableImage(File source, int size, String formatName) throws IOException {
        size = getNextMultipleOfTwo(size);
        File target = new File(source.getParent(), source.getName() + "_" + size + "." + formatName);

        if (target.isFile()) {
            return target;
        }

        // Use Toolkit.createImage instead of ImageIO.read because of a bug in the JPEG read of ImageIO.
        Image img = Toolkit.getDefaultToolkit().createImage(source.toURI().toURL());
        loadCompletely(img);

        BufferedImage image = toBufferedImage(img);// ImageIO.read(source);
        int type = BufferedImage.TYPE_INT_RGB;

        double resizeFactor = 1.0 * size / Math.max(image.getWidth(), image.getHeight());
        if (resizeFactor < 1.0) {
            int width1 = (int) (resizeFactor * image.getWidth());
            int height1 = (int) (resizeFactor * image.getHeight());
            BufferedImage resizedImage = new BufferedImage(width1, height1, type);
            Graphics2D g = resizedImage.createGraphics();

            g.setComposite(AlphaComposite.Src);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.drawImage(image, 0, 0, resizedImage.getWidth(), resizedImage.getHeight(), null);
            g.dispose();

            ImageIO.write(resizedImage, formatName, target);

            return target;
        } else {
            // Resized images would be larger than original. NOT good. Do nothing instead.
            return source;
        }
    }

    /**
     * Since some methods like toolkit.getImage() are asynchronous, this
     * method should be called to load them completely.
     */
    // http://stackoverflow.com/a/19687400
    private static void loadCompletely(Image img) {
        MediaTracker tracker = new MediaTracker(new JPanel());
        tracker.addImage(img, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    // http://stackoverflow.com/a/19687400
    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        bimage.getGraphics().drawImage(img, 0, 0, null);
        bimage.getGraphics().dispose();

        return bimage;
    }

    private static int getNextMultipleOfTwo(int num) {
        int next = 16; // You got to start somewhere are it is very unlikely that images smaller than 16 px will ever be requested.
        while (next < num) {
            next *= 2;
        }
        return next;
    }

    public static class CleanResizedImagesCacheTask extends Task {
        private File mediaFilesFolder;

        public CleanResizedImagesCacheTask(File mediaFilesFolder) {
            super("cache-clear-resized-images");
            this.mediaFilesFolder = mediaFilesFolder;
        }

        @Override
        public void execute(ImmutableMultimap<String, String> immutableMultimap, PrintWriter printWriter) throws Exception {
            Files.list(mediaFilesFolder.toPath())
                    .map(path -> path.toFile())
                    .filter(MediaFileUtils::isResizedMediaFile)
                    .forEach((file) -> {
                        if (file.delete()) {
                            LOGGER.info("Deleted {}", file.getAbsolutePath());
                        } else {
                            LOGGER.info("Could not delete {}", file.getAbsolutePath());
                        }
                    });
        }
    }

    public static class AutoAssignMediaFileToTags extends Task {
        private SessionFactory sessionFactory;
        private File mediaFilesFolder;
        private String crawlerUser;

        public AutoAssignMediaFileToTags(SessionFactory sessionFactory, File mediaFilesFolder, String crawlerUser) {
            super("auto-assign-mediafiles");
            this.sessionFactory = sessionFactory;
            this.mediaFilesFolder = mediaFilesFolder;
            this.crawlerUser = crawlerUser;
        }

        @Override
        public void execute(ImmutableMultimap<String, String> params, PrintWriter printWriter) throws Exception {
            final boolean force = params.containsKey("force") && Boolean.parseBoolean(params.get("force").asList().get(0));

            MediaFileDao mediaFileDao = new MediaFileDao(sessionFactory);

            printWriter.format("Assigning to tags%n");

            DataAccessUtils.runInTransaction(sessionFactory, () -> {
                TagDao tagDao = new TagDao(sessionFactory);
                tagDao.all().stream()
                        .filter(tag -> force || tag.getMediaFile() == null)
                        .forEach(tag -> {
                            List<MediaFile> mediaFiles = mediaFileDao.byKeyword(MediaFile.getSimplifiedKeyword(tag.getName()));
                            for (MediaFile mediaFile : mediaFiles) {
                                try {
                                    URI sourceURI = new URI(mediaFile.getUri());
                                    if (new File(mediaFilesFolder, sourceURI.getPath()).exists()) {
                                        tag.setMediaFile(mediaFile);
                                        tagDao.update(tag);
                                        break;
                                    }
                                } catch (URISyntaxException e) {
                                    LOGGER.warn("Could not parse URI in database", e);
                                }
                            }
                        });
                return null;
            });

            printWriter.format("Assigning to activities%n");

            DataAccessUtils.runInTransaction(sessionFactory, () -> {
                User crawlerUser = DataAccessUtils.getUser(new UserDao(sessionFactory), this.crawlerUser);
                ActivityDao activityDao = new ActivityDao(sessionFactory);
                activityDao.all().stream()
                        .map(Activity::getProperties)
                        .filter(properties -> force || properties.getMediaFiles() == null || properties.getMediaFiles().isEmpty())
                        .forEach(properties -> {
                            List<MediaFile> mediaFiles = mediaFileDao.byKeyword(MediaFile.getSimplifiedKeyword(properties.getName()));
                            ActivityProperties newValues = new ActivityProperties(properties);
                            Collection<MediaFile> updatedMediaFiles = new HashSet<>(newValues.getMediaFiles());
                            for (MediaFile mediaFile : mediaFiles) {
                                try {
                                    URI sourceURI = new URI(mediaFile.getUri());
                                    if (new File(mediaFilesFolder, sourceURI.getPath()).exists()) {
                                        updatedMediaFiles.add(mediaFile);
                                    }
                                } catch (URISyntaxException e) {
                                    LOGGER.warn("Could not parse URI in database", e);
                                }
                            }
                            newValues.setMediaFiles(updatedMediaFiles);
                            newValues.setAuthor(crawlerUser);

                            if (!properties.isContentEqual(newValues)) {
                                activityDao.update(properties.getActivity(), newValues, false, false, true);
                                printWriter.format("Activity %-30s (id %-5d): Updated to have %d media files %n", StringUtils.left(properties.getName(), 30), properties.getActivity().getId(), updatedMediaFiles.size());
                            } else {
                                printWriter.format("Activity %-30s (id %-5d): No change%n", StringUtils.left(properties.getName(), 30), properties.getActivity().getId());
                            }
                        });
                return null;
            });

            printWriter.format("Done%n");
        }
    }
}
