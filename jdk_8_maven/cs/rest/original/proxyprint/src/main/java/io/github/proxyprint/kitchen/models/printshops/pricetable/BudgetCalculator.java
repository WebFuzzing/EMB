package io.github.proxyprint.kitchen.models.printshops.pricetable;

import io.github.proxyprint.kitchen.models.consumer.PrintingSchema;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;

import java.util.Map;

/**
 * Created by daniel on 14-05-2016.
 */
public class BudgetCalculator {

    private PrintShop pshop;
    private ItemFactory itemFactory;

    public BudgetCalculator(PrintShop printShop) {
        this.pshop = printShop;
        this.itemFactory = new ItemFactory();
    }

    /**
     * Calculate the price for a single specification
     *
     * @param firstPage
     * @param lastPage
     * @param pschema
     * @return -1 if the request cannot be satisfied, a value bigger than or equal to 0 representing the cost of the specification.
     */
    public float calculatePrice(int firstPage, int lastPage, PrintingSchema pschema) {
        float cost = 0;
        boolean flagDuplexOdd = false;
        int numberOfPages = (lastPage - firstPage) + 1;

        // Paper
        if (pschema.getPaperItem() != null) {
            PaperItem pi = pschema.getPaperItem();
            if (pi != null) {
                if(numberOfPages==1) {
                    pi.setSides(Item.Sides.SIMPLEX);
                }
                else if (pi.getSides().equals(Item.Sides.DUPLEX)) {
                    if ((numberOfPages % 2) != 0) {
                        numberOfPages--;
                        flagDuplexOdd = true;
                    }
                    numberOfPages = numberOfPages / 2;
                }
                RangePaperItem rpi = findIdealRangePaperItem(numberOfPages, pi);
                if (rpi != null) {
                    float res = pshop.getPriceByKey(rpi.genKey());
                    if (res != -1) {
                        // If number of pages is odd and schema sides property is DUPLEX then the single page pays as SIMPLEX
                        if (flagDuplexOdd && this.pshop.getPriceTable().containsKey(rpi.genKey())) {
                            String rpiSimplexKey = rpi.genKey().replace(Item.Sides.DUPLEX.toString(), Item.Sides.SIMPLEX.toString());
                            float simplexCost = this.pshop.getPriceTable().get(rpiSimplexKey);
                            cost += simplexCost;
                        }
                        cost += numberOfPages * res; // Paper Items are expressed in €/page
                    } else return -1;
                } else return -1;
            }
        }

        // Binding
        if (pschema.getBindingItem() != null) {
            BindingItem bi = pschema.getBindingItem();

            if (bi.getRingsType().equals(Item.RingType.STAPLING)) {
                bi.setRingThicknessInfLim(0);
                bi.setRingThicknessSupLim(0);
            } else {
                Range r = new Range(1, 2);
                if (numberOfPages > BindingItem.B_6_10_THICKNESS_FLOOR && numberOfPages < BindingItem.B_6_10_THICKNESS_CEIL) {
                    r = new Range(6, 10);
                } else if (numberOfPages > BindingItem.B_12_20_THICKNESS_FLOOR && numberOfPages < BindingItem.B_12_20_THICKNESS_CEIL) {
                    r = new Range(12, 20);
                } else if (numberOfPages > BindingItem.B_22_28_THICKNESS_FLOOR && numberOfPages < BindingItem.B_22_28_THICKNESS_CEIL) {
                    r = new Range(22, 28);
                } else if (numberOfPages > BindingItem.B_32_38_THICKNESS_FLOOR) {
                    r = new Range(32, 38);
                }
                bi = findIdealBindingItem(numberOfPages, bi.getRingsType(), r);
            }

            if (bi != null) {
                float res = pshop.getPriceByKey(bi.genKey());
                if (res != -1) {
                    cost += res;
                } else return -1;
            }
        }

        // Cover
        if (pschema.getCoverItem() != null) {
            CoverItem ci = pschema.getCoverItem();
            if (ci != null) {
                float res = pshop.getPriceByKey(ci.genKey());
                if (res != -1) {
                    cost += res;
                } else return -1;
            }
        }
        return cost;
    }

    /**
     * Find a range paper item given the number of pages and
     * a PaperItem
     *
     * @param nPages number of pages
     * @param pi     a paper item to match in the pricetable
     * @return The suitable RangePagerItem for the given parameters
     */
    private RangePaperItem findIdealRangePaperItem(int nPages, PaperItem pi) {
        RangePaperItem idealRpi = null;
        int maxSupLim = 0;

        for (Map.Entry<String, Float> entry : pshop.getPriceTable().entrySet()) {
            String key = entry.getKey();
            Float price = entry.getValue();

            String[] parts = key.split(",");
            if (parts[0].equals(PaperItem.KEY_BASE)) {
                // Its a paper item and I want it!
                RangePaperItem rpi = (RangePaperItem) itemFactory.createItem(key);
                PaperItem tpi = new PaperItem(rpi.getFormat(), rpi.getSides(), rpi.getColors());

                if (tpi.genKey().equals(pi.genKey())) {
                    // Check for range compatibility
                    if (nPages >= rpi.getInfLim() && nPages <= rpi.getSupLim()) {
                        // Perfect match
                        idealRpi = rpi;
                        return idealRpi;
                    }
                    if (nPages > rpi.getSupLim()) {
                        if (rpi.getSupLim() > maxSupLim) {
                            // Assure that we hold the max sup limit of the interval
                            // in case of a non perfect match
                            idealRpi = rpi;
                            maxSupLim = rpi.getSupLim();
                        }
                    }
                }
            }
        }
        return idealRpi;
    }

    /**
     * Find a range paper item given the number of pages and
     * a PaperItem.
     *
     * @param nPages        number of pages.
     * @param ringType      type of ring to find.
     * @param needThisRange the more suitable rings range for the giver number of pages.
     * @return The suitable RangePagerItem for the given parameters.
     */
    private BindingItem findIdealBindingItem(int nPages, Item.RingType ringType, Range needThisRange) {
        BindingItem idealBi = null;
        int minDiffTop = 1000;
        int minDiffBottom = 1000;

        for (Map.Entry<String, Float> entry : pshop.getPriceTable().entrySet()) {
            String key = entry.getKey();
            Float price = entry.getValue();

            String[] parts = key.split(",");
            if (parts[0].equals(BindingItem.KEY_BASE)) {
                // Its a binding item and I want it!
                BindingItem tmpbi = (BindingItem) itemFactory.createItem(key);

                if (tmpbi.getRingsType().equals(ringType)) {
                    Range r = new Range(tmpbi.getRingThicknessInfLim(), tmpbi.getRingThicknessSupLim());
                    if (r.inf == needThisRange.inf && r.sup == needThisRange.sup) {
                        // Perfect Match
                        idealBi = tmpbi;
                        return idealBi;
                    } else if (r.inf > needThisRange.inf && (r.inf - needThisRange.inf) < minDiffBottom && Math.abs(r.sup - needThisRange.sup) < minDiffTop) {
                        // Best optimal approach match
                        idealBi = tmpbi;
                        minDiffBottom = (r.inf - needThisRange.inf);
                        minDiffTop = Math.abs(r.sup - needThisRange.sup);
                    }
                }
            }
        }
        return idealBi;
    }

    public class Range {
        public int inf, sup;

        public Range(int inf, int sup) {
            this.inf = inf;
            this.sup = sup;
        }
    }
}
