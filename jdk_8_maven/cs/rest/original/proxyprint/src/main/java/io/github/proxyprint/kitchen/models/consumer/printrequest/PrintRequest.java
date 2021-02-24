package io.github.proxyprint.kitchen.models.consumer.printrequest;

import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.pricetable.BudgetCalculator;
import io.github.proxyprint.kitchen.utils.gson.Exclude;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * Created by MGonc on 28/04/16.
 */
@Entity
@Table(name = "print_requests")
public class PrintRequest implements Serializable {

    public static String PAYPAL_COMPLETED_PAYMENT = "Completed";
    public static String PROXY_PAYMENT = "PROXYPRINT_PAYMENT";
    public static String PAYPAL_PAYMENT = "PAYPAL_PAYMENT";

    public enum Status {
        NOT_PAYED, PENDING, IN_PROGRESS, FINISHED, LIFTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = true, name = "cost")
    private double cost;

    @Column(nullable = true, name = "arrival")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date arrivalTimestamp;

    @Column(nullable = true, name = "finished")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishedTimestamp;

    @Column(nullable = true, name = "delivered")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date deliveredTimestamp;

    @Column(nullable = true, name = "empattended")
    private String empAttended;

    @Column(nullable = true, name = "empdelivered")
    private String empDelivered;

    @Column(nullable = true, name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(nullable = true, name = "paypal_sale_id")
    private String payPalSaleID;
    @ManyToOne(cascade = CascadeType.ALL)
    @Exclude
    private PrintShop printshop;

    @ManyToOne(cascade = CascadeType.ALL)
    private Consumer consumer;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "print_request_id")
    private Set<Document> documents;

    @Column(nullable = true, name = "payment_type")
    private String paymentType;

    public PrintRequest() {
        this.documents = new HashSet<>();
        this.status = Status.NOT_PAYED;
        this.paymentType = PROXY_PAYMENT;
    }

    public PrintRequest(double cost, Date arrivalTimestamp, Consumer consumer, Status status) {
        this.cost = cost;
        this.arrivalTimestamp = arrivalTimestamp;
        this.consumer = consumer;
        this.status = status;
        this.documents = new HashSet<>();
        this.paymentType = PROXY_PAYMENT;
    }

    public long getId() {
        return id;
    }

    public double getCost() {
        return cost;
    }

    public Date getArrivalTimestamp() {
        return arrivalTimestamp;
    }

    public Date getDeliveredTimestamp() {
        return deliveredTimestamp;
    }

    public String getEmpAttended() {
        return empAttended;
    }

    public Date getFinishedTimestamp() {
        return finishedTimestamp;
    }

    public String getEmpDelivered() {
        return empDelivered;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public Status getStatus() {
        return status;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setArrivalTimestamp(Date arrivalTimestamp) {
        this.arrivalTimestamp = arrivalTimestamp;
    }

    public void setFinishedTimestamp(Date finishedTimestamp) {
        this.finishedTimestamp = finishedTimestamp;
    }

    public void setEmpAttended(String empAttended) {
        this.empAttended = empAttended;
    }

    public void setDeliveredTimestamp(Date deliveredTimestamp) {
        this.deliveredTimestamp = deliveredTimestamp;
    }

    public void setEmpDelivered(String empDelivered) {
        this.empDelivered = empDelivered;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public PrintShop getPrintshop() {
        return printshop;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    public void addDocument(Document doc) {
        this.documents.add(doc);
    }

    public boolean isPayed() {
        return !(this.status.equals(Status.NOT_PAYED));
    }

    public String getPayPalSaleID() {
        return payPalSaleID;
    }

    public void setPayPalSaleID(String payPalSaleID) {
        this.payPalSaleID = payPalSaleID;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    /**
     * Calculate budgets for a given list of printshops.
     *
     * @param pshops printshops to calculate the budget.
     * @return A map containing the printshopID associated with the calculated
     * budget
     */
    public Map<Long, String> calcBudgetsForPrintShops(List<PrintShop> pshops) {
        Map<Long, String> budgets = new HashMap<>();

        Set<Document> prDocs = this.getDocuments();
        for (PrintShop printShop : pshops) {
            BudgetCalculator budgetCalculator = new BudgetCalculator(printShop);
            float totalCost = 0; // In the future we may specifie the budget by file its easy!
            for (Document document : prDocs) {
                for (DocumentSpec documentSpec : document.getSpecs()) {
                    float specCost = 0;
                    if (documentSpec.getFirstPage() != 0 && documentSpec.getLastPage() != 0) {
                        // Partial calculation
                        specCost = budgetCalculator.calculatePrice(documentSpec.getFirstPage(), documentSpec.getLastPage(), documentSpec.getPrintingSchema());
                    } else {
                        // Total calculation
                        System.out.println(document.getTotalPages());
                        specCost = budgetCalculator.calculatePrice(1, document.getTotalPages(), documentSpec.getPrintingSchema());
                    }
                    if (specCost != -1) {
                        totalCost += specCost;
                    } else {
                        budgets.put(printShop.getId(), "Esta reprografia não pode satisfazer o pedido.");
                    }
                }
            }
            if (totalCost > 0) {
                NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                DecimalFormat df = (DecimalFormat) nf;
                df.setMaximumFractionDigits(2);
                budgets.put(printShop.getId(), String.valueOf(df.format(totalCost))); // add to budgets
            }
        }

        return budgets;
    }
}
