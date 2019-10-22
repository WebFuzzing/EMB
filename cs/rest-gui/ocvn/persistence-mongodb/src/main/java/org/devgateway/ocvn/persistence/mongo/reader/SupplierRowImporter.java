package org.devgateway.ocvn.persistence.mongo.reader;

import java.text.ParseException;
import org.devgateway.ocds.persistence.mongo.Identifier;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.reader.RowImporter;
import org.devgateway.ocds.persistence.mongo.repository.main.OrganizationRepository;
import org.devgateway.ocds.persistence.mongo.spring.ImportService;
import org.devgateway.ocvn.persistence.mongo.dao.VNOrganization;
import org.devgateway.ocvn.persistence.mongo.repository.main.CityRepository;

/**
 * @author mpostelnicu Specific {@link RowImporter} for Suppliers, in the custom
 *         Excel format provided by Vietnam
 * @see VNOrganization
 */
public class SupplierRowImporter extends OrganizationRowImporter<Organization> {

    public SupplierRowImporter(final OrganizationRepository repository, final CityRepository cityRepository,
                               final ImportService importService, final int skipRows) {
        super(repository, cityRepository, importService, skipRows);
    }

    @Override
    public void importRow(final String[] row) throws ParseException {
        if (getRowCell(row, 0) == null) {
            throw new RuntimeException("Main identifier empty!");
        }

        String newIdentifier = getRowCellUpper(row, 0);
        String name = getRowCellUpper(row, 2);

        Organization organization = repository.findByName(name);
        Identifier identifier = new Identifier();
        identifier.setId(newIdentifier);

        if (organization == null) {
            organization = newOrganization(new Organization(), identifier, name);
            newAddress(organization, getRowCell(row, 18));

            newContactPoint(organization, null, getRowCell(row, 20), getRowCell(row, 21), null, getRowCell(row, 22));

            if (getRowCell(row, 17) != null) {
                newCity(organization, getInteger(getRowCell(row, 17)));
            }

        } else {
            addAditionalIdentifierOrFail(organization, identifier, Organization.OrganizationType.supplier);
        }

        organization.getRoles().add(Organization.OrganizationType.supplier);

        repository.save(organization);

    }

}
