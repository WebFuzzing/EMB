/**
 *
 */
package org.devgateway.ocvn.persistence.mongo.reader;

import java.math.BigDecimal;
import org.devgateway.ocds.persistence.mongo.Amount;
import org.devgateway.ocds.persistence.mongo.Award;
import org.devgateway.ocds.persistence.mongo.Detail;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.Tag;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.persistence.mongo.reader.ImportWarningRuntimeException;
import org.devgateway.ocds.persistence.mongo.reader.ReleaseRowImporter;
import org.devgateway.ocds.persistence.mongo.repository.main.OrganizationRepository;
import org.devgateway.ocds.persistence.mongo.repository.main.ReleaseRepository;
import org.devgateway.ocds.persistence.mongo.spring.ImportService;
import org.devgateway.ocvn.persistence.mongo.dao.VNPlanning;

/**
 * @author mpostelnicu
 */
public abstract class AwardReleaseRowImporter extends ReleaseRowImporter {

    protected OrganizationRepository organizationRepository;
    protected BigDecimal maxTenderValue;

    public AwardReleaseRowImporter(final ReleaseRepository releaseRepository, final ImportService importService,
                                   final OrganizationRepository organizationRepository,
                                   final int skipRows, BigDecimal maxTenderValue) {
        super(releaseRepository, importService, skipRows);
        this.organizationRepository = organizationRepository;
        this.maxTenderValue = maxTenderValue;
    }

    public Release newReleaseFromAwardFactory(String planningBidNo) {
        Release release = new Release();
        release.getTag().add(Tag.award);
        release.setOcid(MongoConstants.OCDS_PREFIX + "bidno-" + planningBidNo);
        VNPlanning planning = new VNPlanning();
        release.setPlanning(planning);
        planning.setBidNo(planningBidNo);
        return release;
    }

    public Detail newBidDetailFromAwardData(String id, Amount amount, Organization tenderer) {
        Detail detail = new Detail();
        detail.setValue(amount);
        detail.getTenderers().add(tenderer);
        detail.setId(id);
        return detail;
    }

    /**
     * see OCVN-283
     * <p>
     * We should not allow import of awards records where award value
     * (BID_PRICE_SUCC) is more than 4x the value of tender value (ESTI_PRICE).
     * Import validation should reject these records and generate a log. Note
     * that this does not apply for records where the tender value (ESTI_PRICE)
     * is null or 0.
     *
     * @param release
     * @param award
     */
    public void checkForAwardOutliers(Release release, Award award) {
        if (release.getTender().getValue() != null && award.getValue() != null
                && !release.getTender().getValue().getAmount().equals(BigDecimal.ZERO) && release.getTender().getValue()
                .getAmount().multiply(BigDecimal.valueOf(4d)).compareTo(award.getValue().getAmount()) < 0) {
            throw new ImportWarningRuntimeException("Award value is more than 4x larger than the tender value!");
        }

        if ((release.getTender().getValue() == null
                || release.getTender().getValue().getAmount().equals(BigDecimal.ZERO)) && award.getValue() != null
                && maxTenderValue.multiply(BigDecimal.valueOf(4d)).compareTo(award.getValue().getAmount()) < 0) {
            throw new ImportWarningRuntimeException(
                    "Award value is more than 4x larger than the largest tender value!");
        }
    }

}
