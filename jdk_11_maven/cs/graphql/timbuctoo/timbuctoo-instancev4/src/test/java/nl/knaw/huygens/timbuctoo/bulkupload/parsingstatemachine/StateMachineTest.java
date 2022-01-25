package nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine;

import nl.knaw.huygens.timbuctoo.bulkupload.savers.Saver;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InOrder;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StateMachineTest {

  private static final String COLLECTION_NAME = "collectionName";

  @Test
  public void startEntitySavesThePropertyDescriptionsOnce() {
    Saver saver = mock(Saver.class);
    StateMachine instance = new StateMachine(saver);

    instance.startCollection(COLLECTION_NAME);
    verify(saver, never()).addPropertyDescriptions(any(Vertex.class), any(ImportPropertyDescriptions.class));

    instance.startEntity();
    instance.finishEntity();
    instance.startEntity();
    instance.finishEntity();
    verify(saver, times(1)).addPropertyDescriptions(any(), any(ImportPropertyDescriptions.class));
  }

  @Test
  public void importsEveryEntityForACollection() {
    Saver saver = mock(Saver.class);
    StateMachine instance = new StateMachine(saver);

    instance.startCollection(COLLECTION_NAME);
    instance.registerPropertyName(1, "name");
    instance.startEntity();
    instance.setValue(1, "a");
    instance.finishEntity();
    instance.startEntity();
    instance.setValue(1, "a");
    instance.finishEntity();
    instance.finishCollection();

    InOrder inOrder = inOrder(saver);
    inOrder.verify(saver).addCollection(COLLECTION_NAME);
    inOrder.verify(saver, times(2)).addEntity(any(), any(/*hashmap*/));
  }

  @Test
  public void setsKnownPropertyOnTheEntity() {
    Saver saver = mock(Saver.class);
    StateMachine instance = new StateMachine(saver);

    instance.startCollection(COLLECTION_NAME);
    instance.registerPropertyName(1, "test");
    instance.registerPropertyName(2, "test2");
    instance.startEntity();
    String value1 = "value1";
    instance.setValue(1, value1);
    String value2 = "value2";
    instance.setValue(2, value2);
    instance.finishEntity();
    instance.finishCollection();

    verify(saver).addEntity(any(), argThat(allOf(
      hasEntry("test", (Object) value1),
      hasEntry("test2", (Object) value2)
    )));
  }

  @Test
  public void ignoresPropertiesWithoutValue() {
    Saver saver = mock(Saver.class);
    StateMachine instance = new StateMachine(saver);

    instance.startCollection(COLLECTION_NAME);
    instance.registerPropertyName(1, "test");
    instance.registerPropertyName(2, "test2");
    instance.startEntity();
    String value1 = "value1";
    instance.setValue(1, value1);
    instance.finishEntity();
    instance.finishCollection();

    verify(saver).addEntity(any(), argThat(allOf(hasEntry("test", (Object) value1))));
  }

  @Test
  @Ignore
  public void recordsEachImportError() {
    fail("Yet to implemented.");
  }
}
