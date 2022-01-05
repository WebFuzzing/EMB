package nl.knaw.huygens.timbuctoo.model;

/*
 * #%L
 * Timbuctoo core
 * =======
 * Copyright (C) 2012 - 2015 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import nl.knaw.huygens.timbuctoo.model.PersonNameBuilder;
import nl.knaw.huygens.timbuctoo.model.PersonNameComponent;
import nl.knaw.huygens.timbuctoo.model.PersonNameComponent.Type;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;

public class PersonNameBuilderTest {

  @Test
  public void testSeparator() {
    assertThat(PersonNameBuilder.separator(null, null), isEmptyString());
    assertThat(PersonNameBuilder.separator(Type.FORENAME, null), isEmptyString());
    assertThat(PersonNameBuilder.separator(null, Type.SURNAME), isEmptyString());
    assertThat(PersonNameBuilder.separator(Type.FORENAME, Type.SURNAME), equalTo(" "));
    assertThat(PersonNameBuilder.separator(Type.SURNAME, Type.FORENAME), equalTo(", "));
  }

  @Test
  public void testOneComponent() {
    PersonNameBuilder builder = new PersonNameBuilder();
    builder.addComponent(new PersonNameComponent(Type.FORENAME, "Christiaan"));
    assertThat(builder.getName(), equalTo("Christiaan"));
  }

  @Test
  public void testTwoComponents() {
    PersonNameBuilder builder = new PersonNameBuilder();
    builder.addComponent(new PersonNameComponent(Type.FORENAME, "Christiaan"));
    builder.addComponent(new PersonNameComponent(Type.SURNAME, "Huygens"));
    assertThat(builder.getName(), equalTo("Christiaan Huygens"));
  }

  @Test
  public void testTwoComponentsReversed() {
    PersonNameBuilder builder = new PersonNameBuilder();
    builder.addComponent(new PersonNameComponent(Type.SURNAME, "Huygens"));
    builder.addComponent(new PersonNameComponent(Type.FORENAME, "Christiaan"));
    assertThat(builder.getName(), equalTo("Huygens, Christiaan"));
  }

}
