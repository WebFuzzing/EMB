package nl.knaw.huygens.timbuctoo.model.vre;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import javax.ws.rs.core.MediaType;

import static nl.knaw.huygens.timbuctoo.model.vre.Vre.COLOR_CODE_PROPERTY_NAME;
import static nl.knaw.huygens.timbuctoo.model.vre.Vre.DESCRIPTION_PROPERTY_NAME;
import static nl.knaw.huygens.timbuctoo.model.vre.Vre.IMAGE_BLOB_PROPERTY_NAME;
import static nl.knaw.huygens.timbuctoo.model.vre.Vre.IMAGE_MEDIA_TYPE_PROPERTY_NAME;
import static nl.knaw.huygens.timbuctoo.model.vre.Vre.IMAGE_REV_PROPERTY_NAME;
import static nl.knaw.huygens.timbuctoo.model.vre.Vre.PROVENANCE_PROPERTY_NAME;
import static nl.knaw.huygens.timbuctoo.model.vre.Vre.UPLOADED_FILE_NAME;
import static nl.knaw.huygens.timbuctoo.model.vre.Vre.VRE_LABEL_PROPERTY_NAME;
import static nl.knaw.huygens.timbuctoo.model.vre.Vre.VRE_NAME_PROPERTY_NAME;

public class VreMetadata {
  private String provenance = null;
  private String colorCode = null;
  private String description = null;
  private String label = null;
  private String uploadedFilename = null;
  private Integer imageRev = null;
  private MediaType imageMediaType;

  public String getProvenance() {
    return provenance;
  }

  private void setProvenance(String provenance) {
    this.provenance = provenance;
  }

  public String getColorCode() {
    return colorCode;
  }

  private void setColorCode(String colorCode) {
    this.colorCode = colorCode;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getUploadedFilename() {
    return uploadedFilename;
  }

  private void setUploadedFilename(String uploadedFilename) {
    this.uploadedFilename = uploadedFilename;
  }

  public Integer getImageRev() {
    return imageRev;
  }

  private void setImageRev(Integer imageRev) {
    this.imageRev = imageRev;
  }

  private void setImageMediaType(MediaType imageMediaType) {
    this.imageMediaType = imageMediaType;
  }

  public MediaType getImageMediaType() {
    return imageMediaType;
  }

  static VreMetadata fromVertex(Vertex vreVertex) {
    final VreMetadata instance = new VreMetadata();

    instance.setLabel(vreVertex.property(VRE_LABEL_PROPERTY_NAME).isPresent() ?
      vreVertex.value(VRE_LABEL_PROPERTY_NAME) : vreVertex.value(VRE_NAME_PROPERTY_NAME));

    instance.setColorCode(vreVertex.property(COLOR_CODE_PROPERTY_NAME).isPresent() ?
      vreVertex.value(COLOR_CODE_PROPERTY_NAME) : null);

    instance.setProvenance(vreVertex.property(PROVENANCE_PROPERTY_NAME).isPresent() ?
      vreVertex.value(PROVENANCE_PROPERTY_NAME) : null);

    instance.setDescription(vreVertex.property(DESCRIPTION_PROPERTY_NAME).isPresent() ?
      vreVertex.value(DESCRIPTION_PROPERTY_NAME) : null);

    instance.setUploadedFilename(vreVertex.property(UPLOADED_FILE_NAME).isPresent() ?
      vreVertex.value(UPLOADED_FILE_NAME) : null);

    if (vreVertex.property(IMAGE_BLOB_PROPERTY_NAME).isPresent() &&
      vreVertex.property(IMAGE_REV_PROPERTY_NAME).isPresent()) {
      instance.setImageRev(vreVertex.<Integer>value(IMAGE_REV_PROPERTY_NAME));
    }

    if (vreVertex.property(IMAGE_MEDIA_TYPE_PROPERTY_NAME).isPresent()) {
      instance.setImageMediaType(MediaType.valueOf(vreVertex.value(IMAGE_MEDIA_TYPE_PROPERTY_NAME)));
    }
    return instance;
  }

  @Override
  public String toString() {
    return "VreMetadata{" +
      "provenance='" + provenance + '\'' +
      ", colorCode='" + colorCode + '\'' +
      ", description='" + description + '\'' +
      ", label='" + label + '\'' +
      ", uploadedFilename='" + uploadedFilename + '\'' +
      ", imageRev=" + imageRev +
      ", imageMediaType=" + imageMediaType +
      '}';
  }
}
