package nl.knaw.huygens.timbuctoo.search.description.propertyparser;

import nl.knaw.huygens.timbuctoo.model.Change;
import nl.knaw.huygens.timbuctoo.model.CollectiveType;
import nl.knaw.huygens.timbuctoo.model.Datable;
import nl.knaw.huygens.timbuctoo.model.DocumentType;
import nl.knaw.huygens.timbuctoo.model.Gender;
import nl.knaw.huygens.timbuctoo.model.LocationNames;
import nl.knaw.huygens.timbuctoo.model.PersonNames;
import nl.knaw.huygens.timbuctoo.model.TempName;
import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;

import java.util.List;

public class PropertyParserFactory {
  public PropertyParser getParser(Class<?> type) {

    if (String.class.isAssignableFrom(type)) {
      return new StringPropertyParser();
    } else if (Change.class.isAssignableFrom(type)) {
      return new ChangeDatePropertyParser();
    } else if (Datable.class.isAssignableFrom(type)) {
      return new DatableFromYearPropertyParser();
    } else if (Gender.class.isAssignableFrom(type)) {
      return new GenderPropertyParser();
    } else if (PersonNames.class.isAssignableFrom(type)) {
      return new PersonNamesDefaultNamePropertyParser();
    } else if (LocationNames.class.isAssignableFrom(type)) {
      return new DefaultLocationNamePropertyParser();
    } else if (CollectiveType.class.isAssignableFrom(type)) {
      return new CollectiveTypePropertyParser();
    } else if (DocumentType.class.isAssignableFrom(type)) {
      return new DocumentTypePropertyParser();
    } else if (TempName.class.isAssignableFrom(type)) {
      return new TempNamePropertyParser();
    } else if (List.class.isAssignableFrom(type)) {
      return new StringListParser();
    }
    throw new UnparsableTypeException("Type " + type + " is not supported");
  }

  public PropertyParser getJoinedListParser(String seperator) {
    return new StringListParser(seperator);
  }
}
