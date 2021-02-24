package io.github.proxyprint.kitchen.models.printshops.pricetable;

/**
 * Created by daniel on 28-04-2016.
 */
public abstract class Item {

    public static String PAPER = "PAPER";
    public static String COVER = "COVER";

    public static String checkItemType(String item) {
        String[] chunks = item.split(",");
        if (chunks[0] != null && (chunks[0].equals(PAPER) || chunks[0].equals(COVER) || chunks[0].equals(Item.RingType.BINDING.toString()))) {
            return chunks[0];
        }
        return "";
    }

    // Paper
    public static enum Format {
        A4, A3
    }

    public static enum Sides {
        SIMPLEX, DUPLEX
    }

    public static enum Colors {
        BW, GREY_TONES, COLOR
    }

    // Cover
    public static enum CoverType {
        CRISTAL_ACETATE, PVC_TRANSPARENT, PVC_OPAQUE
    }

    // Binding
    public static enum RingType {
        PLASTIC, SPIRAL, WIRE, STEELMAT, STAPLING, BINDING
    }

    public Item() {
    }

    /**
     * Generates a key which identifies (and describes) a certain item.
     *
     * @return the key which identifies the item by the
     * concatenation of its characteristics.
     */
    public abstract String genKey();

}
