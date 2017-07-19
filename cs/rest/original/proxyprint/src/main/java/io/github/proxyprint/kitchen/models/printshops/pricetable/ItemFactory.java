package io.github.proxyprint.kitchen.models.printshops.pricetable;

import io.github.proxyprint.kitchen.controllers.printshops.pricetable.PaperTableItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 28-04-2016.
 */
public class ItemFactory {

    public ItemFactory(){}

    /**
     * Create a concrete instance of an item.
     * @param item, String that represents the item specs as stored in the database
     *              or as its received from front-end applications
     * @return A concrete instance of the Item derived from the input String
     * which is parsed along the function cut in pieces and feeded to the returned object.
     */
    public Item createItem(String item) {
        String itemType;

        String[] parts = item.split(",");
        itemType = parts[0];

        if(itemType.equals(RangePaperItem.KEY_BASE)) {
            PaperItem.Colors colors;
            PaperItem.Format format;
            PaperItem.Sides sides;

            colors = PaperItem.Colors.valueOf(parts[1]);
            format = PaperItem.Format.valueOf(parts[2]);
            sides = PaperItem.Sides.valueOf(parts[3]);

            if(parts.length > 4 && parts[4]!=null && parts[5]!=null) {
                int infLim, supLim;
                infLim = Integer.parseInt(parts[4]);
                supLim = Integer.parseInt(parts[5]);
                return new RangePaperItem(format, sides, colors, infLim, supLim);
            } else {
                return new PaperItem(format,sides,colors);
            }
        }
        else if(itemType.equals(BindingItem.KEY_BASE)) {
            BindingItem.RingType ringsType;

            ringsType = BindingItem.RingType.valueOf(parts[1]);
            if(parts.length > 3 && parts[2]!=null && parts[3]!=null) {
                int infLim, supLim;
                infLim = Integer.parseInt(parts[2]);
                supLim = Integer.parseInt(parts[3]);
                return new BindingItem(ringsType,infLim,supLim);
            } else {
                return new BindingItem(ringsType,0,0);
            }

        }
        else if(itemType.equals(CoverItem.KEY_BASE)) {
            CoverItem.CoverType coverType;

            coverType = CoverItem.CoverType.valueOf(parts[1]);
            if(parts.length > 2 && parts[2]!=null) {
                PaperItem.Format format = PaperItem.Format.valueOf(parts[2]);
                return new CoverItem(coverType,format);
            } else {
                // by default create an A4 cover
                return new CoverItem(coverType,PaperItem.Format.A4);
            }
        }

        return null;
    }

    /**
     * Convert a PaperTableItem to its respective PriceItems
     * @param pti, a new entry in the price table
     * @return List<RangePaperItem>, List of price items which result from the conversion.
     */
    public List<RangePaperItem> fromPaperTableItemToPaperItems(PaperTableItem pti) {
        List<RangePaperItem> res = new ArrayList<>();

        if(!pti.getPriceA4SIMPLEX().equals(PaperTableItem.DEFAULT)) {
            res.add(new RangePaperItem(PaperItem.Format.A4, PaperItem.Sides.SIMPLEX, PaperItem.Colors.valueOf(pti.getColors()), pti.getInfLim(), pti.getSupLim()));
        }
        if(!pti.getPriceA4DUPLEX().equals(PaperTableItem.DEFAULT)) {
            res.add(new RangePaperItem(PaperItem.Format.A4, PaperItem.Sides.DUPLEX, PaperItem.Colors.valueOf(pti.getColors()), pti.getInfLim(), pti.getSupLim()));
        }
        if(!pti.getPriceA3SIMPLEX().equals(PaperTableItem.DEFAULT)) {
            res.add(new RangePaperItem(PaperItem.Format.A3, PaperItem.Sides.SIMPLEX, PaperItem.Colors.valueOf(pti.getColors()), pti.getInfLim(), pti.getSupLim()));
        }
        if(!pti.getPriceA3DUPLEX().equals(PaperTableItem.DEFAULT)) {
            res.add(new RangePaperItem(PaperItem.Format.A3, PaperItem.Sides.DUPLEX, PaperItem.Colors.valueOf(pti.getColors()), pti.getInfLim(), pti.getSupLim()));
        }

        return res;
    }
}
