package nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine;

import java.util.function.Consumer;

public class ImporterStubs {
  public static Importer withCustomReporter(Consumer<String> reporter) {
    return new Importer(StateMachineStubs.dummy(), new ResultReporter(reporter));
  }
}
