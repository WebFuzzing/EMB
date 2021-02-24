package org.devgateway.ocvn.persistence.mongo.reader.util;

import org.devgateway.ocds.persistence.mongo.Identifier;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.Organization.OrganizationType;
import org.devgateway.ocds.persistence.mongo.repository.main.OrganizationRepository;

public final class OrganizationRepositoryUtil {

    private OrganizationRepositoryUtil() {

    }

    /**
     * Factory for organizations where name and id are sadly the same, because
     * we dont know enough info about them
     * 
     * @param type
     * @param nameId
     *            name and id of the org
     * @return the created org
     */
    public static Organization newAndInsertOrganization(Organization.OrganizationType type, String nameId,
            OrganizationRepository repository) {
        return newAndInsertOrganization(type, nameId, nameId, repository);
    }

    /**
     * {@link Organization} factory, creating one organization and label it as
     * {@link Organization.OrganizationType}
     * 
     * @param type
     *            the type
     * @param name
     *            the org name
     * @param id
     *            the org id
     * @return the created organization
     */
    public static Organization newAndInsertOrganization(Organization.OrganizationType type, String name, String id,
            OrganizationRepository repository) {
        Organization org = new Organization();
        org.getRoles().add(type);
        org.setName(name);
        org.setId(id);
        Identifier identifier = new Identifier();
        identifier.setId(id);
        org.setIdentifier(identifier);
        org.getAdditionalIdentifiers().add(identifier);
        return repository.insert(org);
    }

    /**
     * Adds the specified type to the {@link Organization}, if not already
     * present
     * 
     * @param org
     *            the {@link Organization}
     * @param type
     *            the new {@link OrganizationType} to be added
     * @return the modified org
     */
    public static Organization ensureOrgIsOfTypeAndSave(Organization org, Organization.OrganizationType type,
            OrganizationRepository repository) {
        if (!org.getRoles().contains(type)) {
            org.getRoles().add(Organization.OrganizationType.procuringEntity);
            return repository.save(org);
        }
        return org;
    }

}
