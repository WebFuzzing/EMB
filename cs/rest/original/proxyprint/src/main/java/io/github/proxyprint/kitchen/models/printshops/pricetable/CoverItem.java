package io.github.proxyprint.kitchen.models.printshops.pricetable;

/**
 * Created by daniel on 27-04-2016.
 */
public class CoverItem extends Item {
    public static String KEY_BASE = "COVER";

    private CoverType coverType;
    private PaperItem.Format format;

    public CoverItem() {
        this.coverType = CoverType.CRISTAL_ACETATE;
        this.format = PaperItem.Format.A4;
    }

    public CoverItem(CoverType coverType, PaperItem.Format format) {
        this.coverType = coverType;
        this.format = format;
    }

    public CoverType getCoverType() { return coverType; }

    public void setCoverType(CoverType coverType) { this.coverType = coverType; }

    public PaperItem.Format getFormat() { return format; }

    public void setFormat(PaperItem.Format format) { this.format = format; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CoverItem)) return false;

        CoverItem coverItem = (CoverItem) o;

        if (getCoverType() != coverItem.getCoverType()) return false;
        return getFormat() == coverItem.getFormat();

    }

    @Override
    public int hashCode() {
        int result = getCoverType() != null ? getCoverType().hashCode() : 0;
        result = 31 * result + (getFormat() != null ? getFormat().hashCode() : 0);
        return result;
    }

    @Override
    public String genKey() {
        return String.format("%s,%s,%s",KEY_BASE, this.coverType.toString(), this.format.toString());
    }
}
