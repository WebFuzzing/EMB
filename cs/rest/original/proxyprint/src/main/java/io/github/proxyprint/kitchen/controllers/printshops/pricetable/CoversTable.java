package io.github.proxyprint.kitchen.controllers.printshops.pricetable;

import io.github.proxyprint.kitchen.models.printshops.pricetable.CoverItem;
import io.github.proxyprint.kitchen.models.printshops.pricetable.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by daniel on 02-05-2016.
 */
public class CoversTable {

    private Map<String,CoverTableItem> items;

    public CoversTable() {
        this.items = new HashMap<>();
    }

    public Map<String,CoverTableItem> getItems() { return items; }

    public void setItems(Map<String,CoverTableItem> items) { this.items = items; }

    public void addCoverItem(CoverItem ci, float price) {
        CoverTableItem cti = new CoverTableItem();
        if(this.items.containsKey(ci.getCoverType().toString())) {
            cti = this.items.get(ci.getCoverType().toString());
            if(ci.getFormat().equals(Item.Format.A4)) {
                cti.setPriceA4(String.valueOf(price));
            }
            else if(ci.getFormat().equals(Item.Format.A3)) {
                cti.setPriceA3(String.valueOf(price));
            }
            this.items.put(ci.getCoverType().toString(),cti);
        }
        else {
            cti.setCoverType(ci.getCoverType().toString());
            if(ci.getFormat().equals(Item.Format.A4)) {
                cti.setPriceA4(String.valueOf(price));
            }
            else if(ci.getFormat().equals(Item.Format.A3)) {
                cti.setPriceA3(String.valueOf(price));
            }
            this.items.put(ci.getCoverType().toString(),cti);
        }
    }
}
