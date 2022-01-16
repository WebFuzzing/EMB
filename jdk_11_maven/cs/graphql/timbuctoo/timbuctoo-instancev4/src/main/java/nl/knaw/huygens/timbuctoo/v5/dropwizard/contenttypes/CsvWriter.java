package nl.knaw.huygens.timbuctoo.v5.dropwizard.contenttypes;

import nl.knaw.huygens.timbuctoo.v5.serializable.serializations.CsvSerialization;

import javax.ws.rs.Produces;

@Produces(CsvWriter.MIME_TYPE)
public class CsvWriter extends SerializerWriter {

  public static final String MIME_TYPE = "text/csv";

  public CsvWriter() {
    super(CsvSerialization::new);
  }

  @Override
  public String getMimeType() {
    return MIME_TYPE;
  }
}
