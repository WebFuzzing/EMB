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

public class HuygensAuthenticationHandlerTest {
    //private static final String AUTHENTICATION_URL = "http://localhost:9000";
    //private static final String DEFAULT_SESSION_ID = "test";
    //private static final String PERSISTENT_ID = "111111333";
    //private static final String ORGANISATION = "Doe inc.";
    //private static final String SURNAME = "Doe";
    //private static final String GIVEN_NAME = "John";
    //private static final String EMAIL = "john@doe.com";
    //private static final String NAME = "John Doe";
    //private static final EnumSet<Affiliation> AFFILIATIONS = EnumSet.of(Affiliation.employee);
    //private static final String CREDENTIALS = "Huygens 9aweh80opgf";
    //private static final UUID TEST_SESSION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    //private Client client;
    //private HuygensAuthenticationHandler instance;
    //
    //@Before
    //public void setUp() {
    //    client = mock(Client.class);
    //    instance = new HuygensAuthenticationHandler(client, AUTHENTICATION_URL, CREDENTIALS);
    //}
    //
    //@After
    //public void tearDown() {
    //    client = null;
    //    instance = null;
    //}
    //
    //@Test
    //public void testClientResponseStatusDeprecatedEnumBecameInterface() throws UnauthorizedException {
    //    /* In Jersey 1.17.1 there is a single Status, it being in ClientResponse.java
    //     * In Jersey 1.18 a second Status was introduced in Response.java
    //     * Consequently equality tests using '==' on StatusType instances goes haywire.
    //     */
    //    ClientResponse response = createClientResponse(Response.Status.OK);
    //    when(response.getEntity(ClientSession.class)).thenReturn(createSecuritySession());
    //    Builder builder = setupGETResponse(response);
    //    setUpClient(builder);
    //
    //    SecurityInformation expected = createSecurityInformation();
    //
    //    SecurityInformation actual = instance.getSecurityInformation(DEFAULT_SESSION_ID);
    //    assertEquals(expected, actual);
    //}
    //
    //
    //@Test
    //public void testGetSecurityInformationExistingUser() throws UnauthorizedException {
    //    ClientResponse response = createClientResponse(Status.OK);
    //    when(response.getEntity(ClientSession.class)).thenReturn(createSecuritySession());
    //
    //    Builder builder = setupGETResponse(response);
    //    WebResource resource = setUpClient(builder);
    //
    //    SecurityInformation expected = createSecurityInformation();
    //
    //    SecurityInformation actual = instance.getSecurityInformation(DEFAULT_SESSION_ID);
    //    assertEquals(expected, actual);
    ///*
    // * 2 requests are executed
    // * A GET to retrieve the session.
    // * A PUT to refresh the session.
    // */
    //    verify(resource, times(2)).header(HttpHeaders.AUTHORIZATION, CREDENTIALS);
    //    verify(builder).put();
    //    verify(builder).get(ClientResponse.class);
    //
    //}
    //
    //@Test(expected = UnauthorizedException.class)
    //public void testGetSecurityInformationTokenNull() throws UnauthorizedException {
    //    instance.getSecurityInformation(null);
    //}
    //
    //@Test(expected = UnauthorizedException.class)
    //public void testGetSecurityInformationTokenEmpty() throws UnauthorizedException {
    //    instance.getSecurityInformation("");
    //}
    //
    //@Test(expected = UnauthorizedException.class)
    //public void testGetSecurityInformationSessionNotFound() throws UnauthorizedException {
    //    ClientResponse response = createClientResponse(Status.NOT_FOUND);
    //
    //    setUpClient(setupGETResponse(response));
    //
    //    instance.getSecurityInformation(DEFAULT_SESSION_ID);
    //}
    //
    //@Test(expected = UnauthorizedException.class)
    //public void testGetSecurityInformationIllegalSessionToken() throws UnauthorizedException {
    //    ClientResponse response = createClientResponse(Status.BAD_REQUEST);
    //    setUpClient(setupGETResponse(response));
    //
    //    instance.getSecurityInformation(DEFAULT_SESSION_ID);
    //}
    //
    //@Test(expected = UnauthorizedException.class)
    //public void testGetSecurityInformationWrongCredentials() throws UnauthorizedException {
    //    ClientResponse response = createClientResponse(Status.FORBIDDEN);
    //
    //    setUpClient(setupGETResponse(response));
    //
    //    instance.getSecurityInformation(DEFAULT_SESSION_ID);
    //}
    //
    //@Test(expected = UnauthorizedException.class)
    //public void testGetSecurityInformationSessionExpired() throws UnauthorizedException {
    //    ClientResponse response = createClientResponse(Status.GONE);
    //
    //    setUpClient(setupGETResponse(response));
    //
    //    instance.getSecurityInformation(DEFAULT_SESSION_ID);
    //}
    //
    //@Test
    //public void testLogout() throws UnauthorizedException {
    //    Builder builder = setupDELETEResource(Status.OK);
    //
    //    assertTrue(instance.logout(DEFAULT_SESSION_ID));
    //    verify(builder).delete(ClientResponse.class);
    //}
    //
    //@Test(expected = IllegalArgumentException.class)
    //public void testLogoutIllegalUUID() throws UnauthorizedException {
    //    Builder builder = setupDELETEResource(Status.BAD_REQUEST);
    //
    //    try {
    //        instance.logout(DEFAULT_SESSION_ID);
    //    } finally {
    //        verify(builder).delete(ClientResponse.class);
    //    }
    //}
    //
    //@Test
    //public void testLogoutSessionDoesNotExist() throws UnauthorizedException {
    //    Builder builder = setupDELETEResource(Status.NOT_FOUND);
    //
    //    assertTrue(instance.logout(DEFAULT_SESSION_ID));
    //    verify(builder).delete(ClientResponse.class);
    //}
    //
    //@Test
    //public void testLogoutSessionIsInActive() throws UnauthorizedException {
    //    Builder builder = setupDELETEResource(Status.GONE);
    //
    //    assertTrue(instance.logout(DEFAULT_SESSION_ID));
    //    verify(builder).delete(ClientResponse.class);
    //}
    //
    //@Test(expected = UnauthorizedException.class)
    //public void testLogoutWrongCredentials() throws UnauthorizedException {
    //    Builder builder = setupDELETEResource(Status.FORBIDDEN);
    //    try {
    //        instance.logout(DEFAULT_SESSION_ID);
    //    } finally {
    //        verify(builder).delete(ClientResponse.class);
    //    }
    //}
    //
    //private Builder setupDELETEResource(Status status) {
    //    ClientResponse response = createClientResponse(status);
    //
    //    Builder builder = setupDELETEResponse(response);
    //    setUpClient(builder);
    //
    //    return builder;
    //}
    //
    //private ClientResponse createClientResponse(StatusType statusType) {
    //    ClientResponse response = mock(ClientResponse.class);
    //    when(response.getStatusInfo()).thenReturn(statusType);
    //    return response;
    //}
    //
    //private WebResource setUpClient(Builder builder) {
    //    WebResource resource = mock(WebResource.class);
    //    when(resource.path(DEFAULT_SESSION_ID)).thenReturn(resource);
    //    when(resource.path(SESSION_AUTHENTICATION_PATH)).thenReturn(resource);
    //    when(resource.path(REFRESH_PATH)).thenReturn(resource);
    //    when(resource.path(TEST_SESSION_ID.toString())).thenReturn(resource);
    //    when(resource.header(HttpHeaders.AUTHORIZATION, CREDENTIALS)).thenReturn(builder);
    //
    //    when(client.resource(AUTHENTICATION_URL)).thenReturn(resource);
    //
    //    return resource;
    //
    //}
    //
    //private Builder setupDELETEResponse(ClientResponse response) {
    //    Builder builder = mock(Builder.class);
    //    when(builder.delete(ClientResponse.class)).thenReturn(response);
    //    return builder;
    //}
    //
    //private Builder setupGETResponse(ClientResponse response) {
    //    Builder builder = mock(Builder.class);
    //    when(builder.get(ClientResponse.class)).thenReturn(response);
    //    return builder;
    //}
    //
    //private ClientSession createSecuritySession() {
    //    final HuygensPrincipal principal = createSecurityPrincipal();
    //
    //    return new ClientSession() {
    //        @Override
    //        public HuygensPrincipal getOwner() {
    //            return principal;
    //        }
    //
    //        @Override
    //        public UUID getId() {
    //            return TEST_SESSION_ID;
    //        }
    //    };
    //}
    //
    //private HuygensPrincipal createSecurityPrincipal() {
    //    HuygensPrincipal principal = new HuygensPrincipal();
    //    principal.setAffiliations(AFFILIATIONS);
    //    principal.setCommonName(NAME);
    //    principal.setDisplayName(NAME);
    //    principal.setEmailAddress(EMAIL);
    //    principal.setGivenName(GIVEN_NAME);
    //    principal.setSurname(SURNAME);
    //    principal.setOrganization(ORGANISATION);
    //    principal.setPersistentID(PERSISTENT_ID);
    //    return principal;
    //}
    //
    //private SecurityInformation createSecurityInformation() {
    //    return new HuygensSecurityInformation(createSecurityPrincipal());
    //}
}
