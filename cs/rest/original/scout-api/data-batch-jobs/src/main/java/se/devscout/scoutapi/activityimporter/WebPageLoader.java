package se.devscout.scoutapi.activityimporter;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface WebPageLoader {
    Document fetch(String url) throws IOException;
}
