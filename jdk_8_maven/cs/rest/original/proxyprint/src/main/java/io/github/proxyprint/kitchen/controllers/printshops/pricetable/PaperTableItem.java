package io.github.proxyprint.kitchen.controllers.printshops.pricetable;

import io.github.proxyprint.kitchen.models.printshops.pricetable.PaperItem;

/**
 * Created by daniel on 02-05-2016.
 */
public class PaperTableItem implements Comparable<PaperTableItem> {
    public static String DEFAULT = "-";
    public static String A4Simplex = "A4SIMPLEX";
    public static String A4Duplex = "A4DUPLEX";
    public static String A3Simplex = "A3SIMPLEX";
    public static String A3Duplex = "A3DUPLEX";

    private int infLim;
    private int supLim;
    private String colors;
    private String priceA4SIMPLEX;
    private String priceA4DUPLEX;
    private String priceA3SIMPLEX;
    private String priceA3DUPLEX;

    public PaperTableItem() {
        super();
    }

    public PaperTableItem(int infLim, int supLim) {
        super();
        this.infLim = infLim;
        this.supLim = supLim;
        this.priceA4SIMPLEX = DEFAULT;
        this.priceA4DUPLEX = DEFAULT;
        this.priceA3SIMPLEX = DEFAULT;
        this.priceA3DUPLEX = DEFAULT;
    }

    public int getInfLim() {
        return infLim;
    }

    public void setInfLim(int infLim) {
        this.infLim = infLim;
    }

    public int getSupLim() {
        return supLim;
    }

    public void setSupLim(int supLim) {
        this.supLim = supLim;
    }

    public String getPriceA4SIMPLEX() {
        return priceA4SIMPLEX;
    }

    public void setPriceA4SIMPLEX(String priceA4SIMPLEX) {
        this.priceA4SIMPLEX = priceA4SIMPLEX;
    }

    public String getPriceA4DUPLEX() {
        return priceA4DUPLEX;
    }

    public void setPriceA4DUPLEX(String priceA4DUPLEX) {
        this.priceA4DUPLEX = priceA4DUPLEX;
    }

    public String getPriceA3SIMPLEX() {
        return priceA3SIMPLEX;
    }

    public void setPriceA3SIMPLEX(String priceA3SIMPLEX) {
        this.priceA3SIMPLEX = priceA3SIMPLEX;
    }

    public String getPriceA3DUPLEX() {
        return priceA3DUPLEX;
    }

    public void setPriceA3DUPLEX(String priceA3DUPLEX) {
        this.priceA3DUPLEX = priceA3DUPLEX;
    }

    public String getColors() { return colors; }

    public void setColors(String colors) { this.colors = colors; }

    public String genKey() {
        return this.infLim+";"+this.supLim;
    }

    // Add new cost to the PaperTableItem instance
    public void addPriceToPaperTableItem(PaperItem pi, float price) {
        String paperSpecs = pi.getPaperSpecs();

        // Format+Sides
        if(paperSpecs.equals(A4Simplex)) {
            this.priceA4SIMPLEX = String.valueOf(price);
        } else if(paperSpecs.equals(A4Duplex)) {
            this.priceA4DUPLEX = String.valueOf(price);
        } if(paperSpecs.equals(A3Simplex)) {
            this.priceA3SIMPLEX = String.valueOf(price);
        } if(paperSpecs.equals(A3Duplex)) {
            this.priceA3DUPLEX = String.valueOf(price);
        }
    }

    @Override
    public int compareTo(PaperTableItem pti) {
        if(pti.getSupLim() > this.getSupLim()) return -1;
        else return 1;
    }
}
