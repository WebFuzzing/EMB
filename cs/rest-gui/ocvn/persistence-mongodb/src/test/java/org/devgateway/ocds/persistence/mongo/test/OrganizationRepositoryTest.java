package org.devgateway.ocds.persistence.mongo.test;

import java.io.IOException;

import org.devgateway.ocds.persistence.mongo.Address;
import org.devgateway.ocds.persistence.mongo.ContactPoint;
import org.devgateway.ocds.persistence.mongo.Identifier;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.repository.main.OrganizationRepository;
import org.devgateway.toolkit.persistence.mongo.AbstractMongoTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrganizationRepositoryTest extends AbstractMongoTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    private static final String ORG_ID = "1234";

    @Before
    public void importTestData() throws IOException, InterruptedException {
        // be sure that the organization collection is empty
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
        organization.getAdditionalIdentifiers().add(identifier);
        organization.getRoles().add(Organization.OrganizationType.procuringEntity);
        organization.getRoles().add(Organization.OrganizationType.buyer);

        final Organization savedOrganization = organizationRepository.save(organization);

        Assert.assertNotNull(savedOrganization);
        Assert.assertEquals(ORG_ID, savedOrganization.getId());
    }


    @Test
    public void testOrganizationSaveAndFind() {
        final Organization foundOrg = organizationRepository.findOne(ORG_ID);
        Assert.assertNotNull(foundOrg);

        final Organization foundOrg2 = organizationRepository.findByIdOrNameAndTypes(ORG_ID,
                Organization.OrganizationType.procuringEntity);
        Assert.assertNotNull(foundOrg2);

        final Organization foundOrg3 = organizationRepository.findByIdOrNameAndTypes(ORG_ID,
                Organization.OrganizationType.supplier);
        Assert.assertNull(foundOrg3);

        final Organization foundOrg4 = organizationRepository.findByIdOrNameAllIgnoreCase(ORG_ID, ORG_ID);
        Assert.assertNotNull(foundOrg4);
    }

}
