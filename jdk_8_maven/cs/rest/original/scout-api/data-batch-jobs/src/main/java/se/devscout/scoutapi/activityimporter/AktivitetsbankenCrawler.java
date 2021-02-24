package se.devscout.scoutapi.activityimporter;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.devscout.scoutapi.model.Activity;
import se.devscout.scoutapi.model.ActivityProperties;
import se.devscout.scoutapi.model.MediaFile;
import se.devscout.scoutapi.model.Tag;

import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AktivitetsbankenCrawler {

    /**
     * Expressions which indicate that text is a list item.
     */
    private static final ImmutableMap<Pattern, String> LISTS = ImmutableMap.<Pattern, String>builder()
            .put(Pattern.compile("^-\\s+"), "- ")
            .put(Pattern.compile("^• +"), "- ")
            .put(Pattern.compile("^" + String.valueOf(Character.toChars(8211)) + "\\s+"), "- ")
            .put(Pattern.compile("^\\s*\\d\\.\\s+"), "1. ")
            .build();

    private static final ImmutableMap<String, String> MIME_TYPES = ImmutableMap.<String, String>builder()
            .put(".jpg", "image/jpeg")
            .put(".gif", "image/gif")
            .put(".png", "image/png")
            .build();

    private static final Logger LOGGER = LoggerFactory.getLogger(AktivitetsbankenCrawler.class);

    private WebPageLoader pageLoader;

    public AktivitetsbankenCrawler(WebPageLoader pageLoader) {
        this.pageLoader = pageLoader;
    }

    private enum TimeInterval {
        aktivitets_tidsatgang_5_15_min(5, 15),
        aktivitets_tidsatgang_15_30_min(15, 30),
        aktivitets_tidsatgang_30_min_1_timme(30, 60),
        aktivitets_tidsatgang_2_3_timmar(120, 180),
        aktivitets_tidsatgang_heldag(6 * 60, 10 * 60),
        aktivitets_tidsatgang_halvdag(3 * 60, 5 * 60),
        aktivitets_tidsatgang_flera_dagar(8 * 60, 7 * 24 * 60);
        private int min;
        private int max;

        TimeInterval(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }

    private enum GroupSize {
        gruppstorlek_1_8_pers(1, 8),
        gruppstorlek_8_15_pers(8, 15),
        gruppstorlek_16_or_more(16, 99),
        gruppstorlek_16_eller_fler(16, 99);
        private int min;
        private int max;

        GroupSize(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }

    private enum AgeInterval {
        aktivitet_alder_8_10_ar_spararscout(8, 10),
        aktivitet_alder_10_12_ar_upptackarscout(10, 12),
        aktivitet_alder_12_15_ar_aventyrarscout(12, 15),
        aktivitet_alder_15_19_ar_utmanarscout(15, 19),
        aktivitet_alder_19_25_ar_roverscout(19, 25);

        private int min;
        private int max;

        AgeInterval(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }
    }

    private void run() throws IOException {

        List<ActivityProperties> activities = readAllActivities();

        PrintStream out = new PrintStream("out.txt", Charsets.UTF_8.name());
        activities.stream().forEach(properties -> {
            printActivityInfo(out, properties);
        });

        out.close();
    }

    public List<ActivityProperties> readAllActivities() throws IOException {
        Map<String, Tag> tags = getTags();
        String urlAll = "http://www.scouterna.se/aktiviteter-och-lager/aktivitetsbanken/alla-aktiviteter";
        Document docAll = pageLoader.fetch(urlAll);

        return docAll.select(".funna-aktiviteter > li > strong > a").stream().map(element -> {
            String urlOne = element.attr("href");
            try {
                return readActivity(urlOne, tags);
            } catch (IOException e) {
                // Ignore printing stacktrace for IOExceptions. Otherwise the logs get cluttered with relatively useless information.
                LOGGER.info("I/O exception when processing " + urlOne + ". " + e.getClass().getSimpleName() + ": " + e.getMessage());
                return null;
            } catch (Throwable e) {
                LOGGER.warn("Exception when processing " + urlOne, e);
                return null;
            }
        }).filter(properties -> properties != null).collect(Collectors.toList());
    }

    private ActivityProperties readActivity(String url, Map<String, Tag> foundTags) throws IOException {
        Document docOne = pageLoader.fetch(url);

        Element wrapper = docOne.select("div.aktivitet").first();

        if (wrapper == null) {
            throw new IOException("Page does not seem to describe an activity. This can happen if the web server returns an HTML version of a 404 Not Found (still http status 200 but not the expected content).");
        }

        Element entryContentElement = wrapper.select(".entry-content").get(0);

        fixRelativeUrls(entryContentElement);

        Set<String> classNames = wrapper.classNames();

        ActivityProperties properties = new ActivityProperties();
        properties.setSource(url);
        properties.setName(wrapper.select("h1.entry-title").get(0).text());
        List<Tag> trackOrConceptTags = classNames.stream()
                .filter(s -> s.startsWith("aktivitets-mal-") || s.startsWith("aktivitets-upplagg-"))
                .map(s1 -> s1.substring(s1.indexOf('-', 11) + 1))
                .map(s2 -> foundTags.containsKey(s2) ? foundTags.get(s2) : new Tag("aktivitetsbanken-misc", s2))
                .collect(Collectors.toList());


        ArrayList<Tag> oneTags = new ArrayList<>();
        oneTags.addAll(trackOrConceptTags);
        oneTags.addAll(classNames.stream()
                .filter(s3 -> s3.startsWith("aktivitetskategori-"))
                .map(s1 -> s1.substring(s1.indexOf('-', 11) + 1))
                .filter(s -> !foundTags.containsKey(s) &&
                        !Stream.of(AgeInterval.values()).anyMatch(ageInterval -> ageInterval.name().equals("aktivitet_alder_" + s.replace('-', '_'))) &&
                        !Stream.of(GroupSize.values()).anyMatch(ageInterval -> ageInterval.name().equals("gruppstorlek_" + s.replace('-', '_'))) &&
                        !Stream.of(TimeInterval.values()).anyMatch(ageInterval -> ageInterval.name().equals("aktivitets_tidsatgang_" + s.replace('-', '_'))))
                .map(s2 -> new Tag("aktivitetsbanken-misc", s2))
                .collect(Collectors.toList()));

        properties.setTags(oneTags);

        fillMediaFiles(properties, entryContentElement);

        fillAge(properties, classNames);

        fillParticipants(properties, classNames);

        fillTime(properties, classNames);

        fillIntroduction(properties, entryContentElement);

        fillDescriptions(
                entryContentElement.children().subList(entryContentElement.getElementsContainingOwnText("S\u00e5 genomf\u00f6r du aktiviteten").get(0).elementSiblingIndex() + 1, entryContentElement.children().size()),
                properties);

        Activity activity = new Activity();
        activity.updateProperties(properties, false);
        return properties;
    }

    private void printActivityInfo(PrintStream out, ActivityProperties properties) {
        out.println(properties.getSource());
        out.printf("%-50s %11s %11s %11s %s%n",
                "Name",
                "Age",
                "Time",
                "Particip.",
                "Tags");
        out.printf("%-50s %5d %5d %5d %5d %5d %5d %s%n",
                properties.getName(),
                properties.getAgeMin(),
                properties.getAgeMax(),
                properties.getTimeMin(),
                properties.getTimeMax(),
                properties.getParticipantsMin(),
                properties.getParticipantsMax(),
                Joiner.on(',').join(properties.getTags()));
        properties.getMediaFiles().forEach(mediaFile -> {
            out.println("         Image: " + mediaFile.getUri());
        });
        Stream.of(
                properties.getDescriptionIntroduction(),
                properties.getDescriptionMain(),
                properties.getDescriptionMaterial(),
                properties.getDescriptionPrepare(),
                properties.getDescriptionSafety(),
                properties.getDescriptionNotes()
        ).map(s -> Strings.repeat(" ", 30) + s.replace("\n", "\n" + Strings.repeat(" ", 30)) + "\n").forEach(out::print);
    }

    private Map<String, Tag> getTags() throws IOException {
        Map<String, Tag> tags = new HashMap<>();

        collectTags(tags, "http://www.scouterna.se/aktiviteter-och-lager/aktivitetsbanken/upplagg/", "aktivitetsbanken-concept");
        collectTags(tags, "http://www.scouterna.se/aktiviteter-och-lager/aktivitetsbanken/mal/", "aktivitetsbanken-track");

        return tags;
    }

    private void collectTags(Map<String, Tag> tags, String url, String group) throws IOException {
        Document docTracks = pageLoader.fetch(url);
        docTracks.select("div.entry-content h2").stream().forEach(element1 -> {
            String href = element1.parent().getElementsByIndexGreaterThan(element1.elementSiblingIndex()).select("a").get(0).attr("href");
            String key = href.substring(url.length(), href.length() - 1);
            tags.put(key, new Tag(group, element1.text()));
        });
    }

    /**
     * Find references to images in:
     * <ul>
     * <li>"IMG" elements</li>
     * <li>"A" elements linking to URLs ending in common image filename extensions (like "jpg" and "png")</li>
     * </ul>
     *
     * @param properties
     * @param entryContentElement
     */
    private void fillMediaFiles(ActivityProperties properties, Element entryContentElement) {
        Elements imagesAndLinks = entryContentElement.select("img, a");
        Collection<MediaFile> mediaFiles = imagesAndLinks.stream()

                // Keep only images and links to images.
                .filter(element1 -> "img".equals(element1.tagName()) || element1.attr("href").toLowerCase().matches(".*(jpg|jpeg|gif|png)$"))

                .collect(() -> new HashMap<String, MediaFile>(), (res, element) -> {
                    /*
                     * Process each element and collect information about each image. Some IMG elements are
                     * wrapped in A elements linking to the image,so it is important to avoid
                     * multiple MediaItem objects referencing the same URL.
                     */
                    String uri = element.attr(element.hasAttr("src") ? "src" : "href");
                    String name = element.attr(element.hasAttr("alt") ? "alt" : "title");

                    if (!res.containsKey(uri)) {
                        // New image URL
                        String mimeType = MIME_TYPES.entrySet().stream()
                                .filter(stringStringEntry -> uri.toLowerCase().endsWith(stringStringEntry.getKey()))
                                .limit(1)
                                .map(stringStringEntry1 -> stringStringEntry1.getValue())
                                .findFirst()
                                .orElse(null);
                        MediaFile mediaFile = new MediaFile(mimeType, uri, name);
                        if (!Strings.isNullOrEmpty(mediaFile.getUri())) {
                            // Do not add if URL is empty, which it will be if the HTML contains an invalid (non-parseable) URL.
                            res.put(uri, mediaFile);
                        }
                    } else {
                        // Previously detected URL once more. See if name is more descriptive (i.e. non-empty) this time.
                        MediaFile mediaFile = res.get(uri);
                        if (Strings.isNullOrEmpty(mediaFile.getName()) && !Strings.isNullOrEmpty(name)) {
                            mediaFile.setName(name);
                        }
                    }

                }, (res1, res2) -> res1.putAll(res2)).values();

        properties.setMediaFiles(mediaFiles);
    }

    /**
     * Use everything before the first H2 element and an introduction.
     */
    private void fillIntroduction(ActivityProperties properties, Element entryContentElement) {
        properties.setDescriptionIntroduction(entryContentElement
                .childNodes().subList(
                        0,
                        entryContentElement.getElementsByTag("h2").get(0).siblingIndex())
                .stream()
                .map(node -> {
                    if (node instanceof TextNode) {
                        return ((TextNode) node).text() + "\n\n";
                    } else if (node instanceof Element) {
                        Element el = (Element) node;
                        if (!"content-preamble".equals(el.attr("class"))) {
                            return el.text() + "\n\n";
                        }
                    }
                    return "";
                })
                .collect(Collectors.joining("")).trim());
    }

    /**
     * Make relative URLs into absolute ones (meaning that protocol and hostname is added when missing).
     */
    private void fixRelativeUrls(Element entryContentElement) {
        entryContentElement.getElementsByTag("a").stream()
                .forEach(element2 -> element2.attr("href", fixUrl(element2.attr("href"))));

        entryContentElement.getElementsByTag("img").stream()
                .forEach(element2 -> element2.attr("src", fixUrl(element2.attr("src"))));
    }

    private String fixUrl(String src) {
        try {
            if (src.startsWith("/")) {
                src = "http://www.scouterna.se" + src;
            } else if (!Pattern.compile("^(http|https|mail)://.*").matcher(src).matches()) {
                src = "http://" + src;
            }
            return new URL(src).toExternalForm();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return src;
        }
    }

    private void fillAge(ActivityProperties properties, Set<String> classNames) {
        properties.setAgeMax(classNames.stream()
                .filter(s -> s.startsWith("aktivitet-alder-") && !s.equals("aktivitet-alder-ledare"))
                .map(s -> s.replace('-', '_'))
                .map(AgeInterval::valueOf)
                .mapToInt(AgeInterval::getMax)
                .max()
                .orElse(Integer.MAX_VALUE));
        properties.setAgeMin(classNames.stream()
                .filter(s -> s.startsWith("aktivitet-alder-") && !s.equals("aktivitet-alder-ledare"))
                .map(s -> s.replace('-', '_'))
                .map(AgeInterval::valueOf)
                .mapToInt(AgeInterval::getMin)
                .min()
                .orElse(Integer.MIN_VALUE));
    }

    private void fillParticipants(ActivityProperties properties, Set<String> classNames) {
        properties.setParticipantsMax(classNames.stream()
                .filter(s -> s.startsWith("gruppstorlek-"))
                .map(s -> s.replace('-', '_'))
                .map(GroupSize::valueOf)
                .mapToInt(value -> value.max)
                .max()
                .orElse(Integer.MAX_VALUE));
        properties.setParticipantsMin(classNames.stream()
                .filter(s -> s.startsWith("gruppstorlek-"))
                .map(s -> s.replace('-', '_'))
                .map(GroupSize::valueOf)
                .mapToInt(value -> value.min)
                .min()
                .orElse(Integer.MIN_VALUE));
    }

    private void fillTime(ActivityProperties properties, Set<String> classNames) {
        properties.setTimeMin(classNames.stream()
                .filter(s -> s.startsWith("aktivitets-tidsatgang-"))
                .map(s -> s.replace('-', '_'))
                .map(TimeInterval::valueOf)
                .mapToInt(value -> value.min)
                .min()
                .orElse(Integer.MIN_VALUE));
        properties.setTimeMax(classNames.stream()
                .filter(s -> s.startsWith("aktivitets-tidsatgang-"))
                .map(s -> s.replace('-', '_'))
                .map(TimeInterval::valueOf)
                .mapToInt(value -> value.max)
                .max()
                .orElse(Integer.MAX_VALUE));
    }

    private void fillDescriptions(List<Element> descriptionElements, ActivityProperties properties) {
        StringBuilder main = new StringBuilder();
        StringBuilder materials = new StringBuilder();
        StringBuilder notes = new StringBuilder();
        StringBuilder preparations = new StringBuilder();
        StringBuilder safety = new StringBuilder();

        StringBuilder sb = main;

        for (Element element : descriptionElements) {
            switch (element.tagName()) {
                case "blockquote":
                    sb.append("> " + processInlineElements(element).replace("\n", "\n> "));
                    break;
                case "div":
                    // TODO: Handle P and other block-level elements nested inside DIV
                    sb.append(processInlineElements(element)).append("\n\n");
                    break;
                case "p":
                case "span":
                    String inline = processInlineElements(element);
                    sb.append(inline).append("\n");
                    boolean isListItem = LISTS.values().stream().anyMatch(s -> inline.startsWith(s));
                    if (isListItem) {
                        // P, or SPAN, is actually a list item.
                        String nextInline = processInlineElements(element.nextElementSibling());
                        boolean isNextListItem = LISTS.values().stream().anyMatch(s -> nextInline.startsWith(s));

                        // Make sure there is not linefeed between consecutive list items
                        if (!isNextListItem) {
                            sb.append("\n");
                        }
                    } else {
                        // Add extra linefeed to indicate end of paragraph
                        sb.append("\n");
                    }
                    break;
                case "h1":
                case "h2":
                case "h3":
                    switch (element.text()) {
                        case "Detta material beh\u00f6ver du":
                            sb = materials;
                            break;
                        case "S\u00e4kerhet":
                            sb = safety;
                            break;
                        case "F\u00f6rberedelse":
                            sb = preparations;
                            break;
                        case "Att t\u00e4nka p\u00e5":
                            sb = notes;
                            break;
                        case "Referenser":
                        case "Aktiviteten \u00e4r gjord av":
                        case "Scoutmetoden":
                            sb = notes;
                            sb.append("## ").append(element.text()).append("\n\n");
                            break;
                        default:
                            sb.append("## ").append(element.text()).append("\n\n");
                    }
                    break;
                case "h4":
                    sb.append("### ").append(element.text()).append("\n\n");
                    break;
                case "ul":
                    for (Element child : element.children()) {
                        sb.append("* ").append(child.text()).append("\n");
                    }
                    sb.append("\n");
                    break;
                case "ol":
                    for (Element child : element.children()) {
                        sb.append("1. ").append(child.text()).append("\n");
                    }
                    sb.append("\n");
                    break;
                case "li":
                    if (!"ul".equals(element.parent().tagName()) && !"li".equals(element.parent().tagName())) {
                        sb.append("* ").append(element.text()).append("\n\n");
                    }
                    break;
                case "br":
                    if (null != element.nextElementSibling() && "br".equals(element.nextElementSibling().tagName())) {
                        sb.append("\n\n");
                    }
                    break;
                case "a":
                    appendLink(sb, element).append("\n\n");
                    break;
                case "img":
                    appendImage(sb, element).append("\n\n");
                    break;
                default:
                    throw new UnsupportedOperationException("Cannot handle " + element.tagName());
            }
        }

        properties.setDescriptionMain(main.length() > 0 ? /*"# SÅ HÄR GÖR DU\n\n" + */trimLineFeeds(main) : "");
        properties.setDescriptionMaterial(materials.length() > 0 ? /*"# MATERIAL\n\n" + */trimLineFeeds(materials) : "");
        properties.setDescriptionNotes(notes.length() > 0 ? /*"# ATT TÄNKA PÅ\n\n" + */trimLineFeeds(notes) : "");
        properties.setDescriptionPrepare(preparations.length() > 0 ? /*"# FÖRBEREDELSER\n\n" + */trimLineFeeds(preparations) : "");
        properties.setDescriptionSafety(safety.length() > 0 ? /*"# SÄKERHETSTIPS\n\n" + */trimLineFeeds(safety) : "");
    }

    private String trimLineFeeds(StringBuilder sb) {
        return sb.toString().trim();
    }

    private String processInlineElements(Element element) {
        StringBuilder sb = new StringBuilder();
        for (Node node : element.childNodes()) {
            if (node instanceof Element) {
                Element childElement = (Element) node;
                switch (childElement.tagName()) {
                    case "br":
                        sb.append("\n");
                        break;
                    case "a":
                        appendLink(sb, childElement);
                        break;
                    case "img":
                        appendImage(sb, childElement);
                        break;
                    case "p":
                        sb.append(childElement.text()).append('\n');
                        break;
                    default:
                        sb.append(childElement.text());
                        break;
                }
            } else {
                boolean isListItem = false;

                String str = node.toString();

                // Check if current node looks like a list item
                for (Map.Entry<Pattern, String> entry : LISTS.entrySet()) {
                    Pattern prefix = entry.getKey();
                    String replacement = entry.getValue();

                    Matcher matcher = prefix.matcher(str);
                    if (matcher.find()) {
                        sb.append(replacement).append(str.substring(matcher.group().length()));
                        isListItem = true;
                        break;
                    }
                }

                if (!isListItem) {
                    // Node does not look like list item

                    // Some activities have header-like paragraph enclosed in odd quotation marks. Make them into proper headers.
                    str = Pattern.compile("”’(.*)”’").matcher(str).replaceAll("### $1\n");
                    sb.append(str);
                }
            }
        }
        return Parser.unescapeEntities(sb.toString(), false);
    }

    private StringBuilder appendImage(StringBuilder sb, Element element) {
        return sb.append("![" + element.attr("alt") + "](" + element.attr("src") + ")");
    }

    private StringBuilder appendLink(StringBuilder sb, Element childElement) {
        return sb.append("[" + childElement.text() + "](" + childElement.attr("href") + ")");
    }

    void setPageLoader(WebPageLoader pageLoader) {
        this.pageLoader = pageLoader;
    }

    public static void main(String[] args) {
        try {
            new AktivitetsbankenCrawler(new DefaultWebPageLoader(Paths.get("temp", "crawler").toFile())).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
