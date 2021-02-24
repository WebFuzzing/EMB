package io.github.proxyprint.kitchen.controllers.printshops.pricetable;

import io.github.proxyprint.kitchen.models.printshops.pricetable.BindingItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by daniel on 01-05-2016.
 */
public class RingsTable {
    public static String DEFAULT = "-";

    private Map<String,Set<RingTableItem>> items;
    private float staplingPrice;

    public RingsTable() {
        this.items = new HashMap<>();
        this.staplingPrice = 0;
    }

    public Map<String, Set<RingTableItem>> getItems() {
        return items;
    }

    public void setItems(Map<String, Set<RingTableItem>> items) {
        this.items = items;
    }

    public float getStaplingPrice() { return staplingPrice; }

    public void setStaplingPrice(float staplingPrice) { this.staplingPrice = staplingPrice; }

    public void addBindingItem(BindingItem bi, float price) {
        if(bi.getRingsType().equals(BindingItem.RingType.STAPLING)) {
            this.staplingPrice = price;
        }
        else {
            RingTableItem rti = new RingTableItem(RingTableItem.getPresentationStringForRings(bi.getRingsType()), bi.getRingThicknessInfLim(), bi.getRingThicknessSupLim(), String.valueOf(price));
            if (items.containsKey(bi.getRingsType().toString())) {
                this.items.get(bi.getRingsType().toString()).add(rti);
            } else {
                Set<RingTableItem> newRingSet = new TreeSet<>();
                newRingSet.add(rti);
                this.items.put(bi.getRingsType().toString(), newRingSet);
            }
        }
    }

}
