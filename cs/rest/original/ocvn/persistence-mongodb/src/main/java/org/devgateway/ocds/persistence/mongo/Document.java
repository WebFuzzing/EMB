package org.devgateway.ocds.persistence.mongo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.devgateway.ocds.persistence.mongo.merge.Merge;
import org.devgateway.ocds.persistence.mongo.merge.MergeStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Document
 * <p>
 * Links to, or descriptions of, external documents can be attached at various locations within the standard.
 * Documents may be supporting information, formal notices, downloadable forms,
 * or any other kind of resource that should be made public as part of full open contracting.
 *
 * http://standard.open-contracting.org/latest/en/schema/reference/#document
 *
 */
@JsonPropertyOrder({
        "id",
        "documentType",
        "title",
        "description",
        "url",
        "datePublished",
        "dateModified",
        "format",
        "language"
})
public class Document implements Identifiable {

    /**
     * A local, unique identifier for this document. This field is used to keep track of multiple revisions
     * of a document through the compilation from release to record mechanism.
     * (Required)
     *
     */
    @JsonProperty("id")
    @Merge(MergeStrategy.overwrite)
    private String id;

    /**
     * A classification of the document described taken from the
     * [documentType codelist](http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#document-type).
     * Values from the provided codelist should be used wherever possible,
     * though extended values can be provided if the codelist does not have a relevant code.
     *
     */
    @JsonProperty("documentType")
    @Merge(MergeStrategy.ocdsVersion)
    private String documentType;

    /**
     * The document title.
     *
     */
    @JsonProperty("title")
    @Merge(MergeStrategy.ocdsVersion)
    private String title;

    /**
     * A short description of the document. We recommend descriptions do not exceed 250 words.
     * In the event the document is not accessible online, the description field can be used to describe arrangements
     * for obtaining a copy of the document.
     *
     */
    @JsonProperty("description")
    @Merge(MergeStrategy.ocdsVersion)
    private String description;

    /**
     *  direct link to the document or attachment. The server providing access to this document should be configured
     *  to correctly report the document mime type.
     *
     */
    @JsonProperty("url")
    @Merge(MergeStrategy.ocdsVersion)
    private String url;

    /**
     * The date on which the document was first published. This is particularly important for legally
     * important documents such as notices of a tender.
     *
     */
    @JsonProperty("datePublished")
    @Merge(MergeStrategy.ocdsVersion)
    private Date datePublished;

    /**
     * Date that the document was last modified
     *
     */
    @JsonProperty("dateModified")
    @Merge(MergeStrategy.ocdsVersion)
    private Date dateModified;

    /**
     * The format of the document taken from the
     * [IANA Media Types code list](http://www.iana.org/assignments/media-types/),
     * with the addition of one extra value for 'offline/print', used when this document entry is being used
     * to describe the offline publication of a document. Use values from the template column.
     * Links to web pages should be tagged 'text/html'.
     *
     */
    @JsonProperty("format")
    @Merge(MergeStrategy.ocdsVersion)
    private String format;

    /**
     * Specifies the language of the linked document using either two-digit
     * [ISO 639-1](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes), o
     * r extended [BCP47 language tags](http://www.w3.org/International/articles/language-tags/).
     * The use of two-letter codes from [ISO 639-1](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes)
     * is strongly recommended unless there is a clear user need for distinguishing the language subtype.
     *
     */
    @JsonProperty("language")
    @Merge(MergeStrategy.ocdsVersion)
    private String language;

    /**
     * A local, unique identifier for this document. This field is used to keep track of multiple revisions of
     * a document through the compilation from release to record mechanism.
     * (Required)
     *
     * @return
     *     The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * A local, unique identifier for this document. This field is used to keep track of multiple revisions of
     * a document through the compilation from release to record mechanism.
     * (Required)
     *
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * A classification of the document described taken from the
     * [documentType codelist](http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#document-type).
     * Values from the provided codelist should be used wherever possible, though extended values can be provided if
     * the codelist does not have a relevant code.
     *
     * @return
     *     The documentType
     */
    @JsonProperty("documentType")
    public String getDocumentType() {
        return documentType;
    }

    /**
     * A classification of the document described taken from the
     * [documentType codelist](http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#document-type).
     * Values from the provided codelist should be used wherever possible, though extended values can be provided if
     * the codelist does not have a relevant code.
     *
     * @param documentType
     *     The documentType
     */
    @JsonProperty("documentType")
    public void setDocumentType(final String documentType) {
        this.documentType = documentType;
    }

    /**
     * The document title.
     *
     * @return
     *     The title
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    /**
     * The document title.
     *
     * @param title
     *     The title
     */
    @JsonProperty("title")
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * A short description of the document. We recommend descriptions do not exceed 250 words.
     * In the event the document is not accessible online, the description field can be used to describe arrangements
     * for obtaining a copy of the document.
     *
     * @return
     *     The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * A short description of the document. We recommend descriptions do not exceed 250 words.
     * In the event the document is not accessible online, the description field can be used to describe arrangements
     * for obtaining a copy of the document.
     *
     * @param description
     *     The description
     */
    @JsonProperty("description")
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     *  direct link to the document or attachment. The server providing access to this document should be configured
     *  to correctly report the document mime type.
     *
     * @return
     *     The url
     */
    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    /**
     *  direct link to the document or attachment. The server providing access to this document should be configured
     *  to correctly report the document mime type.
     *
     * @param url
     *     The url
     */
    @JsonProperty("url")
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * The date on which the document was first published. This is particularly important for legally important
     * documents such as notices of a tender.
     *
     * @return
     *     The datePublished
     */
    @JsonProperty("datePublished")
    public Date getDatePublished() {
        return datePublished;
    }

    /**
     * The date on which the document was first published. This is particularly important for legally important
     * documents such as notices of a tender.
     *
     * @param datePublished
     *     The datePublished
     */
    @JsonProperty("datePublished")
    public void setDatePublished(final Date datePublished) {
        this.datePublished = datePublished;
    }

    /**
     * Date that the document was last modified
     *
     * @return
     *     The dateModified
     */
    @JsonProperty("dateModified")
    public Date getDateModified() {
        return dateModified;
    }

    /**
     * Date that the document was last modified
     *
     * @param dateModified
     *     The dateModified
     */
    @JsonProperty("dateModified")
    public void setDateModified(final Date dateModified) {
        this.dateModified = dateModified;
    }

    /**
     * The format of the document taken from the
     * [IANA Media Types code list](http://www.iana.org/assignments/media-types/),
     * with the addition of one extra value for 'offline/print', used when this document entry is being used to
     * describe the offline publication of a document. Use values from the template column.
     * Links to web pages should be tagged 'text/html'.
     *
     * @return
     *     The format
     */
    @JsonProperty("format")
    public String getFormat() {
        return format;
    }

    /**
     * The format of the document taken from the
     * [IANA Media Types code list](http://www.iana.org/assignments/media-types/),
     * with the addition of one extra value for 'offline/print', used when this document entry is being used to
     * describe the offline publication of a document. Use values from the template column.
     * Links to web pages should be tagged 'text/html'.
     *
     * @param format
     *     The format
     */
    @JsonProperty("format")
    public void setFormat(final String format) {
        this.format = format;
    }

    /**
     * Specifies the language of the linked document using either two-digit
     * [ISO 639-1](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes),
     * or extended [BCP47 language tags](http://www.w3.org/International/articles/language-tags/).
     * The use of two-letter codes from [ISO 639-1](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes)
     * is strongly recommended unless there is a clear user need for distinguishing the language subtype.
     *
     * @return
     *     The language
     */
    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    /**
     * Specifies the language of the linked document using either two-digit
     * [ISO 639-1](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes),
     * or extended [BCP47 language tags](http://www.w3.org/International/articles/language-tags/).
     * The use of two-letter codes from [ISO 639-1](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes)
     * is strongly recommended unless there is a clear user need for distinguishing the language subtype.
     *
     * @param language
     *     The language
     */
    @JsonProperty("language")
    public void setLanguage(final String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(id).
                append(documentType).
                append(title).
                append(description).
                append(url).
                append(datePublished).
                append(dateModified).
                append(format).
                append(language).
                toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Document)) {
            return false;
        }
        Document rhs = ((Document) other);
        return new EqualsBuilder().
                append(id, rhs.id).
                append(documentType, rhs.documentType).
                append(title, rhs.title).
                append(description, rhs.description).
                append(url, rhs.url).
                append(datePublished, rhs.datePublished).
                append(dateModified, rhs.dateModified).
                append(format, rhs.format).
                append(language, rhs.language).
                isEquals();
    }

    @Override
    public Serializable getIdProperty() {
        return id;
    }

}
