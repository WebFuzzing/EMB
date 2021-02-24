package io.github.proxyprint.kitchen.controllers.printshops.pricetable;

import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.pricetable.RangePaperItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by daniel on 02-05-2016.
 */
public class PapersTable {
    private Map<String,Map<String,PaperTableItem>> items = new HashMap<>();

    public PapersTable() {
        this.items = new HashMap<>();
    }

    public Map<String, Map<String,PaperTableItem>> getItems() { return items; }

    public void setItems(Map<String, Map<String,PaperTableItem>> items) { this.items = items; }

    /**
     * Insert a (Range)PaperItem in a pricetable.
     * @param pi, the PaperItem to be inserted.
     * @param pshop, the current print shop instance.
     * @return the updated price table.
     */
    public void addRangePaperItem(RangePaperItem pi, PrintShop pshop) {
        if (!items.containsKey(pi.getColors().toString())) { // The color is new
            // Create new PaperTableItem
            PaperTableItem pti = new PaperTableItem(pi.getInfLim(), pi.getSupLim());
            pti.addPriceToPaperTableItem(pi, pshop.getPrice(pi));
            pti.setColors(pi.getColors().toString());

            // Add new range and associated PaperTableItem instance
            Map<String,PaperTableItem> map = new HashMap<>();
            map.put(pti.genKey(), pti);

            // Add to table
            items.put(pi.getColors().toString(), map);

        } else { // The color already exists
            String ptiKey = pi.getInfLim() + ";" + pi.getSupLim();
            Map<String, PaperTableItem> aux = items.get(pi.getColors().toString());
            PaperTableItem pti = aux.get(ptiKey);

            if (pti != null) {
                // PriceTableItem instance already exists add price
                pti.addPriceToPaperTableItem(pi, pshop.getPrice(pi));
                pti.setColors(pi.getColors().toString());
                items.get(pi.getColors().toString()).put(pti.genKey(), pti);
            } else {
                // Create new PaperTableItem
                pti = new PaperTableItem(pi.getInfLim(), pi.getSupLim());
                pti.setColors(pi.getColors().toString());
                pti.addPriceToPaperTableItem(pi, pshop.getPrice(pi));

                // Add new range and associated PaperTableItem instance
                Map<String,PaperTableItem> map = items.get(pi.getColors().toString());
                map.put(pti.genKey(), pti);

                // Add to table
                items.put(pi.getColors().toString(), map);
            }
        }
    }

    public Map<String, Set<PaperTableItem>> getFinalTable() {
        Map<String, Set<PaperTableItem>> finalTable = new HashMap<>();

        // Covert Map<String,PaperTableItem> into Set<PaperTableItem>
        for(String color : items.keySet()) {
            Map<String,PaperTableItem> map = items.get(color);
            Set<PaperTableItem> set = new TreeSet<>();
            for(PaperTableItem pti : map.values()) {
                set.add(pti);
            }
            finalTable.put(color,set);
        }

        return finalTable;
    }
}
