package org.devgateway.ocvn.persistence.mongo.reader;

import java.math.BigDecimal;
import java.text.ParseException;
import org.devgateway.ocds.persistence.mongo.Amount;
import org.devgateway.ocds.persistence.mongo.Award;
import org.devgateway.ocds.persistence.mongo.Detail;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.Tender;
import org.devgateway.ocds.persistence.mongo.reader.RowImporter;
import org.devgateway.ocds.persistence.mongo.repository.main.OrganizationRepository;
import org.devgateway.ocds.persistence.mongo.repository.main.ReleaseRepository;
import org.devgateway.ocds.persistence.mongo.spring.ImportService;
import org.devgateway.ocvn.persistence.mongo.dao.VNAward;
import org.devgateway.ocvn.persistence.mongo.dao.VNTender;
import org.devgateway.ocvn.persistence.mongo.dao.VNTendererOrganization;
import org.devgateway.ocvn.persistence.mongo.reader.util.OrganizationRepositoryUtil;

/**
 * Specific {@link RowImporter} for Offline Awards, in the custom Excel format
 * provided by Vietnam
 *
 * @author mpostelnicu
 * @see VNAward
 */
public class OfflineAwardRowImporter extends AwardReleaseRowImporter {

    public OfflineAwardRowImporter(ReleaseRepository releaseRepository, ImportService importService,
                                   OrganizationRepository organizationRepository,
                                   int skipRows, BigDecimal maxTenderValue) {
        super(releaseRepository, importService, organizationRepository, skipRows, maxTenderValue);
    }

    @Override
    public Release createReleaseFromReleaseRow(final String[] row) throws ParseException {

        Release release = repository.findByPlanningBidNo(getRowCell(row, 0));

        if (release == null) {
            release = newReleaseFromAwardFactory(getRowCell(row, 0));
        }

        if (release.getTender() == null) {
            VNTender tender = new VNTender();
            tender.setId(release.getOcid());
            release.setTender(tender);
        }

        release.getTender().getSubmissionMethod().add(Tender.SubmissionMethod.written);

        VNAward award = new VNAward();
        award.setId(release.getOcid() + "-award-" + release.getAwards().size());

        release.getAwards().add(award);

        award.setTitle(getRowCell(row, 1));

        if (getRowCell(row, 2) != null) {
            Amount value = new Amount();
            value.setCurrency("VND");
            value.setAmount(getDecimal(getRowCell(row, 2)));
            award.setValue(value);
        }

        Organization supplier = null;
        if (getRowCell(row, 3) != null) {

            supplier = organizationRepository.findByName(getRowCellUpper(row, 3));

            if (supplier == null) {
                supplier = OrganizationRepositoryUtil.newAndInsertOrganization(Organization.OrganizationType.supplier,
                        getRowCellUpper(row, 3), organizationRepository);
            } else {
                supplier = OrganizationRepositoryUtil.ensureOrgIsOfTypeAndSave(supplier,
                        Organization.OrganizationType.supplier, organizationRepository);
            }
        }

        award.setStatus("Y".equals(getRowCell(row, 5)) ? Award.Status.active : Award.Status.unsuccessful);

        // active=successful awards have suppliers
        if (supplier != null && Award.Status.active.equals(award.getStatus())) {
            award.getSuppliers().add(supplier);
        }

        award.setContractTime(getRowCell(row, 4));

        award.setIneligibleYN(getRowCell(row, 6));

        award.setIneligibleRson(getRowCell(row, 7));

        award.setBidType(getInteger(getRowCell(row, 8)));

        award.setBidSuccMethod(getInteger(getRowCell(row, 9)));

        Organization supplierOrganization = supplier;
        Detail detail = null;
        if (supplierOrganization != null && getRowCell(row, 10) != null) {
            Amount value2 = new Amount();
            value2.setCurrency("VND");
            value2.setAmount(getDecimal(getRowCell(row, 10)));
            VNTendererOrganization tendererOrganization = new VNTendererOrganization(supplier);
            tendererOrganization.setBidValue(value2);
            supplierOrganization = tendererOrganization;
            detail = newBidDetailFromAwardData(getRowCell(row, 0), value2, supplier);
        }

        if (getRowCell(row, 12) != null) {
            award.setDate(getExcelDate(getRowCell(row, 12)));
            award.setPublishedDate(getExcelDate(getRowCell(row, 12)));
        }

        if (getRowCell(row, 11) != null) {
            award.setAlternateDate(getExcelDate(getRowCell(row, 11)));
            detail.setDate(award.getAlternateDate());
        }

        // regardless if the award is active or not, we add the supplier to
        // tenderers
        if (supplierOrganization != null) {
            release.getTender().getTenderers().add(supplierOrganization);
        }

        release.getTender().setNumberOfTenderers(release.getTender().getTenderers().size());

        if (detail != null) {
            release.getBids().getDetails().add(detail);
        }

        // copy items from tender
        award.getItems().addAll(release.getTender().getItems());

        checkForAwardOutliers(release, award);

        return release;
    }
}
