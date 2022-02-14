package nl.knaw.huygens.security.client;

/*
 * #%L
 * Security Client
 * =======
 * Copyright (C) 2013 - 2014 Huygens ING
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

import nl.knaw.huygens.security.client.model.HuygensSecurityInformation;
import nl.knaw.huygens.security.client.model.SecurityInformation;
import nl.knaw.huygens.security.core.model.Affiliation;

import java.security.Principal;
import java.util.EnumSet;

/**
 * A mock implementation for the {@code AuthorizationHandler} interface.
 * @author martijnm
 *
 */
public class MockAuthenticationHandler implements AuthenticationHandler {

  @Override
  public SecurityInformation getSecurityInformation(String sessionId) throws UnauthorizedException {
    HuygensSecurityInformation securityInformation = new HuygensSecurityInformation();
    securityInformation.setAffiliations(EnumSet.of(Affiliation.employee));
    securityInformation.setCommonName("John Doe");
    securityInformation.setDisplayName("John Doe");
    securityInformation.setEmailAddress("john@doe.com");
    securityInformation.setGivenName("John");
    securityInformation.setSurname("Doe");
    securityInformation.setOrganization("Doe inc.");
    securityInformation.setPersistentID("111111333");
    securityInformation.setPrincipal(new Principal() {

      @Override
      public String getName() {
        return "John Doe";
      }
    });

    return securityInformation;
  }
}
