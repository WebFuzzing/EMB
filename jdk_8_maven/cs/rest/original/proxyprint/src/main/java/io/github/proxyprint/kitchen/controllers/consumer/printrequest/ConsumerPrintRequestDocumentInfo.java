package io.github.proxyprint.kitchen.controllers.consumer.printrequest;

import io.github.proxyprint.kitchen.models.consumer.printrequest.DocumentSpec;

import java.util.List;

/**
 * Created by daniel on 09-05-2016.
 */
public class ConsumerPrintRequestDocumentInfo {
    private List<DocumentSpec> specs;

    public ConsumerPrintRequestDocumentInfo(){}

    public ConsumerPrintRequestDocumentInfo(List<DocumentSpec> specs) {
        this.specs = specs;
    }

    public List<DocumentSpec> getSpecs() { return specs; }

    public void setSpecs(List<DocumentSpec> specs) { this.specs = specs; }

    @Override
    public String toString() {
        return "ConsumerPrintRequestDocumentInfo{" +
                " specs=" + specs +
                '}';
    }
}
