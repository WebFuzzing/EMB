package io.github.proxyprint.kitchen.models.consumer;

import io.github.proxyprint.kitchen.models.printshops.pricetable.*;

import javax.persistence.*;

/**
 * Created by daniel on 27-04-2016.
 */
@Entity
@Table(name = "printing_schemas")
public class PrintingSchema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "pschema_name", nullable = false)
    private String name;

    @Column(name = "paper_specs", nullable = false)
    private String paperSpecs;

    @Column(name = "binding_specs", nullable = true)
    private String bindingSpecs;

    @Column(name = "cover_specs", nullable = true)
    private String coverSpecs;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    public PrintingSchema() {}

    public PrintingSchema(PrintingSchema ps) {
        this.name = ps.getName();
        this.paperSpecs = ps.getPaperSpecs();
        this.bindingSpecs = ps.getBindingSpecs();
        this.coverSpecs = ps.getCoverSpecs();
        this.deleted = false;
    }

    public PrintingSchema(String name, String paperSpecs, String bindingSpecs, String coverSpecs) {
        this.name = name;
        this.paperSpecs = paperSpecs;
        this.bindingSpecs = bindingSpecs;
        this.coverSpecs = coverSpecs;
        this.deleted = false;
    }

    public PrintingSchema(String name , PaperItem psi, BindingItem bi, CoverItem ci) {
        this.name = name;
        this.paperSpecs = psi.genKey(); // Always required paperSpecs
        if(bi!=null) {
            this.bindingSpecs = bi.genKey();
            if(ci!=null) {
                this.coverSpecs = ci.genKey();
            } else {
                this.coverSpecs = "";
            }
        } else if(bi.getRingsType().equals(Item.RingType.BINDING.toString())) {
            bi.setRingThicknessInfLim(0);
            bi.setRingThicknessSupLim(0);
            this.bindingSpecs = BindingItem.RingType.STAPLING.toString();
        } else {
            this.bindingSpecs="";
        }
        this.deleted = false;
    }

    public long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getPaperSpecs() {
        if(paperSpecs!=null) return paperSpecs;
        else return "";
    }

    public void setPaperSpecs(String paperSpecs) { this.paperSpecs = paperSpecs; }

    public String getBindingSpecs() {
        if(bindingSpecs!=null) return bindingSpecs;
        else return "";
    }

    public void setBindingSpecs(String bindingSpecs) { this.bindingSpecs = bindingSpecs; }

    public String getCoverSpecs() {
        if(coverSpecs!=null) return coverSpecs;
        else return "";
    }

    public void setCoverSpecs(String coverSpecs) { this.coverSpecs = coverSpecs; }

    public boolean isDeleted() { return this.deleted; }

    public void delete() { this.deleted=true; }

    /**
     * Converts the string paperSpecs to its respective PaperItem Object.
     * @return a PaperItem
     */
    public PaperItem getPaperItem() {
        ItemFactory itemf = new ItemFactory();
        return (PaperItem) itemf.createItem(this.paperSpecs);
    }

    /**
     * Converts the string bindingSpecs to its respective PaperItem Object.
     * @return a BindingItem
     */
    public BindingItem getBindingItem() {
        ItemFactory itemf = new ItemFactory();
        return (BindingItem) itemf.createItem(this.bindingSpecs);
    }

    /**
     * Converts the string coverSpecs to its respective CoverItem Object.
     * @return CoverItem
     */
    public CoverItem getCoverItem() {
        ItemFactory itemf = new ItemFactory();
        return (CoverItem) itemf.createItem(this.coverSpecs);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrintingSchema)) return false;

        PrintingSchema that = (PrintingSchema) o;

        if (getId() != that.getId()) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getPaperSpecs() != null ? !getPaperSpecs().equals(that.getPaperSpecs()) : that.getPaperSpecs() != null)
            return false;
        if (getBindingSpecs() != null ? !getBindingSpecs().equals(that.getBindingSpecs()) : that.getBindingSpecs() != null)
            return false;
        return getCoverSpecs() != null ? getCoverSpecs().equals(that.getCoverSpecs()) : that.getCoverSpecs() == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPaperSpecs() != null ? getPaperSpecs().hashCode() : 0);
        result = 31 * result + (getBindingSpecs() != null ? getBindingSpecs().hashCode() : 0);
        result = 31 * result + (getCoverSpecs() != null ? getCoverSpecs().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PrintingSchema{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", paperSpecs='" + paperSpecs + '\'' +
                ", bindingSpecs='" + bindingSpecs + '\'' +
                ", coverSpecs='" + coverSpecs + '\'' +
                '}';
    }

    public String getPresentationString(String range) {
        StringBuilder sb = new StringBuilder();

        String[] psecs = this.paperSpecs.split(",");
        sb.append(range);
        sb.append(" - Cor: ");

        switch (psecs[1]){
            case "COLOR" :
                sb.append("A cores");
                break;
            case "GREY_TONES" :
                sb.append("Tons de Cinza");
                break;
            case "BW" :
                sb.append("Preto e Branco");
                break;
        }

        sb.append(" + Formato: " + psecs[2]);

        switch (psecs[3]){
            case "DUPLEX" :
                sb.append(", Frente e Verso");
                break;
            case "SIMPLEX" :
                sb.append(", Frente");
                break;
        }

        if(this.bindingSpecs!=null && !this.bindingSpecs.equals("")) {

            String[] bspecs = this.bindingSpecs.split(",");

            switch (bspecs[0]){
                case "STAPLING" :
                    sb.append(" + Acabamentos: Agrafar");
                    break;
                case "BINDING" :
                    switch (bspecs[1]){
                        case "SPIRAL" :
                            sb.append(" + Acabamentos: Argolas em Espiral");
                            break;
                        case "PLASTIC" :
                            sb.append(" + Acabamentos: Argolas de plástico");
                            break;
                        case "WIRE" :
                            sb.append(" + Acabamentos: Argolas de arame");
                            break;
                        case "STEELMAT" :
                            sb.append(" + Acabamentos: Encadernação térmica");
                            break;
                    }
                    break;
            }
        }

        if(this.coverSpecs!=null && !this.coverSpecs.equals("")) {

            String[] cspecs = this.coverSpecs.split(",");

            switch (cspecs[1]){
                case "CRISTAL_ACETATE" :
                    sb.append(" + Capa: Acetato em cristal");
                    break;
                case "PVC_TRANSPARENT" :
                    sb.append(" + Capa: PVC transparente fosco");
                    break;
                case "PVC_OPAQUE" :
                    sb.append(" + Capa: PVC opaco");
                    break;
            }
        }

        return sb.toString();
    }
}
