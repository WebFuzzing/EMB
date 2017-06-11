package se.devscout.scoutapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import se.devscout.scoutapi.model.SystemMessage;
import se.devscout.scoutapi.model.Tag;
import se.devscout.scoutapi.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScoutAPIConfiguration extends Configuration {
    @NotNull
    @Valid
    private File mediaFilesFolder;

    @NotNull
    @Valid
    private File tempFolder;

    @NotNull
    @Valid
    private String crawlerUser;

    @NotNull
    @Valid
    private DataSourceFactory database;

    @NotNull
    @Valid
    private long autoUpdateIntervalSeconds = 60L * 60L; // Default to 1 hour = 3600 seconds
    private se.devscout.scoutapi.textanalyzer.Configuration similarityCalculatorConfiguration;
    private se.devscout.scoutapi.auth.google.Configuration googleAuthentication;

    @JsonProperty
    public DataSourceFactory getDatabase() {
        return database;
    }

    @Valid
    private List<Tag> defaultTags = new ArrayList<>();

    @JsonProperty
    public List<Tag> getDefaultTags() {
        return defaultTags;
    }

    @Valid
    private List<User> defaultUsers = new ArrayList<>();

    @Valid
    private List<SystemMessage> defaultSystemMessages = new ArrayList<>();

    @JsonProperty
    public List<User> getDefaultUsers() {
        return defaultUsers;
    }

    public File getMediaFilesFolder() {
        return mediaFilesFolder;
    }

    public void setMediaFilesFolder(File mediaFilesFolder) {
        this.mediaFilesFolder = mediaFilesFolder;
    }

    public File getTempFolder() {
        return tempFolder;
    }

    public void setTempFolder(File tempFolder) {
        this.tempFolder = tempFolder;
    }

    public String getCrawlerUser() {
        return crawlerUser;
    }

    /**
     * The interval, in seconds, with which to crawl the activity source site(s) and update the database accordingly.
     *
     * Set to 0 in order to disable the auto-updater.
     */
    public long getAutoUpdateIntervalSeconds() {
        return autoUpdateIntervalSeconds;
    }

    public void setAutoUpdateIntervalSeconds(long autoUpdateIntervalSeconds) {
        this.autoUpdateIntervalSeconds = autoUpdateIntervalSeconds;
    }

    public se.devscout.scoutapi.textanalyzer.Configuration getSimilarityCalculatorConfiguration() {
        return similarityCalculatorConfiguration;
    }

    public void setSimilarityCalculatorConfiguration(se.devscout.scoutapi.textanalyzer.Configuration similarityCalculatorConfiguration) {
        this.similarityCalculatorConfiguration = similarityCalculatorConfiguration;
    }

    public se.devscout.scoutapi.auth.google.Configuration getGoogleAuthentication() {
        return googleAuthentication;
    }

    public void setGoogleAuthentication(se.devscout.scoutapi.auth.google.Configuration googleAuthentication) {
        this.googleAuthentication = googleAuthentication;
    }

    public List<SystemMessage> getDefaultSystemMessages() {
        return defaultSystemMessages;
    }

    public void setDefaultSystemMessages(List<SystemMessage> defaultSystemMessages) {
        this.defaultSystemMessages = defaultSystemMessages;
    }
}
