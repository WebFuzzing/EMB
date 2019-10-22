package org.devgateway.ocvn.persistence.mongo.reader;

import org.devgateway.ocds.persistence.mongo.Address;
import org.devgateway.ocds.persistence.mongo.ContactPoint;
import org.devgateway.ocds.persistence.mongo.Identifier;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.Organization.OrganizationType;
import org.devgateway.ocds.persistence.mongo.reader.ImportWarningRuntimeException;
import org.devgateway.ocds.persistence.mongo.reader.RowImporter;
import org.devgateway.ocds.persistence.mongo.repository.main.GenericOrganizationRepository;
import org.devgateway.ocds.persistence.mongo.spring.ImportService;
import org.devgateway.ocvn.persistence.mongo.dao.City;
import org.devgateway.ocvn.persistence.mongo.repository.main.CityRepository;

/**
 * @author mpostelnicu
 */
public abstract class OrganizationRowImporter<O extends Organization>
        extends RowImporter<O, String, GenericOrganizationRepository<O>> {

    protected final CityRepository cityRepository;

    public OrganizationRowImporter(final GenericOrganizationRepository<O> repository,
            final CityRepository cityRepository, final ImportService importService, final int skipRows) {
        super(repository, importService, skipRows);
        this.cityRepository = cityRepository;
    }

    /**
     * Adds an additional identifier to the current organization. If the
     * identifier is already present, it throws an exception. If there is a type
     * specified, it will accept a duplicate identifier if the types are
     * different, and in such case it only makes the organization of new type
     * 
     * @param organization
     * @param identifier
     * @param type
     */
    protected void addAditionalIdentifierOrFail(Organization organization, Identifier identifier,
            OrganizationType type) {
        if (containsIdentifier(organization, identifier)) {
            if (type == null || organization.getRoles().contains(type)) {
                throw new ImportWarningRuntimeException(
                        "Duplicate identifier " + identifier.getId() + " for organization " + organization);
            } else {
                organization.getRoles().add(type);
            }
        } else {
            organization.getAdditionalIdentifiers().add(identifier);
        }
    }

    protected void newCity(Organization organization, Integer cityId) {
        City city = cityRepository.findOne(cityId);
        if (city != null) {
            organization.getAddress().setPostalCode(city.getId().toString());
        }
    }

    protected Organization newOrganization(Organization organization, Identifier identifier, String name) {
        organization.setIdentifier(identifier);
        organization.setName(name);
        organization.setId(identifier.getId());
        organization.getAdditionalIdentifiers().add(identifier);
        return organization;
    }

    protected void newContactPoint(Organization organization, String name, String telephone, String fax, String email,
            String url) {
        ContactPoint cp = new ContactPoint();
        cp.setName(name);
        cp.setTelephone(telephone);
        cp.setFaxNumber(fax);
        cp.setEmail(email);
        cp.setUrl(url);
        organization.setContactPoint(cp);
    }

    protected void newAddress(Organization organization, String addressCell) {
        Address address = new Address();
        address.setStreetAddress(addressCell);
        organization.setAddress(address);
    }

    protected boolean containsIdentifier(Organization organization, Identifier identifier) {
        return organization.getAdditionalIdentifiers().contains(identifier);
    }

}
