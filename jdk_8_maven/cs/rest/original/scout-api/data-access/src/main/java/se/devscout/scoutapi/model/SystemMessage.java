package se.devscout.scoutapi.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "SYSTEM_MESSAGE")
@NamedQueries(value = {
        @NamedQuery(name = "SystemMessage.all", query = "SELECT t FROM SystemMessage t"),
        @NamedQuery(name = "SystemMessage.validAtTime", query = "" +
                "SELECT t " +
                "FROM SystemMessage AS t " +
                "WHERE " +
                "   (t.validTo IS NULL OR t.validTo > :date)" +
                "   AND " +
                "   (t.validFrom IS NULL OR t.validFrom <= :date)"),
        @NamedQuery(name = "SystemMessage.validAtOrEventuallyAfterTime", query = "" +
                "SELECT t " +
                "FROM SystemMessage AS t " +
                "WHERE t.validTo IS NULL OR t.validTo > :date"),
        @NamedQuery(name = "SystemMessage.byKey", query = "" +
                "SELECT t " +
                "FROM SystemMessage AS t " +
                "WHERE t.key LIKE :key")
})
@XmlRootElement
@JsonFilter("custom")
@JsonNaming(value = PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class SystemMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic(optional = false)
    @Column(unique = true)
    private String key;

    @Basic(optional = false)
    private String value;

    @Basic(optional = true)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "valid_to")
    private Date validTo;

    @Basic(optional = true)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "valid_from")
    private Date validFrom;

    public SystemMessage() {
    }

    public SystemMessage(long id) {
        setId(id);
    }

    public SystemMessage(String key, String value) {
        this(key, value, null, null);
    }

    public SystemMessage(String key, String value, Date validFrom, Date validTo) {
        this.key = key;
        this.value = value;
        this.validTo = validTo;
        this.validFrom = validFrom;
    }

    public SystemMessage(long id, String key, String value, Date validFrom, Date validTo) {
        this(key, value, validFrom, validTo);
        setId(id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SystemMessage)) return false;
        SystemMessage that = (SystemMessage) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(key, that.key) &&
                Objects.equals(value, that.value) &&
                Objects.equals(validTo, that.validTo) &&
                Objects.equals(validFrom, that.validFrom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, key, value, validTo, validFrom);
    }
}
