package nl.knaw.huygens.timbuctoo.model;

public class PersonNameStubs {
  public static PersonName forename(String forename) {
    PersonName personName = new PersonName();
    personName.addNameComponent(PersonNameComponent.Type.FORENAME, forename);
    return personName;
  }

  public static PersonName surname(String surname) {
    PersonName personName = new PersonName();
    personName.addNameComponent(PersonNameComponent.Type.SURNAME, surname);
    return personName;
  }
}
