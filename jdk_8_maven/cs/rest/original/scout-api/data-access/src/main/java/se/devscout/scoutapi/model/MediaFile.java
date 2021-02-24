package se.devscout.scoutapi.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.BatchSize;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Pattern;

@Entity
@Table(name = "MEDIA_FILE")
@NamedQueries(value = {
        @NamedQuery(name = "MediaFile.all", query = "SELECT mf FROM MediaFile mf"),
        @NamedQuery(name = "MediaFile.byUri", query = "" +
                "SELECT mf " +
                "FROM MediaFile AS mf " +
                "WHERE mf.uri LIKE :uri"),
        @NamedQuery(name = "MediaFile.byKeyword", query = "" +
                "SELECT mf " +
                "FROM MediaFile AS mf " +
                "WHERE :keyword MEMBER OF mf.keywords"),
        @NamedQuery(name = "MediaFile.associatedActivityProperties", query = "" +
                "SELECT apmf.activityProperties.id " +
                "FROM ActivityPropertiesMediaFile AS apmf " +
                "WHERE apmf.mediaFile = :id")
})
@XmlRootElement
@JsonFilter("custom")
@JsonNaming(value = PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class MediaFile {
    private static final Pattern PATTERN_NOT_ALPHANUMERICAL_CHARACTERS = Pattern.compile("[^\\p{L}\\p{Nd} ]+");
    private static final Pattern PATTERN_MULTIPLE_SPACES = Pattern.compile("\\s+");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic(optional = true)
    @Column(name = "mime_type")
    private String mimeType;

    @Basic(optional = false)
    @Column(unique = true)
    private String uri;

    @Basic(optional = true)
    @Column(length = 100)
    private String name;

    @JsonIgnore // Hide keywords since they may contain sensitive information, such as names and locations.
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "MEDIA_FILE_KEYWORDS", joinColumns = @JoinColumn(name = "MEDIA_FILE_ID"))
    @Column(length = 100, name = "KEYWORD")
    private Set<String> keywords;

    @Basic(optional = true)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "capture_date")
    private Date captureDate;

    @Basic(optional = true)
    @Column(name = "copy_right", length = 100)
    private String copyRight;

    @Basic(optional = true)
    @Column(length = 50)
    private String author;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "mediaFile")
    @XmlElement(name = "activity")
    @XmlElementWrapper(name = "activities")
    @BatchSize(size = 50)
    // Don't initialize the list to an empty list -- it makes it impossible to differentiate between "list is empty" and "list should be ignored".
    private List<ActivityPropertiesMediaFile> activities;

/*
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, orphanRemoval = true, mappedBy = "mediaFile")
    @XmlElement(name = "tag")
    @XmlElementWrapper(name = "tags")
    @BatchSize(size = 50)
    // Don't initialize the list to an empty list -- it makes it impossible to differentiate between "list is empty" and "list should be ignored".
    private List<Tag> tags;
*/

    public MediaFile() {
    }

    public MediaFile(String mimeType, String uri) {
        this.mimeType = mimeType;
        setUri(uri);
    }

    public MediaFile(String mimeType, String uri, String name) {
        this.mimeType = mimeType;
        setUri(uri);
        setName(name);
    }

    public MediaFile(long id) {
        setId(id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        try {
            this.uri = new URI(uri).toString();
        } catch (URISyntaxException e) {
            LoggerFactory.getLogger(MediaFile.class).info("Could not set URI", e);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtils.left(name, 100);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediaFile)) return false;
        MediaFile mediaFile = (MediaFile) o;
        return Objects.equals(id, mediaFile.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static String getSimplifiedKeyword(String keyword) {
        return PATTERN_MULTIPLE_SPACES
                .matcher(
                        PATTERN_NOT_ALPHANUMERICAL_CHARACTERS
                                .matcher(keyword)
                                .replaceAll("")
                )
                .replaceAll(" ")
                .toLowerCase()
                .trim();
    }

    public Date getCaptureDate() {
        return captureDate;
    }

    public void setCaptureDate(Date captureDate) {
        this.captureDate = captureDate;
    }

    public String getCopyRight() {
        return copyRight;
    }

    public void setCopyRight(String copyRight) {
        this.copyRight = copyRight;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Set<String> getKeywords() {
        if (keywords == null) {
            keywords = new HashSet<>();
        }
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public String toString() {
        return "MediaFile{" +
                "name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                ", id=" + id +
                '}';
    }
}
