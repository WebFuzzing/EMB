package org.devgateway.ocds.web.rest.controller;

import java.io.IOException;
import java.util.List;
import org.devgateway.ocds.persistence.mongo.Address;
import org.devgateway.ocds.persistence.mongo.ContactPoint;
import org.devgateway.ocds.persistence.mongo.Identifier;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.repository.main.OrganizationRepository;
import org.devgateway.ocds.web.rest.controller.request.TextSearchRequest;
import org.devgateway.ocds.web.rest.controller.selector.BuyerSearchController;
import org.devgateway.ocds.web.rest.controller.selector.OrganizationSearchController;
import org.devgateway.ocds.web.rest.controller.selector.ProcuringEntitySearchController;
import org.devgateway.ocds.web.rest.controller.selector.SupplierSearchController;
import org.devgateway.toolkit.web.AbstractWebTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrganizationEndpointsTest extends AbstractWebTest {

    @Autowired
    private OrganizationSearchController organizationSearchController;

    @Autowired
    private ProcuringEntitySearchController procuringEntitySearchController;

    @Autowired
    private BuyerSearchController buyerSearchController;

    @Autowired
    private SupplierSearchController supplierSearchController;

    @Autowired
    private OrganizationRepository organizationRepository;

    private static final String ORG_ID = "1234";

    @Before
    public void importTestData() throws IOException, InterruptedException {
        organizationRepository.deleteAll();

        final Organization organization = new Organization();
        organization.setName("Development Gateway");
        organization.setId(ORG_ID);

        final Address address = new Address();
        address.setCountryName("Romania");
        address.setLocality("Bucuresti");
        address.setPostalCode("022671");
        address.setRegion("Bucuresti");
        address.setStreetAddress("7 Sos. Iancului");
        organization.setAddress(address);

        final ContactPoint contactPoint = new ContactPoint();
        contactPoint.setEmail("mpostelnicu@developmentgateway.org");
        contactPoint.setFaxNumber("01234567");
        contactPoint.setTelephone("01234567");
        contactPoint.setUrl("http://developmentgateway.org");
        organization.setContactPoint(contactPoint);

        final Identifier identifier = new Identifier();
        identifier.setId(ORG_ID);
        organization.getAdditionalIdentifiers().add(identifier);
        organization.getRoles().add(Organization.OrganizationType.procuringEntity);
        organization.getRoles().add(Organization.OrganizationType.buyer);

        final Organization savedOrganization = organizationRepository.save(organization);

        Assert.assertNotNull(savedOrganization);
        Assert.assertEquals(ORG_ID, savedOrganization.getId());
    }

    @Test
    public void testOrganizationIdEndpoint() {
        final Organization organizationId = organizationSearchController.byId(ORG_ID);
        Assert.assertNotNull(organizationId);
    }

    @Test
    public void testOrganizationSearchText() {
        final TextSearchRequest osr = new TextSearchRequest();
        osr.setText("Development");
        final List<Organization> organizations = organizationSearchController.searchText(osr);
        Assert.assertEquals(1, organizations.size(), 0);
    }

    @Test
    public void testProcuringEntityIdEndpoint() {
        final Organization organizationId = procuringEntitySearchController.byId(ORG_ID);
        Assert.assertNotNull(organizationId);
    }

    @Test
    public void testProcuringEntitySearchText() {
        final TextSearchRequest osr = new TextSearchRequest();
        osr.setText("Development");
        final List<Organization> organizations = procuringEntitySearchController.searchText(osr);
        Assert.assertEquals(1, organizations.size(), 0);
    }

    @Test
    public void testBuyerIdEndpoint() {
        final Organization organizationId = buyerSearchController.byId(ORG_ID);
        Assert.assertNotNull(organizationId);
    }

    @Test
    public void testBuyerSearchText() {
        final TextSearchRequest osr = new TextSearchRequest();
        osr.setText("Development");
        final List<Organization> organizations = buyerSearchController.searchText(osr);
        Assert.assertEquals(1, organizations.size(), 0);
    }

    @Test
    public void testSupplierIdEndpoint() {
        final Organization organizationId = supplierSearchController.byId(ORG_ID);
        Assert.assertNull(organizationId);
    }

    @Test
    public void testSupplierSaerchText() {
        final TextSearchRequest osr = new TextSearchRequest();
        osr.setText("Development");
        final List<Organization> organizations = supplierSearchController.searchText(osr);
        Assert.assertEquals(0, organizations.size(), 0);
    }

}