package nl.knaw.huygens.timbuctoo.v5.datastores.implementations;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import nl.knaw.huygens.timbuctoo.v5.dataset.ImportStatus;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.LogList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.DefaultComparisonFormatter;

import javax.xml.transform.Source;
import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class RdfDescriptionSaverTest {
  private static final String BASE_URI = "http://example.org/datasets/DUMMY/clusius2";
  private RdfDescriptionSaver testRdfDescriptionSaver;
  private String descriptionFileName;
  private File descriptionFile;
  private LogList logList;
  private ImportStatus importStatus;

  @Before
  public void setUp() throws Exception {
    descriptionFileName = "description.xml";
    descriptionFile = new File(descriptionFileName);
    logList = new LogList();
    importStatus = new ImportStatus(logList);
    testRdfDescriptionSaver = new RdfDescriptionSaver(descriptionFile, BASE_URI, importStatus);
    testRdfDescriptionSaver.start(0);
  }

  @After
  public void tearDown() throws Exception {
    descriptionFile.delete();
  }

  @Test
  public void addsValue() throws Exception {
    testRdfDescriptionSaver.addValue(BASE_URI, "http://purl.org/dc/terms/title", "DWC Data", null, BASE_URI);
    testRdfDescriptionSaver.commit();

    Source expected = Input.fromByteArray(
      (
        "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
          "  <rdf:Description rdf:about=\"http://example.org/datasets/DUMMY/clusius2\">\n" +
          "    <title xmlns=\"http://purl.org/dc/terms/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">" +
          "DWC Data</title> \n" +
          "  </rdf:Description>\n" +
          "</rdf:RDF>\n"
      ).getBytes(StandardCharsets.UTF_8)
    ).build();

    Source actual = Input.fromFile(descriptionFile).build();
    assertThat(actual,
      isSimilarTo(expected).ignoreWhitespace().withComparisonFormatter(new DefaultComparisonFormatter())
    );
  }

  @Test
  public void addsMultipleValues() throws Exception {
    testRdfDescriptionSaver.addValue(BASE_URI, "http://purl.org/dc/terms/title", "DWC Data", null, BASE_URI);
    testRdfDescriptionSaver.addValue(BASE_URI,
      "http://purl.org/dc/terms/description", "Biographical data of the Digital Web Centre for " +
        "the History of Science (DWC)", null, BASE_URI);
    testRdfDescriptionSaver.commit();

    Source expected = Input.fromByteArray(
      (
        "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
          "  <rdf:Description rdf:about=\"http://example.org/datasets/DUMMY/clusius2\">\n" +
          "    <description xmlns=\"http://purl.org/dc/terms/\">" +
          "Biographical data of the Digital Web Centre for the History of Science (DWC)</description> \n" +
          "    <title xmlns=\"http://purl.org/dc/terms/\">DWC Data</title> \n" +
          "  </rdf:Description>\n" +
          "</rdf:RDF>\n"
      ).getBytes(StandardCharsets.UTF_8)
    ).build();

    Source actual = Input.fromFile(descriptionFile).build();
    assertThat(actual,
      isSimilarTo(expected).ignoreWhitespace().withComparisonFormatter(new DefaultComparisonFormatter())
    );
  }

  @Test
  public void addsValueToExistingDescriptionFile() throws Exception {
    testRdfDescriptionSaver.addValue(BASE_URI, "http://purl.org/dc/terms/title", "DWC Data", null, BASE_URI);
    testRdfDescriptionSaver.commit();
    testRdfDescriptionSaver.addValue(BASE_URI,
      "http://purl.org/dc/terms/description", "Biographical data of the Digital Web Centre for " +
        "the History of Science (DWC)", null, BASE_URI);
    testRdfDescriptionSaver.commit();

    Source expected = Input.fromByteArray(
      (
        "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
          ">\n" +
          "  <rdf:Description rdf:about=\"http://example.org/datasets/DUMMY/clusius2\">\n" +
          "    <description xmlns=\"http://purl.org/dc/terms/\">" +
          "Biographical data of the Digital Web Centre for the History of Science (DWC)</description>\n" +
          "    <title xmlns=\"http://purl.org/dc/terms/\">DWC Data</title>\n" +
          "  </rdf:Description>\n" +
          "</rdf:RDF>\n"
      ).getBytes(StandardCharsets.UTF_8)
    ).build();

    Source actual = Input.fromFile(descriptionFile).build();
    assertThat(actual,
      isSimilarTo(expected).ignoreWhitespace().withComparisonFormatter(new DefaultComparisonFormatter())
    );
  }


  @Test
  public void addsMultipleValuesToSamePredicate() throws Exception {
    testRdfDescriptionSaver.addValue(BASE_URI, "http://purl.org/dc/terms/title", "DWC Data", null, BASE_URI);
    testRdfDescriptionSaver.addValue(BASE_URI,
      "http://purl.org/dc/terms/title", "DWC Data 2",
      null, BASE_URI);
    testRdfDescriptionSaver.commit();

    Source expected = Input.fromByteArray(
      (
        "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
          ">\n" +
          "  <rdf:Description rdf:about=\"http://example.org/datasets/DUMMY/clusius2\">\n" +
          "    <title xmlns=\"http://purl.org/dc/terms/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">" +
          "DWC Data</title> \n" +
          "    <title xmlns=\"http://purl.org/dc/terms/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">" +
          "DWC Data 2</title> \n" +
          "  </rdf:Description>\n" +
          "</rdf:RDF>\n"
      ).getBytes(StandardCharsets.UTF_8)
    ).build();

    Source actual = Input.fromFile(descriptionFile).build();
    assertThat(actual,
      isSimilarTo(expected).ignoreWhitespace().withComparisonFormatter(new DefaultComparisonFormatter())
    );
  }

  @Test
  public void doesNotAddDuplicateValuesToSamePredicate() throws Exception {
    testRdfDescriptionSaver.addValue(BASE_URI, "http://purl.org/dc/terms/title", "DWC Data", null, BASE_URI);
    testRdfDescriptionSaver.addValue(BASE_URI,
      "http://purl.org/dc/terms/title", "DWC Data",
      null, BASE_URI);
    testRdfDescriptionSaver.commit();

    Source expected = Input.fromByteArray(
      (
        "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
          ">\n" +
          "  <rdf:Description rdf:about=\"http://example.org/datasets/DUMMY/clusius2\">\n" +
          "    <title xmlns=\"http://purl.org/dc/terms/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">" +
          "DWC Data</title> \n" +
          "  </rdf:Description>\n" +
          "</rdf:RDF>\n"
      ).getBytes(StandardCharsets.UTF_8)
    ).build();

    Source actual = Input.fromFile(descriptionFile).build();
    assertThat(actual,
      isSimilarTo(expected).ignoreWhitespace().withComparisonFormatter(new DefaultComparisonFormatter())
    );
  }

  @Test
  public void addsRelation() throws Exception {
    testRdfDescriptionSaver.addRelation(BASE_URI,
      "http://purl.org/dc/terms/rightsHolder", "http://example.com/rightsHolder", BASE_URI);
    testRdfDescriptionSaver.commit();

    Source expected = Input.fromByteArray(
      (
        "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
          "  <rdf:Description rdf:about=\"http://example.org/datasets/DUMMY/clusius2\">\n" +
          "    <rightsHolder xmlns=\"http://purl.org/dc/terms/\" " +
          "       rdf:resource=\"http://example.com/rightsHolder\" /> \n" +
          "  </rdf:Description>\n" +
          "</rdf:RDF>\n"
      ).getBytes(StandardCharsets.UTF_8)
    ).build();


    Source actual = Input.fromFile(descriptionFile).build();
    assertThat(actual,
      isSimilarTo(expected).ignoreWhitespace().withComparisonFormatter(new DefaultComparisonFormatter())
    );
  }

  @Test
  public void addsMultipleRelation() throws Exception {
    testRdfDescriptionSaver.addRelation(BASE_URI,
      "http://purl.org/dc/terms/rightsHolder", "http://example.com/rightsHolder", BASE_URI);
    testRdfDescriptionSaver.addRelation(BASE_URI,
      "http://purl.org/dc/terms/abstract", "http://example.com/summaryProperties", BASE_URI);
    testRdfDescriptionSaver.addRelation(BASE_URI,
      "http://purl.org/dc/terms/provenance", "http://example.com/provenance", BASE_URI);
    testRdfDescriptionSaver.commit();

    Source expected = Input.fromByteArray(
      (
        "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
          "  <rdf:Description rdf:about=\"http://example.org/datasets/DUMMY/clusius2\">\n" +
          "    <abstract xmlns=\"http://purl.org/dc/terms/\" " +
          "       rdf:resource=\"http://example.com/summaryProperties\" /> \n" +
          "    <provenance xmlns=\"http://purl.org/dc/terms/\" rdf:resource=\"http://example.com/provenance\" /> \n" +
          "    <rightsHolder xmlns=\"http://purl.org/dc/terms/\" " +
          "       rdf:resource=\"http://example.com/rightsHolder\" /> \n" +
          "  </rdf:Description>\n" +
          "</rdf:RDF>\n"
      ).getBytes(StandardCharsets.UTF_8)
    ).build();

    Source actual = Input.fromFile(descriptionFile).build();
    assertThat(actual,
      isSimilarTo(expected).ignoreWhitespace().withComparisonFormatter(new DefaultComparisonFormatter())
    );
  }

  @Test
  public void doesNotAddDuplicateRelationToSamePredicate() throws Exception {
    testRdfDescriptionSaver.addRelation(BASE_URI,
      "http://purl.org/dc/terms/rightsHolder", "http://example.com/rightsHolder", BASE_URI);
    testRdfDescriptionSaver.addRelation(BASE_URI,
      "http://purl.org/dc/terms/rightsHolder", "http://example.com/rightsHolder", BASE_URI);
    testRdfDescriptionSaver.commit();

    Source expected = Input.fromByteArray(
      (
        "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
          "  <rdf:Description rdf:about=\"http://example.org/datasets/DUMMY/clusius2\">\n" +
          "    <rightsHolder xmlns=\"http://purl.org/dc/terms/\" " +
          "       rdf:resource=\"http://example.com/rightsHolder\" /> \n" +
          "  </rdf:Description>\n" +
          "</rdf:RDF>\n"
      ).getBytes(StandardCharsets.UTF_8)
    ).build();


    Source actual = Input.fromFile(descriptionFile).build();
    assertThat(actual,
      isSimilarTo(expected).ignoreWhitespace().withComparisonFormatter(new DefaultComparisonFormatter())
    );
  }


  @Test
  public void doesNotAddValueWhenSubjectUriDoesNotMatchBaseUri() throws Exception {
    testRdfDescriptionSaver.addValue("http://example.org/datasets/DUMMY/test",
      "http://purl.org/dc/terms/title", "DWC Data", null, BASE_URI);
    testRdfDescriptionSaver.commit();

    File file = new File(descriptionFileName);
    String descriptionFile = Files.toString(file, Charsets.UTF_8);

    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<rdf:RDF\n" +
      "\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
      "\n" +
      "</rdf:RDF>", descriptionFile);

  }

  @Test
  public void doesNotAddValueWhenSubjectNull() throws Exception {
    testRdfDescriptionSaver.addValue(null, "http://purl.org/dc/terms/title", "DWC Data", null, BASE_URI);
    testRdfDescriptionSaver.commit();

    File file = new File(descriptionFileName);
    String descriptionFile = Files.toString(file, Charsets.UTF_8);

    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<rdf:RDF\n" +
      "\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
      "\n" +
      "</rdf:RDF>", descriptionFile);
  }

  @Test
  public void doesNotAddValueForInvalidPredicate() throws Exception {
    testRdfDescriptionSaver.addValue(BASE_URI, "http://invalid.org/terms/title", "DWC Data", null, BASE_URI);
    testRdfDescriptionSaver.commit();

    File file = new File(descriptionFileName);
    String descriptionFile = Files.toString(file, Charsets.UTF_8);

    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<rdf:RDF\n" +
      "\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
      "\n" +
      "</rdf:RDF>", descriptionFile);
  }

  @Test
  public void doesNotAddRelationWhenSubjectUriDoesNotMatchBaseUri() throws Exception {
    testRdfDescriptionSaver.addRelation("http://example.org/datasets/DUMMY/test",
      "http://purl.org/dc/terms/title", "http://purl.org/dc/terms/rightsHolder", BASE_URI);
    testRdfDescriptionSaver.commit();

    File file = new File(descriptionFileName);
    String descriptionFile = Files.toString(file, Charsets.UTF_8);

    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<rdf:RDF\n" +
      "\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
      "\n" +
      "</rdf:RDF>", descriptionFile);
  }

  @Test
  public void doesNotAddRelationWhenSubjectNull() throws Exception {
    testRdfDescriptionSaver.addRelation(null,
      "http://purl.org/dc/terms/title", "http://purl.org/dc/terms/rightsHolder", BASE_URI);
    testRdfDescriptionSaver.commit();

    File file = new File(descriptionFileName);
    String descriptionFile = Files.toString(file, Charsets.UTF_8);

    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<rdf:RDF\n" +
      "\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
      "\n" +
      "</rdf:RDF>", descriptionFile);
  }

  @Test
  public void deletesRelation() throws Exception {
    testRdfDescriptionSaver.addRelation(BASE_URI,
      "http://purl.org/dc/terms/rightsHolder", "http://example.com/rightsHolder", BASE_URI);
    testRdfDescriptionSaver.commit();
    testRdfDescriptionSaver.delRelation(BASE_URI,
      "http://purl.org/dc/terms/rightsHolder", "http://example.com/rightsHolder", BASE_URI);
    testRdfDescriptionSaver.commit();

    Source expected = Input.fromByteArray(
      ("<rdf:RDF xmlns=\"http://purl.org/dc/terms/\"\n xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
        "</rdf:RDF>\n"
      ).getBytes(StandardCharsets.UTF_8)
    ).build();

    Source actual = Input.fromFile(descriptionFile).build();

    assertThat(actual,
      isSimilarTo(expected).ignoreWhitespace().withComparisonFormatter(new DefaultComparisonFormatter())
    );

  }

  @Test
  public void doesNotDeleteRelationWhenSubjectUriDoesNotMatchBaseUri() throws Exception {
    testRdfDescriptionSaver.addRelation(BASE_URI,
      "http://purl.org/dc/terms/rightsHolder", "http://example.com/rightsHolder", BASE_URI);
    testRdfDescriptionSaver.delRelation("http://example.org/datasets/DUMMY/test",
      "http://purl.org/dc/terms/title", "http://purl.org/dc/terms/rightsHolder", BASE_URI);
    testRdfDescriptionSaver.commit();

    Source expected = Input.fromByteArray(
      (
        "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
          "  <rdf:Description rdf:about=\"http://example.org/datasets/DUMMY/clusius2\">\n" +
          "    <rightsHolder xmlns=\"http://purl.org/dc/terms/\" " +
          "       rdf:resource=\"http://example.com/rightsHolder\" /> \n" +
          "  </rdf:Description>\n" +
          "</rdf:RDF>\n"
      ).getBytes(StandardCharsets.UTF_8)
    ).build();

    Source actual = Input.fromFile(descriptionFile).build();
    assertThat(actual,
      isSimilarTo(expected).ignoreWhitespace().withComparisonFormatter(new DefaultComparisonFormatter())
    );

  }

  @Test
  public void doesNotDeleteRelationWhenSubjectNull() throws Exception {
    testRdfDescriptionSaver.addRelation(BASE_URI,
      "http://purl.org/dc/terms/rightsHolder", "http://example.com/rightsHolder", BASE_URI);
    testRdfDescriptionSaver.delRelation(null,
      "http://purl.org/dc/terms/title", "http://purl.org/dc/terms/rightsHolder", BASE_URI);
    testRdfDescriptionSaver.commit();

    Source expected = Input.fromByteArray(
      (
        "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
          "  <rdf:Description rdf:about=\"http://example.org/datasets/DUMMY/clusius2\">\n" +
          "    <rightsHolder xmlns=\"http://purl.org/dc/terms/\" " +
          "       rdf:resource=\"http://example.com/rightsHolder\" /> \n" +
          "  </rdf:Description>\n" +
          "</rdf:RDF>\n"
      ).getBytes(StandardCharsets.UTF_8)
    ).build();

    Source actual = Input.fromFile(descriptionFile).build();
    assertThat(actual,
      isSimilarTo(expected).ignoreWhitespace().withComparisonFormatter(new DefaultComparisonFormatter())
    );

  }

  @Test
  public void deletesValue() throws Exception {
    testRdfDescriptionSaver.addValue(BASE_URI, "http://purl.org/dc/terms/title", "DWC Data", null, BASE_URI);
    testRdfDescriptionSaver.commit();
    testRdfDescriptionSaver.delValue(BASE_URI, "http://purl.org/dc/terms/title", "DWC Data", null, BASE_URI);
    testRdfDescriptionSaver.commit();

    Source expected = Input.fromByteArray(
      ("<rdf:RDF xmlns=\"http://purl.org/dc/terms/\"\n xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
        "</rdf:RDF>\n"
      ).getBytes(StandardCharsets.UTF_8)
    ).build();

    Source actual = Input.fromFile(descriptionFile).build();

    assertThat(actual,
      isSimilarTo(expected).ignoreWhitespace().withComparisonFormatter(new DefaultComparisonFormatter())
    );

  }

  @Test
  public void doesNotDeleteValueWhenSubjectNull() throws Exception {
    testRdfDescriptionSaver.addValue(BASE_URI, "http://purl.org/dc/terms/title", "DWC Data", null, BASE_URI);
    testRdfDescriptionSaver.delValue(null, "http://purl.org/dc/terms/title", "DWC Data", null, BASE_URI);
    testRdfDescriptionSaver.commit();

    Source expected = Input.fromByteArray(
      (
        "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
          ">\n" +
          "  <rdf:Description rdf:about=\"http://example.org/datasets/DUMMY/clusius2\">\n" +
          "    <title xmlns=\"http://purl.org/dc/terms/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">" +
          "DWC Data</title> \n" +
          "  </rdf:Description>\n" +
          "</rdf:RDF>\n"
      ).getBytes(StandardCharsets.UTF_8)
    ).build();

    Source actual = Input.fromFile(descriptionFile).build();
    assertThat(actual,
      isSimilarTo(expected).ignoreWhitespace().withComparisonFormatter(new DefaultComparisonFormatter())
    );
  }

  @Test
  public void doesNotDeleteValueWhenWhenSubjectUriDoesNotMatchBaseUri() throws Exception {
    testRdfDescriptionSaver.addValue(BASE_URI, "http://purl.org/dc/terms/title", "DWC Data", null, BASE_URI);
    testRdfDescriptionSaver.delValue("http://example.org/datasets/DUMMY/clusius10",
      "http://purl.org/dc/terms/title", "DWC Data", null, BASE_URI);
    testRdfDescriptionSaver.commit();

    Source expected = Input.fromByteArray(
      (
        "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
          ">\n" +
          "  <rdf:Description rdf:about=\"http://example.org/datasets/DUMMY/clusius2\">\n" +
          "    <title xmlns=\"http://purl.org/dc/terms/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">" +
          "DWC Data</title> \n" +
          "  </rdf:Description>\n" +
          "</rdf:RDF>\n"
      ).getBytes(StandardCharsets.UTF_8)
    ).build();

    Source actual = Input.fromFile(descriptionFile).build();
    assertThat(actual,
      isSimilarTo(expected).ignoreWhitespace().withComparisonFormatter(new DefaultComparisonFormatter())
    );
  }

  @Test
  public void replacesValue() throws Exception {
    testRdfDescriptionSaver.addValue(BASE_URI, "http://purl.org/dc/terms/title", "DWC Data", null, BASE_URI);
    testRdfDescriptionSaver.delValue(BASE_URI, "http://purl.org/dc/terms/title", "DWC Data", null, BASE_URI);
    testRdfDescriptionSaver.addValue(BASE_URI, "http://purl.org/dc/terms/title", "DWC Data New", null, BASE_URI);
    testRdfDescriptionSaver.commit();

    Source expected = Input.fromByteArray(
      (
        "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
          ">\n" +
          "  <rdf:Description rdf:about=\"http://example.org/datasets/DUMMY/clusius2\">\n" +
          "    <title xmlns=\"http://purl.org/dc/terms/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">" +
          "DWC Data New</title> \n" +
          "  </rdf:Description>\n" +
          "</rdf:RDF>\n"
      ).getBytes(StandardCharsets.UTF_8)
    ).build();

    Source actual = Input.fromFile(descriptionFile).build();
    assertThat(actual,
      isSimilarTo(expected).ignoreWhitespace().withComparisonFormatter(new DefaultComparisonFormatter())
    );
  }

}
