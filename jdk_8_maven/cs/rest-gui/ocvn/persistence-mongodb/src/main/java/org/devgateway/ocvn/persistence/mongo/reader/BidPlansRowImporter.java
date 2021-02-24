package org.devgateway.ocvn.persistence.mongo.reader;

import java.text.ParseException;
import org.devgateway.ocds.persistence.mongo.Amount;
import org.devgateway.ocds.persistence.mongo.Budget;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.Tag;
import org.devgateway.ocds.persistence.mongo.Tender;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.persistence.mongo.reader.ReleaseRowImporter;
import org.devgateway.ocds.persistence.mongo.reader.RowImporter;
import org.devgateway.ocds.persistence.mongo.repository.main.ReleaseRepository;
import org.devgateway.ocds.persistence.mongo.spring.ImportService;
import org.devgateway.ocvn.persistence.mongo.dao.VNItem;
import org.devgateway.ocvn.persistence.mongo.dao.VNPlanning;
import org.devgateway.ocvn.persistence.mongo.dao.VNTender;

/**
 * @author mpostelnicu Specific {@link RowImporter} for Bid Plans in the custom Excel
 *         format provided by Vietnam
 * @see VNPlanning
 */
public class BidPlansRowImporter extends ReleaseRowImporter {

    public BidPlansRowImporter(final ReleaseRepository releaseRepository, final ImportService importService,
                               final int skipRows) {
        super(releaseRepository, importService, skipRows);
    }

    @Override
    public Release createReleaseFromReleaseRow(final String[] row) throws ParseException {

        String projectID = getRowCell(row, 0);
        Release release = repository.findByBudgetProjectId(projectID);

        if (release == null) {
            release = new Release();
            release.getTag().add(Tag.planning);
            release.setOcid(MongoConstants.OCDS_PREFIX + "prjid-" + projectID);
            VNPlanning planning = new VNPlanning();
            release.setPlanning(planning);
        }

        Budget budget = release.getPlanning().getBudget();
        if (budget == null) {
            budget = new Budget();
            release.getPlanning().setBudget(budget);
        }
        budget.setProjectID(getRowCell(row, 0));

        Amount value = new Amount();
        value.setCurrency("VND");
        budget.setAmount(value);

        // decimal2
        value.setAmount(getDecimal(getRowCell(row, 5)));

        Tender tender = release.getTender();
        if (tender == null) {
            tender = new VNTender();
            tender.setId(release.getOcid());
            release.setTender(tender);
        }

        // create Items
        VNItem item = new VNItem();
        item.setId(Integer.toString(tender.getItems().size()));
        tender.getItems().add(item);

        // decimal2
        value.setAmount(getDecimal(getRowCell(row, 5)));
        item.setDescription(getRowCell(row, 1));
        item.setBidPlanItemRefNum(getRowCell(row, 2));
        item.setBidPlanItemStyle(getRowCell(row, 3));
        item.setBidPlanItemFund(getRowCell(row, 4));
        item.setBidPlanItemMethodSelect(getRowCell(row, 6));
        item.setBidPlanItemMethod(getRowCell(row, 7));
        item.setId(getRowCell(row, 8));

        return release;
    }
}
