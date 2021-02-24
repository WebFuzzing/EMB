package se.devscout.scoutapi.activityimporter;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DefaultWebPageLoader implements WebPageLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWebPageLoader.class);
    private File tempFolder;
    private static Map<String, IOException> blackListedURLs = new HashMap<>();

    public DefaultWebPageLoader(File tempFolder) {
        this.tempFolder = tempFolder;
    }

    @Override
    public Document fetch(String url) throws IOException {
        if (blackListedURLs.containsKey(url)) {
            // URL has been blacklisted. Rethrow the same exception that originally sparked the blacklisting.
            IOException e = blackListedURLs.get(url);
            throw new IOException("The URL " + url + " is blacklisted. " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        File file = new File(tempFolder, url.replaceAll("[^a-z0-9]", ""));
        if (!file.exists()) {
            if (!tempFolder.exists()) {
                tempFolder.mkdirs();
            }
            String sourceData = null;
            try {
                LOGGER.info("Downloading {}", url);
                sourceData = Resources.toString(new URL(url), Charsets.UTF_8);
            } catch (IOException e) {
                // Blacklist the URL. If it failed once, it will probably fail again.
                LOGGER.info("Could not download {}. URL will be black-listed until service is restarted. {}: {}", url, e.getClass().getSimpleName(), e.getMessage());
                blackListedURLs.put(url, e);
                throw e;
            }
            Files.write(sourceData, file, Charsets.UTF_8);
        }
        return Jsoup.parse(file, Charsets.UTF_8.name());
    }

}
