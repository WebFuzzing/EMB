package org.devgateway.ocds.web.rest.controller.request;

import org.devgateway.ocds.web.annotate.LanguageValidation;

/**
 * Created by mpostelnicu on 2/17/17.
 * A paging request that also holds the language param
 */
public class LangYearFilterPagingRequest extends YearFilterPagingRequest {

    @LanguageValidation
    protected String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
