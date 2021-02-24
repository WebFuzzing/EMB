package io.github.proxyprint.kitchen.models.consumer.printrequest;

import io.github.proxyprint.kitchen.models.consumer.PrintingSchema;

import javax.persistence.*;
import java.util.List;

/**
 * Created by daniel on 09-05-2016.
 */
@Entity
@Table(name = "documents_specs")
public class DocumentSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_page", nullable = false)
    private int firstPage;

    @Column(name = "last_page", nullable = false)
    private int lastPage;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "printing_schema")
    private PrintingSchema printingSchema;

    @Transient private String specsToString;

    public DocumentSpec() {}

    public DocumentSpec(int firstPage, int lastPage, PrintingSchema printingSchema) {
        this.firstPage = firstPage;
        this.lastPage = lastPage;
        this.printingSchema = printingSchema;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public int getFirstPage() { return firstPage; }

    public void setFirstPage(int firstPage) { this.firstPage = firstPage; }

    public int getLastPage() { return lastPage; }

    public void setLastPage(int lastPage) { this.lastPage = lastPage; }

    public PrintingSchema getPrintingSchema() { return printingSchema; }

    public void setPrintingSchema(PrintingSchema printingSchema) { this.printingSchema = printingSchema; }

    public void setSpecsToString() {
        String range;
        if(this.firstPage==0 && this.lastPage==0) {
            range = "Documento completo";
        } else {
            range = "Páginas " + this.firstPage + " - " + this.getLastPage();
        }
        this.specsToString = printingSchema.getPresentationString(range);
    }

    @Override
    public String toString() {
        return "DocumentSpec{" +
                "id=" + id +
                ", firstPage=" + firstPage +
                ", lastPage=" + lastPage +
                ", printingSchema=" + printingSchema.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentSpec)) return false;

        DocumentSpec that = (DocumentSpec) o;

        if (getId() != that.getId()) return false;
        if (getFirstPage() != that.getFirstPage()) return false;
        if (getLastPage() != that.getLastPage()) return false;
        return getPrintingSchema() != null ? getPrintingSchema().equals(that.getPrintingSchema()) : that.getPrintingSchema() == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + getFirstPage();
        result = 31 * result + getLastPage();
        result = 31 * result + (getPrintingSchema() != null ? getPrintingSchema().hashCode() : 0);
        return result;
    }

    public String getPresentationString() {
        StringBuilder sb = new StringBuilder();
        String range;
        if(this.firstPage==0 && this.lastPage==0) {
            range = "Documento completo ";
        } else {
            range = "Páginas "+this.firstPage+"-"+this.getLastPage();
        }
        sb.append(range);
        sb.append(" "+this.printingSchema.getPresentationString(range));

        return sb.toString();
    }
}
