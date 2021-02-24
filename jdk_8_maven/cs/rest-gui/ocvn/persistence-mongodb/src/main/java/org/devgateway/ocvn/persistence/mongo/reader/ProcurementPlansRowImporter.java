package org.devgateway.ocvn.persistence.mongo.reader;

import java.text.ParseException;
import org.devgateway.ocds.persistence.mongo.Amount;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.Tag;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.persistence.mongo.reader.ReleaseRowImporter;
import org.devgateway.ocds.persistence.mongo.reader.RowImporter;
import org.devgateway.ocds.persistence.mongo.repository.main.ReleaseRepository;
import org.devgateway.ocds.persistence.mongo.spring.ImportService;
import org.devgateway.ocvn.persistence.mongo.dao.VNBudget;
import org.devgateway.ocvn.persistence.mongo.dao.VNLocation;
import org.devgateway.ocvn.persistence.mongo.dao.VNPlanning;
import org.devgateway.ocvn.persistence.mongo.repository.main.VNLocationRepository;

/**
 *
 * @author mpostelnicu Specific {@link RowImporter} for Procurement Plans, in the
 *         custom Excel format provided by Vietnam
 * @see VNPlanning
 */
public class ProcurementPlansRowImporter extends ReleaseRowImporter {
    private VNLocationRepository locationRepository;

    public ProcurementPlansRowImporter(final ReleaseRepository releaseRepository, final ImportService importService,
                                       final VNLocationRepository locationRepository, final int skipRows) {
        super(releaseRepository, importService, skipRows);
        this.locationRepository = locationRepository;
    }

    @Override
    public Release createReleaseFromReleaseRow(final String[] row) throws ParseException {

        String projectID = getRowCell(row, 0);
        Release oldRelease = repository.findByBudgetProjectId(projectID);
        if (oldRelease != null) {
            throw new RuntimeException("Duplicate planning.budget.projectID");
        }

        Release release = new Release();
        release.setOcid(MongoConstants.OCDS_PREFIX + "prjid-" + projectID);
        release.getTag().add(Tag.planning);
        VNPlanning planning = new VNPlanning();
        VNBudget budget = new VNBudget();
        release.setPlanning(planning);
        planning.setBudget(budget);

        // search for locations
        if (getRowCell(row, 3) != null) {
            String[] locations = getRowCell(row, 3).split(",");
            for (int i = 0; i < locations.length; i++) {
                VNLocation location = locationRepository.findByDescription(locations[i].trim());
                if (location == null) {
                    location = new VNLocation();
                    location.setDescription(locations[i]);
                    location = locationRepository.insert(location);
                }

                budget.getProjectLocation().add(location);
            }
        }

        planning.setBidPlanProjectDateIssue(getExcelDate(getRowCell(row, 4)));

        planning.setBidPlanProjectCompanyIssue(getRowCell(row, 6));

        planning.setBidPlanProjectFund(getInteger(getRowCell(row, 8)));
        budget.getProjectClassification().setDescription(getRowCell(row, 9));

        planning.setBidPlanProjectDateApprove(getExcelDate(getRowCell(row, 10)));
        budget.getProjectClassification().setId(getRowCell(row, 12));
        planning.setBidNo(getRowCell(row, 13));

        budget.setProjectID(getRowCell(row, 0));
        budget.setBidPlanProjectStyle(getRowCell(row, 5));
        budget.setBidPlanProjectType(getRowCell(row, 7));
        budget.setProject(getRowCell(row, 1));
        budget.setDescription(getRowCell(row, 11));

        Amount value = new Amount();
        budget.setProjectAmount(value);
        value.setCurrency("VND");
        value.setAmount(getDecimal(getRowCell(row, 2)));
        return release;
    }
}
