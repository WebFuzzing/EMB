package io.github.proxyprint.kitchen.controllers.consumer.printrequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 09-05-2016.
 */
public class ConsumerPrintRequest {
    // private Map<String,List<ConsumerPrintRequestDocumentInfo>> files;
    private List<Long> printshops;
    int pages;

    public ConsumerPrintRequest() {
        // files = new HashMap<>();
        printshops = new ArrayList<>();
        pages=0;
    }

    public ConsumerPrintRequest(Map<String, List<ConsumerPrintRequestDocumentInfo>> files, List<Long> printshops, int pages) {
        // this.files = files;
        this.printshops = printshops;
        this.pages = pages;
    }

    public List<Long> getPrintshops() { return printshops; }

    public void setPrintshops(List<Long> printshops) { this.printshops = printshops; }

    public int getPages() { return pages; }

    public void setPages(int pages) { this.pages = pages; }
}
