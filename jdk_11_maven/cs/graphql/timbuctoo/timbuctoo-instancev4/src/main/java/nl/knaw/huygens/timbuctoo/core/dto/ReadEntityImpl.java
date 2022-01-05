package nl.knaw.huygens.timbuctoo.core.dto;

import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.core.dto.property.TimProperty;
import nl.knaw.huygens.timbuctoo.model.Change;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReadEntityImpl implements ReadEntity {
  private List<TimProperty<?>> properties;
  private int rev;
  private boolean deleted;
  private String pid;
  private List<String> types;
  private Change modified;
  private Change created;
  private List<RelationRef> relations;
  private String displayName;
  private UUID id;
  private final HashMap<String, Object> extraProperties;
  private URI rdfUri;
  private List<String> rdfAlternatives = new ArrayList<>(0);

  public ReadEntityImpl() {
    extraProperties = Maps.newHashMap();
  }

  public void setProperties(List<TimProperty<?>> properties) {
    this.properties = properties;
  }

  @Override
  public Iterable<TimProperty<?>> getProperties() {
    return properties;
  }

  public void setRev(int rev) {
    this.rev = rev;
  }

  @Override
  public int getRev() {
    return rev;
  }

  public void setDeleted(Boolean deleted) {
    this.deleted = deleted;
  }

  @Override
  public boolean getDeleted() {
    return deleted;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  @Override
  public String getPid() {
    return pid;
  }

  @Override
  public URI getRdfUri() {
    return rdfUri;
  }

  public void setRdfUri(URI rdfUri) {
    this.rdfUri = rdfUri;
  }

  public void setTypes(List<String> types) {
    this.types = types;
  }

  @Override
  public List<String> getTypes() {
    return types;
  }

  public void setModified(Change modified) {
    this.modified = modified;
  }

  @Override
  public Change getModified() {
    return modified;
  }

  public void setCreated(Change created) {
    this.created = created;
  }

  @Override
  public Change getCreated() {
    return created;
  }

  public void setRelations(List<RelationRef> relations) {
    this.relations = relations;
  }

  @Override
  public List<RelationRef> getRelations() {
    return relations;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  @Override
  public UUID getId() {
    return id;
  }

  public void addExtraPoperty(String key, Object value) {
    extraProperties.put(key, value);
  }

  @Override
  public Map<String, Object> getExtraProperties() {
    return extraProperties;
  }

  @Override
  public List<String> getRdfAlternatives() {
    return rdfAlternatives;
  }

  public void setRdfAlternatives(List<String> rdfAlternatives) {
    this.rdfAlternatives = rdfAlternatives;
  }
}
