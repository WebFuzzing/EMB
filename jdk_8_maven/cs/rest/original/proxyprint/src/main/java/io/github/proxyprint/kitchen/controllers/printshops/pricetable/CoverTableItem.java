package io.github.proxyprint.kitchen.controllers.printshops.pricetable;

import io.github.proxyprint.kitchen.models.printshops.pricetable.CoverItem;
import io.github.proxyprint.kitchen.models.printshops.pricetable.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 02-05-2016.
 */
public class CoverTableItem {
    public static String DEFAULT = "-";
    public String coverType;
    public String priceA4, priceA3;

    public CoverTableItem() {
        this.coverType = DEFAULT;
        this.priceA4 = DEFAULT;
        this.priceA3 = DEFAULT;
    }

    public CoverTableItem(String coverType, String priceA4, String priceA3) {
        this.coverType = coverType;
        this.priceA4 = priceA4;
        this.priceA3 = priceA3;
    }

    public String getCoverType() { return coverType; }

    public void setCoverType(String coverType) {this.coverType = coverType; }

    public String getPriceA4() { return priceA4; }

    public void setPriceA4(String priceA4) { this.priceA4 = priceA4; }

    public String getPriceA3() { return priceA3; }

    public void setPriceA3(String priceA3) { this.priceA3 = priceA3; }

    public static String getPresentationStringForCover(Item.CoverType ct) {
        if(ct.equals(Item.CoverType.CRISTAL_ACETATE)) {
            return "Acetato Cristal";
        } else if(ct.equals(Item.CoverType.PVC_TRANSPARENT)) {
            return "PVC Transparente";
        } else if(ct.equals(Item.CoverType.PVC_OPAQUE)) {
            return "PVC Opaco";
        }
        return "";
    }

    public List<CoverItem> convertToCoverItems() {
        List<CoverItem> res = new ArrayList<>();
        CoverItem ci;
        if(!this.getPriceA3().equals(DEFAULT)) {
            // Has A3 price
            ci = new CoverItem(Item.CoverType.valueOf(this.getCoverType()), Item.Format.A3);
            res.add(ci);
        }
        if(!this.getPriceA4().equals(DEFAULT)) {
            // Has A4 price
            ci = new CoverItem(Item.CoverType.valueOf(this.getCoverType()), Item.Format.A4);
            res.add(ci);
        }

        return res;
    }
}
