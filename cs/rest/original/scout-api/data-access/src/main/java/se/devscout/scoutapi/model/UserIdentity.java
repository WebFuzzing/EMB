package se.devscout.scoutapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Entity
@Table(name = "user_identity")
/*
@NamedQueries(value = {
        @NamedQuery(name = "Identity.all", query = "SELECT i FROM UserIdentity AS i")
})
*/
@XmlRootElement
@JsonFilter("custom")
@JsonNaming(value = PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class UserIdentity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "date_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated = new Date();

    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    private IdentityType type;

    @Basic(optional = false)
    private String value;

    @ManyToOne
    @JsonBackReference
    @XmlIDREF
    @XmlAttribute
    private User user;

    public UserIdentity() {
    }

    public UserIdentity(IdentityType type, String value) {
        this(type, value, null);
    }

    public UserIdentity(IdentityType type, String value, User owner) {
        this.type = type;
        this.value = value;
        this.user = owner;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public IdentityType getType() {
        return type;
    }

    public void setType(IdentityType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public User getUser() {
        return user;
    }

    public long getId() {
        return id;
    }
}
