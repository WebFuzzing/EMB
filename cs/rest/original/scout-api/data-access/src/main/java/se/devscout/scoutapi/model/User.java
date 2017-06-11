package se.devscout.scoutapi.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "USERS" /* Table cannot be called USER since that is a reserved word in PostgreSQL. */)
@NamedQueries(value = {
        @NamedQuery(name = "User.all", query = "SELECT u FROM User u"),
        @NamedQuery(name = "User.byIdentity", query = "" +
                "SELECT u " +
                "FROM User AS u JOIN u.identities AS i " +
                "WHERE i.type = :type AND i.value = :value"),
        @NamedQuery(name = "User.byName", query = "" +
                "SELECT u " +
                "FROM User AS u " +
                "WHERE u.name LIKE :name")
})
@XmlRootElement
@JsonFilter("custom")
@JsonNaming(value = PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "date_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated = new Date();

    @Basic(optional = false)
    private String name;

    @Basic
    @Column(name = "email_address")
    private String emailAddress;

    @Basic(optional = false)
    @Column(name = "authorization_level")
    private int authorizationLevel = 0;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @XmlElement(name = "identity")
    @XmlElementWrapper(name = "identities")
    @BatchSize(size = 5)
    private List<UserIdentity> identities = new ArrayList<UserIdentity>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    @XmlElement(name = "rating")
    @XmlElementWrapper(name = "ratings")
    @BatchSize(size = 50)
    // Don't initialize the list to an empty list -- it makes it impossible to differentiate between "list is empty" and "list should be ignored".
    private List<ActivityRating> ratings;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public User(String name, int authorizationLevel, String apiKey) {
        this.name = name;
        this.authorizationLevel = authorizationLevel;
        addIdentity(IdentityType.API, apiKey);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserIdentity> getIdentities() {
        return identities;
    }

    public void setIdentities(List<UserIdentity> identities) {
        this.identities = identities;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void addIdentity(IdentityType type, String value) {
        identities.add(new UserIdentity(type, value, this));
    }

    @JsonIgnore
    public boolean isApiKeySet() {
        for (UserIdentity identity : identities) {
            if (identity.getType() == IdentityType.API) {
                return true;
            }
        }
        return false;
    }

    public UserIdentity getIdentityById(long id) {
        for (UserIdentity identity : identities) {
            if (identity.getId() == id) {
                return identity;
            }
        }
        return null;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return name + " (id: " + id + ")";
    }

    public int getAuthorizationLevel() {
        return authorizationLevel;
    }

    public void setAuthorizationLevel(int authorizationLevel) {
        this.authorizationLevel = authorizationLevel;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
