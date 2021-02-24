package org.devgateway.ocds.persistence.mongo.flags.preconditions;


import org.devgateway.ocds.persistence.mongo.Award;
import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.Tender;

/**
 * @author mpostelnicu
 */
public final class FlaggedReleasePredicates {

    private FlaggedReleasePredicates() {

    }

    public static final NamedPredicate<FlaggedRelease> TENDER_START_DATE = new NamedPredicate<>(
            "Needs to have tender start date", p -> p.getTender() != null && p.getTender().getTenderPeriod() != null
            && p.getTender().getTenderPeriod().getStartDate() != null);

    public static final NamedPredicate<FlaggedRelease> TENDER_PROCURING_ENTITY = new NamedPredicate<>(
            "Needs to have tender procuring entity", p -> p.getTender() != null
            && p.getTender().getProcuringEntity() != null);

    public static final NamedPredicate<FlaggedRelease> TENDER_VALUE_AMOUNT = new NamedPredicate<>(
            "Needs to have tender value amount", p -> p.getTender() != null
            && p.getTender().getValue() != null && p.getTender().getValue().getAmount() != null);

    public static final NamedPredicate<FlaggedRelease> TENDER_END_DATE =
            new NamedPredicate<>("Needs to have tender end date", p -> p.getTender() != null
                    && p.getTender().getTenderPeriod() != null && p.getTender().getTenderPeriod().getEndDate() != null);

    public static final NamedPredicate<FlaggedRelease> OPEN_PROCUREMENT_METHOD =
            new NamedPredicate<>("Needs to have open tender procurement method",
                    p -> p.getTender() != null
                            && Tender.ProcurementMethod.open.equals(p.getTender().getProcurementMethod()));

    public static final NamedPredicate<FlaggedRelease> SELECTIVE_PROCUREMENT_METHOD =
            new NamedPredicate<>("Needs to have selective tender procurement method",
                    p -> p.getTender() != null
                            && Tender.ProcurementMethod.selective.equals(p.getTender().getProcurementMethod()));

    public static final NamedPredicate<FlaggedRelease> LIMITED_PROCUREMENT_METHOD =
            new NamedPredicate<>("Needs to have limited tender procurement method",
                    p -> p.getTender() != null
                            && Tender.ProcurementMethod.limited.equals(p.getTender().getProcurementMethod()));

    public static final NamedPredicate<FlaggedRelease> ACTIVE_AWARD_WITH_DATE =
            new NamedPredicate<>("Needs to have at least one active award",
                    p -> p.getAwards().stream().filter(a -> a.getDate() != null
                            && Award.Status.active.equals(a.getStatus())).count() > 0);

    public static final NamedPredicate<FlaggedRelease> ACTIVE_AWARD =
            new NamedPredicate<>("Needs to have at least one active award",
                    p -> p.getAwards().stream().filter(a -> Award.Status.active.equals(a.getStatus())).count() > 0);

    public static final NamedPredicate<FlaggedRelease> AWARDED_AMOUNT =
            new NamedPredicate<>("Needs to have at least one award with awarded amount",
                    p -> p.getAwards().stream().filter(a -> a.getValue() != null
                            && a.getValue().getAmount() != null).count() > 0);

    public static final NamedPredicate<FlaggedRelease> TENDER_ITEMS_CLASSIFICATION =
            new NamedPredicate<>("Needs to have tender with items classification",
                    p -> p.getTender() != null
                            && !p.getTender().getItems().isEmpty()
                            && p.getTender().getItems().stream().findFirst().get().getClassification() != null);

    public static final NamedPredicate<FlaggedRelease> UNSUCCESSFUL_AWARD = new NamedPredicate<>(
            "Needs to have at least one unsuccessful award",
            p -> p.getAwards().stream().filter(a -> Award.Status.unsuccessful.equals(a.getStatus())).count() > 0);

    public static final NamedPredicate<FlaggedRelease> ELECTRONIC_SUBMISSION = new NamedPredicate<>(
            "Needs to have electronic submission tender submission method",
            p -> p.getTender() != null && p.getTender().getSubmissionMethod() != null
                    && p.getTender().getSubmissionMethod().contains(Tender.SubmissionMethod.electronicSubmission));
}
