package nl.knaw.huygens.timbuctoo.server.tasks;

import io.dropwizard.servlets.tasks.Task;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.BdbWrapper;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.exceptions.BdbDbCreationException;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.exceptions.DatabaseWriteException;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.BdbTripleStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.BdbTruePatchStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.UpdatedPerPatchStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction.OUT;

public class ReplaceBlankNodeUrisTask extends Task {
  private static final Pattern BLANK_NODE = Pattern.compile("^BlankNode:(.*)/(.*)$");
  private final DataSetRepository dataSetRepository;

  public ReplaceBlankNodeUrisTask(DataSetRepository dataSetRepository) {
    super("replaceBlankNodeUris");
    this.dataSetRepository = dataSetRepository;
  }

  @Override
  public void execute(Map<String, List<String>> parameters, PrintWriter output) throws Exception {
    for (DataSet dataSet : dataSetRepository.getDataSets()) {
      output.println("Replace blank node URIs in dataset: " + dataSet.getMetadata().getCombinedId());
      output.flush();

      String baseUri = dataSet.getMetadata().getBaseUri();
      updateQuadStore((BdbTripleStore) dataSet.getQuadStore(), baseUri);
      updateTruePatchStore(dataSet.getTruePatchStore(), dataSet.getUpdatedPerPatchStore(), baseUri);
      updateUpdatedPerPatchStore(dataSet.getUpdatedPerPatchStore(), baseUri);

      output.println("Finished replacing blank node URIs in dataset: " + dataSet.getMetadata().getCombinedId());
      output.flush();
    }
  }

  private void updateQuadStore(BdbTripleStore quadStore, String baseUri) {
    quadStore
        .getAllQuads()
        .filter(cursorQuad ->
            cursorQuad.getSubject().startsWith("BlankNode") || cursorQuad.getObject().startsWith("BlankNode"))
        .forEach(cursorQuad -> {
          try {
            Tuple<String, String> newValues = withQuad(cursorQuad, baseUri);

            quadStore.deleteQuad(
                cursorQuad.getSubject(),
                cursorQuad.getPredicate(),
                cursorQuad.getDirection(),
                cursorQuad.getObject(),
                cursorQuad.getValuetype().orElse(null),
                cursorQuad.getLanguage().orElse(null)
            );

            quadStore.putQuad(
                newValues.getLeft(),
                cursorQuad.getPredicate(),
                cursorQuad.getDirection(),
                newValues.getRight(),
                cursorQuad.getValuetype().orElse(null),
                cursorQuad.getLanguage().orElse(null)
            );
          } catch (DatabaseWriteException e) {
            e.printStackTrace();
          }
        });

    quadStore.commit();
  }

  private void updateTruePatchStore(BdbTruePatchStore truePatchStore,
                                    UpdatedPerPatchStore updatedPerPatchStore, String baseUri) {
    updatedPerPatchStore.getVersions().forEach(version -> {
      try {
        BdbWrapper<String, String> bdbWrapper = truePatchStore.getOrCreateBdbWrapper(version);

        truePatchStore
            .getChangesOfVersion(version, true)
            .filter(cursorQuad ->
                cursorQuad.getSubject().startsWith("BlankNode") ||
                    cursorQuad.getObject().startsWith("BlankNode"))
            .forEach(cursorQuad -> {
              try {
                Tuple<String, String> newValues = withQuad(cursorQuad, baseUri);

                bdbWrapper.delete(
                    cursorQuad.getSubject() + "\n" + version + "\n" + 1,
                    cursorQuad.getPredicate() + "\n" +
                        (cursorQuad.getDirection() == OUT ? "1" : "0") + "\n" +
                        cursorQuad.getValuetype().orElse("") + "\n" +
                        cursorQuad.getLanguage().orElse("") + "\n" +
                        cursorQuad.getObject()
                );

                bdbWrapper.put(
                    newValues.getLeft() + "\n" + version + "\n" + 1,
                    cursorQuad.getPredicate() + "\n" +
                        (cursorQuad.getDirection() == OUT ? "1" : "0") + "\n" +
                        cursorQuad.getValuetype().orElse("") + "\n" +
                        cursorQuad.getLanguage().orElse("") + "\n" +
                        newValues.getRight()
                );
              } catch (DatabaseWriteException e) {
                e.printStackTrace();
              }
            });

        truePatchStore
            .getChangesOfVersion(version, false)
            .filter(cursorQuad ->
                cursorQuad.getSubject().startsWith("BlankNode") ||
                    cursorQuad.getObject().startsWith("BlankNode"))
            .forEach(cursorQuad -> {
              try {
                Tuple<String, String> newValues = withQuad(cursorQuad, baseUri);

                bdbWrapper.delete(
                    cursorQuad.getSubject() + "\n" + version + "\n" + 0,
                    cursorQuad.getPredicate() + "\n" +
                        (cursorQuad.getDirection() == OUT ? "1" : "0") + "\n" +
                        cursorQuad.getValuetype().orElse("") + "\n" +
                        cursorQuad.getLanguage().orElse("") + "\n" +
                        cursorQuad.getObject()
                );

                bdbWrapper.put(
                    newValues.getLeft() + "\n" + version + "\n" + 0,
                    cursorQuad.getPredicate() + "\n" +
                        (cursorQuad.getDirection() == OUT ? "1" : "0") + "\n" +
                        cursorQuad.getValuetype().orElse("") + "\n" +
                        cursorQuad.getLanguage().orElse("") + "\n" +
                        newValues.getRight()
                );
              } catch (DatabaseWriteException e) {
                e.printStackTrace();
              }
            });

        bdbWrapper.commit();
      } catch (BdbDbCreationException e) {
        e.printStackTrace();
      }
    });
  }

  private void updateUpdatedPerPatchStore(UpdatedPerPatchStore updatedPerPatchStore, String baseUri) {
    updatedPerPatchStore.getVersions().forEach(version -> {
      updatedPerPatchStore
          .ofVersion(version)
          .filter(subject -> subject.startsWith("BlankNode"))
          .forEach(subject -> {
            try {
              String newSubject = replaceBlankNodeUri(baseUri, subject);

              updatedPerPatchStore.delete(version, subject);
              updatedPerPatchStore.put(version, newSubject);
            } catch (DatabaseWriteException e) {
              e.printStackTrace();
            }
          });
    });

    updatedPerPatchStore.commit();
  }

  private static Tuple<String, String> withQuad(CursorQuad cursorQuad, String baseUri) {
    String subject = cursorQuad.getSubject();
    if (subject.startsWith("BlankNode")) {
      subject = replaceBlankNodeUri(baseUri, subject);
    }

    String object = cursorQuad.getObject();
    if (object.startsWith("BlankNode")) {
      object = replaceBlankNodeUri(baseUri, object);
    }

    return new Tuple<>(subject, object);
  }

  private static String replaceBlankNodeUri(String baseUri, String uri) {
    Matcher matcher = BLANK_NODE.matcher(uri);
    if (!matcher.matches()) {
      return uri;
    }
    return baseUri + ".well-known/genid/" + DigestUtils.md5Hex(matcher.group(1)) + "_" + matcher.group(2);
  }
}
