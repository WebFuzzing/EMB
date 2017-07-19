package io.github.proxyprint.kitchen.models.printshops;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.proxyprint.kitchen.controllers.printshops.pricetable.PaperTableItem;
import io.github.proxyprint.kitchen.models.Money;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.printshops.pricetable.Item;
import io.github.proxyprint.kitchen.models.printshops.pricetable.ItemFactory;
import io.github.proxyprint.kitchen.models.printshops.pricetable.PaperItem;
import io.github.proxyprint.kitchen.models.printshops.pricetable.RangePaperItem;
import io.github.proxyprint.kitchen.utils.gson.Exclude;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

/**
 * Created by daniel on 18-04-2016.
 */
@Entity
@Table(name = "printshops")
public class PrintShop {

    public static double PRINTSHOPS_PERCENTILS_REVENUE = 0.9;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(nullable = false, name = "address")
    private String address;

    @Column(nullable = false, name = "latitude")
    private Double latitude;

    @Column(nullable = false, name = "longitude")
    private Double longitude;

    @Column(nullable = false, name = "nif")
    private String nif;

    @Column(nullable = false, name = "logo")
    private String logo;

    @Column(nullable = false, name = "avg_rating")
    private float avgRating;

    @ElementCollection
    @JoinTable(name = "pricetables", joinColumns = @JoinColumn(name = "printshop_id"))
    @MapKeyColumn(name = "item")
    @Column(name = "price")
    @Exclude
    private Map<String, Float> priceTable;

    @JsonIgnore
    @Exclude
    @Transient
    private ItemFactory itemFactory;

    @JsonIgnore
    @Exclude
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "printshop")
    private Set<PrintRequest> printrequests;

    @JsonIgnore
    @Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "printshop")
    private Set<Review> reviews;

    @Column(unique = true)
    private Money balance;

    public PrintShop() {
        this.priceTable = new HashMap<>();
        itemFactory = new ItemFactory();
        this.printrequests = new HashSet<>();
        this.reviews = new HashSet<>();
        this.balance = new Money();
    }

    public PrintShop(String name, String address, Double latitude, Double longitude, String nif, String logo, float avgRating) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nif = nif;
        this.logo = logo;
        this.avgRating = avgRating;
        this.priceTable = new HashMap<>();
        this.itemFactory = new ItemFactory();
        this.printrequests = new HashSet<>();
        this.reviews = new HashSet<>();
        this.balance = new Money();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    public void addItemPriceTable(String i, float price) {
        this.priceTable.put(i, price);
    }

    public void addPriceItem(RangePaperItem item, float price) {
        this.priceTable.put(item.genKey(), price);
    }

    public Set<PrintRequest> getPrintRequests() {
        return printrequests;
    }

    public void setPrintingSchemas(Set<PrintRequest> printingReq) {
        this.printrequests = printingReq;
    }

    public float getPrice(Item item) {
        return this.priceTable.get(item.genKey());
    }

    public float getPriceByKey(String key) {
        if (this.priceTable.containsKey(key)) {
            return this.priceTable.get(key);
        } else {
            return -1;
        }
    }

    public Money getBalance() { return balance; }

    public void setBalance(Money balance) { this.balance = balance; }

    public Map<String, Float> getPriceTable() {
        return this.priceTable;
    }

    public void addPrintRequest(PrintRequest printrequest) {
        this.printrequests.add(printrequest);
    }

    public Item loadPriceItem(String item) {
        return itemFactory.createItem(item);
    }

    /**
     * Insert a PaperTableItem in the price table
     *
     * @param pti, a new entry in the price table
     */
    public void insertPaperTableItemsInPriceTable(PaperTableItem pti) {
        if (!pti.getPriceA4SIMPLEX().equals(PaperTableItem.DEFAULT)) {
            RangePaperItem a4s = new RangePaperItem(PaperItem.Format.A4, PaperItem.Sides.SIMPLEX, PaperItem.Colors.valueOf(pti.getColors()), pti.getInfLim(), pti.getSupLim());
            this.priceTable.put(a4s.genKey(), Float.parseFloat(pti.getPriceA4SIMPLEX()));
        }
        if (!pti.getPriceA4DUPLEX().equals(PaperTableItem.DEFAULT)) {
            RangePaperItem a4d = new RangePaperItem(PaperItem.Format.A4, PaperItem.Sides.DUPLEX, PaperItem.Colors.valueOf(pti.getColors()), pti.getInfLim(), pti.getSupLim());
            this.priceTable.put(a4d.genKey(), Float.parseFloat(pti.getPriceA4DUPLEX()));
        }
        if (!pti.getPriceA3SIMPLEX().equals(PaperTableItem.DEFAULT)) {
            RangePaperItem a3s = new RangePaperItem(PaperItem.Format.A3, PaperItem.Sides.SIMPLEX, PaperItem.Colors.valueOf(pti.getColors()), pti.getInfLim(), pti.getSupLim());
            this.priceTable.put(a3s.genKey(), Float.parseFloat(pti.getPriceA3SIMPLEX()));
        }
        if (!pti.getPriceA3DUPLEX().equals(PaperTableItem.DEFAULT)) {
            RangePaperItem a3d = new RangePaperItem(PaperItem.Format.A3, PaperItem.Sides.DUPLEX, PaperItem.Colors.valueOf(pti.getColors()), pti.getInfLim(), pti.getSupLim());
            this.priceTable.put(a3d.genKey(), Float.parseFloat(pti.getPriceA3DUPLEX()));
        }
    }

    public List<RangePaperItem> convertPaperTableItemToPaperItems(PaperTableItem pti) {
        List<RangePaperItem> res = itemFactory.fromPaperTableItemToPaperItems(pti);
        return res;
    }
    
    public Set<Review> getReviews(){
        return new HashSet(this.reviews);
    }
    
    public void addReview(Review review){
        this.reviews.add(review);
    }
    
    public boolean removeReview(Review review){
        return this.reviews.remove(review);
    }

    // Call me after add new review to printshop
    public void updatePrintShopRating() {
        int totalReviews = this.reviews.size();
        if(totalReviews > 1) {
            int sumRatins = 0;
            for (Review rev : this.reviews) {
                sumRatins += rev.getRating();
            }
            this.avgRating = (sumRatins / totalReviews);
        } else {
            Review only=null;
            for (Review rev : this.reviews) {
                only = rev;
            }
            if(only!=null) this.avgRating = (float)only.getRating();
        }
    }

    public String getPrintShopProfit() {
        double profit=0;

        for(PrintRequest prquest : printrequests) {
            if(prquest.getStatus().equals(PrintRequest.Status.FINISHED)) {
                profit += prquest.getCost();
            }
        }
        return String.format("%.2f", profit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrintShop)) {
            return false;
        }

        PrintShop printShop = (PrintShop) o;

        if (getId() != printShop.getId()) {
            return false;
        }
        if (Float.compare(printShop.getAvgRating(), getAvgRating()) != 0) {
            return false;
        }
        if (getName() != null ? !getName().equals(printShop.getName()) : printShop.getName() != null) {
            return false;
        }
        if (getAddress() != null ? !getAddress().equals(printShop.getAddress()) : printShop.getAddress() != null) {
            return false;
        }
        if (getLatitude() != null ? !getLatitude().equals(printShop.getLatitude()) : printShop.getLatitude() != null) {
            return false;
        }
        if (getLongitude() != null ? !getLongitude().equals(printShop.getLongitude()) : printShop.getLongitude() != null) {
            return false;
        }
        if (getNif() != null ? !getNif().equals(printShop.getNif()) : printShop.getNif() != null) {
            return false;
        }
        if (getLogo() != null ? !getLogo().equals(printShop.getLogo()) : printShop.getLogo() != null) {
            return false;
        }
        return getPriceTable() != null ? getPriceTable().equals(printShop.getPriceTable()) : printShop.getPriceTable() == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getAddress() != null ? getAddress().hashCode() : 0);
        result = 31 * result + (getLatitude() != null ? getLatitude().hashCode() : 0);
        result = 31 * result + (getLongitude() != null ? getLongitude().hashCode() : 0);
        result = 31 * result + (getNif() != null ? getNif().hashCode() : 0);
        result = 31 * result + (getAvgRating() != +0.0f ? Float.floatToIntBits(getAvgRating()) : 0);
        return result;
    }
}
