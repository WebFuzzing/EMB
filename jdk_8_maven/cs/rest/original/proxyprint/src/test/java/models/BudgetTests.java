package models;

import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.PrintingSchema;
import io.github.proxyprint.kitchen.models.consumer.printrequest.Document;
import io.github.proxyprint.kitchen.models.consumer.printrequest.DocumentSpec;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.pricetable.*;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * Created by daniel on 04-06-2016.
 */
public class BudgetTests extends TestCase {
    PrintRequest pr;
    PrintingSchema ps;
    PrintShop printshop;
    Consumer consumer = new Consumer("Test", "test", "1234", "test@gmail.com", "0", "0");

    @Before
    public void setUp(){
        // Printshop
        printshop = new PrintShop("Video Norte", "Rua Nova de Santa Cruz", 41.5594, -8.3972, "123444378", "logo_8", 0);
        printshop.setId(8);

        // Paper
        PaperItem p1 = new PaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.BW);

        // Black & White
        RangePaperItem rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.BW, 1, 20);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.1);
        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.BW, 1, 20);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.19);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.BW, 1, 20);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.18);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.BW, 1, 20);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.35);

        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.BW, 21, 50);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.08);
        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.BW, 21, 50);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.15);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.BW, 21, 50);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.16);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.BW, 21, 50);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.31);

        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.BW, 51, 100);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.06);
        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.BW, 51, 100);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.11);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.BW, 51, 100);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.14);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.BW, 51, 100);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.27);

        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.BW, 101, 500);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.05);
        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.BW, 101, 500);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.09);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.BW, 101, 500);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.12);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.BW, 101, 500);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.23);

        // Color
        PaperItem p2 = new PaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.COLOR);

        RangePaperItem rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.COLOR, 1, 5);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.75);
        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.COLOR, 1, 5);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.49);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.COLOR, 1, 5);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.40);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.COLOR, 1, 5);
        printshop.addItemPriceTable(rp2.genKey(),(float) 2.79);

        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.COLOR, 6, 20);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.60);
        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.COLOR, 6, 20);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.19);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.COLOR, 6, 20);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.10);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.COLOR, 6, 20);
        printshop.addItemPriceTable(rp2.genKey(),(float) 2.19);

        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.COLOR, 21, 50);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.50);
        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.COLOR, 21, 50);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.99);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.COLOR, 21, 50);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.95);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.COLOR, 21, 50);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.89);

        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.COLOR, 51, 100);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.40);
        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.COLOR, 51, 100);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.79);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.COLOR, 51, 100);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.75);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.COLOR, 51, 100);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.49);

        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.COLOR, 101, 500);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.30);
        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.COLOR, 101, 500);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.59);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.COLOR, 101, 500);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.55);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.COLOR, 101, 500);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.19);

        // Bindings
        BindingItem b = new BindingItem(BindingItem.RingType.PLASTIC, 6, 10);
        printshop.addItemPriceTable(b.genKey(), (float) 1.15);
        b = new BindingItem(BindingItem.RingType.PLASTIC, 12, 20);
        printshop.addItemPriceTable(b.genKey(), (float) 1.4);
        b = new BindingItem(BindingItem.RingType.PLASTIC, 22, 28);
        printshop.addItemPriceTable(b.genKey(), (float) 1.75);
        b = new BindingItem(BindingItem.RingType.PLASTIC, 32, 38);
        printshop.addItemPriceTable(b.genKey(), (float) 2.00);
        b = new BindingItem(BindingItem.RingType.PLASTIC, 45, 52);
        printshop.addItemPriceTable(b.genKey(), (float) 2.5);

        b = new BindingItem(BindingItem.RingType.SPIRAL, 6, 10);
        printshop.addItemPriceTable(b.genKey(), (float) 1.55);
        b = new BindingItem(BindingItem.RingType.SPIRAL, 12, 20);
        printshop.addItemPriceTable(b.genKey(), (float) 1.90);
        b = new BindingItem(BindingItem.RingType.SPIRAL, 24, 32);
        printshop.addItemPriceTable(b.genKey(), (float) 2.55);
        b = new BindingItem(BindingItem.RingType.SPIRAL, 36, 40);
        printshop.addItemPriceTable(b.genKey(), (float) 2.95);
        b = new BindingItem(BindingItem.RingType.SPIRAL, 44, 50);
        printshop.addItemPriceTable(b.genKey(), (float) 3.35);
        BindingItem bs = new BindingItem(BindingItem.RingType.STAPLING, 0, 0);
        printshop.addItemPriceTable(bs.genKey(), (float) 0.01);

        // Covers
        CoverItem c = new CoverItem(Item.CoverType.CRISTAL_ACETATE, Item.Format.A4);
        printshop.addItemPriceTable(c.genKey(), (float) 0.5);

        c = new CoverItem(Item.CoverType.PVC_TRANSPARENT, Item.Format.A4);
        printshop.addItemPriceTable(c.genKey(), (float) 0.7);
        c = new CoverItem(Item.CoverType.PVC_TRANSPARENT, Item.Format.A3);
        printshop.addItemPriceTable(c.genKey(), (float) 1.5);

        c = new CoverItem(Item.CoverType.PVC_OPAQUE, Item.Format.A4);
        printshop.addItemPriceTable(c.genKey(), (float) 0.7);
        c = new CoverItem(Item.CoverType.PVC_OPAQUE, Item.Format.A3);
        printshop.addItemPriceTable(c.genKey(), (float) 1.5);

    }

    /**
     * Doc.pdf (23 pag.)
     * Budget: (((22/2)*1.19)+(1*0.60)) + 0.01 = 13.70
     * @throws Exception
     */
    @Test
    public void testSimpleBudget() throws Exception {
        ps = new PrintingSchema("Cores+A4+Frente+Agrafar", "PAPER,COLOR,A4,DUPLEX", "BINDING,STAPLING,0,0", "");
        pr = new PrintRequest();
        pr.setArrivalTimestamp(new Date());
        pr.setConsumer(consumer);
        pr.setPaymentType(PrintRequest.PROXY_PAYMENT);

        Document doc = new Document("Doc.pdf", 23);
        DocumentSpec docSpc = new DocumentSpec(1,23,ps);
        Set<DocumentSpec> docSpcs = new HashSet<>();
        docSpcs.add(docSpc);
        doc.setSpecs(docSpcs);

        Set<Document> docs = new HashSet<>();
        docs.add(doc);
        pr.setDocuments(docs);

        List<PrintShop> pshops = new ArrayList<>();
        pshops.add(printshop);

        Map<Long,String> map = pr.calcBudgetsForPrintShops(pshops);

        assertTrue(map.get((long)8).equals("13.7"));
    }

    /**
     * Doc.pdf (1 pag.)
     * Budget: 0.75 + 0.01 = 0.76
     * @throws Exception
     */
    @Test
    public void testSimpleBudgetOnePageDoc() throws Exception {
        ps = new PrintingSchema("Cores+A4+Frente+Agrafar", "PAPER,COLOR,A4,DUPLEX", "BINDING,STAPLING,0,0", "");
        pr = new PrintRequest();
        pr.setArrivalTimestamp(new Date());
        pr.setConsumer(consumer);
        pr.setPaymentType(PrintRequest.PROXY_PAYMENT);

        Document doc = new Document("Doc.pdf", 1);
        DocumentSpec docSpc = new DocumentSpec(1,1,ps);
        Set<DocumentSpec> docSpcs = new HashSet<>();
        docSpcs.add(docSpc);
        doc.setSpecs(docSpcs);

        Set<Document> docs = new HashSet<>();
        docs.add(doc);
        pr.setDocuments(docs);

        List<PrintShop> pshops = new ArrayList<>();
        pshops.add(printshop);

        Map<Long,String> map = pr.calcBudgetsForPrintShops(pshops);

        assertTrue(map.get((long)8).equals("0.76"));
    }

    /**
     * Doc.pdf (100 pag.)
     * Budget: ((88/2)*0.15) + 1.40 + 0.5 = 8.50
     * @throws Exception
     */
    @Test
    public void testBudgetBindAndCover() throws Exception {
        ps = new PrintingSchema("PB+A4+FV+Encadernar", "PAPER,BW,A4,DUPLEX", "BINDING,PLASTIC", "COVER,CRISTAL_ACETATE,A4");
        pr = new PrintRequest();
        pr.setArrivalTimestamp(new Date());
        pr.setConsumer(consumer);
        pr.setPaymentType(PrintRequest.PROXY_PAYMENT);

        Document doc = new Document("Doc.pdf", 100);
        DocumentSpec docSpc = new DocumentSpec(1,88,ps);
        Set<DocumentSpec> docSpcs = new HashSet<>();
        docSpcs.add(docSpc);
        doc.setSpecs(docSpcs);

        Set<Document> docs = new HashSet<>();
        docs.add(doc);
        pr.setDocuments(docs);

        List<PrintShop> pshops = new ArrayList<>();
        pshops.add(printshop);

        Map<Long,String> map = pr.calcBudgetsForPrintShops(pshops);

        assertTrue(map.get((long)8).equals("8.5"));
    }

    /**
     * Print the same document in separeted parts.
     * From page 1 to 101 PB+A4+DUPLEX+Argolas de Espiral+Capa A4 de Acetato de PVC Opaco
     * From page 102 to 115 Cores+A3+Frente+Agrafar
     *
     * Budget: ((100/2)*0.15) + (1*0.08) + 1.90 + 0.70 |+| (14*1.10) + 0.01 = 10.18 + 15.41 = 25.59
     *
     * @throws Exception
     */
    @Test
    public void testBudgetPageInterval() throws Exception {
        ps = new PrintingSchema("PB+A4+FV+Encadernar", "PAPER,BW,A4,DUPLEX", "BINDING,SPIRAL", "COVER,PVC_OPAQUE,A4");
        PrintingSchema ps2 = new PrintingSchema("Cores+A3+Frente+Agrafar", "PAPER,COLOR,A3,SIMPLEX", "BINDING,STAPLING,0,0", "");

        pr = new PrintRequest();
        pr.setArrivalTimestamp(new Date());
        pr.setConsumer(consumer);
        pr.setPaymentType(PrintRequest.PROXY_PAYMENT);

        Document doc = new Document("doc.pdf", 115);

        Set<DocumentSpec> docSpcs = new HashSet<>();

        DocumentSpec docSpc = new DocumentSpec(1,101,ps);
        docSpcs.add(docSpc);
        docSpc = new DocumentSpec(102,115,ps2);
        docSpcs.add(docSpc);

        doc.setSpecs(docSpcs);

        Set<Document> docs = new HashSet<>();
        docs.add(doc);
        pr.setDocuments(docs);

        List<PrintShop> pshops = new ArrayList<>();
        pshops.add(printshop);

        Map<Long,String> map = pr.calcBudgetsForPrintShops(pshops);

        assertTrue(map.get((long)8).equals("25.59"));
    }

    /**
     * Doc1.pdf (120 pag.)
     * From 1 to 1: COLOR+A3+SIMPLEX | 1.40 [OK]
     * From 1 to 6: COLOR+A4+DUPLEX | (6/2)*1.49 = 4.47 [OK]
     * From 4 to 120: BW+A4+DUPLEX+PLASTIC+CRISTAL_ACETATE | ((116/2)*0.11) + (1*0.06) +  1.40 + 0.5 = 8.34 [OK]
     * Budget: 14.21
     *
     * Doc2.pdf (20 pag.)
     * From 1 to 10: BW+A4+DUPLEX+STAPLING | ((10/2)*0.19) + 0.01 = 0.96 [OK]
     * From 1 to 10: COLOR+A4+DUPLEX+SPIRAL+PVC_TRANSPARENT | ((10/2)*1.49) + 1.55 + 0.70 = 9.70 []
     * From 1 to 20: BW+A4+DUPLEX+SPIRAL+CRISTAL_ACETATE | ((20/2)*0.19) + 1.55 + 0.50 = 3.95 [OK]
     * Budget: 13.11
     *
     * Total: 28.82
     *
     * BUG: If the number of pages id odd and the user chooses DUPLEX
     * the remain page must count as SIMPLEX!!!!
     *
     * BUG: How does the consumer tells he wants to bind same document when that document has more than 1 specs?
     *
     * @throws Exception
     */
    @Test
    public void testVeryComplexBudget() throws Exception {
        // Printing Schemas
        ps = new PrintingSchema("COLOR+A3+SIMPLEX", "PAPER,COLOR,A3,SIMPLEX", "", "");
        PrintingSchema ps1_2 = new PrintingSchema("COLOR+A4+DUPLEX", "PAPER,COLOR,A4,DUPLEX", "", "");
        PrintingSchema ps1_3 = new PrintingSchema("BW+A4+DUPLEX+Encadernar", "PAPER,BW,A4,DUPLEX", "BINDING,PLASTIC", "COVER,CRISTAL_ACETATE");
        PrintingSchema ps2_1 = new PrintingSchema("BW+A4+DUPLEX+STAPLING", "PAPER,BW,A4,DUPLEX", "BINDING,STAPLING", "");
        PrintingSchema ps2_2 = new PrintingSchema("COLOR+A4+DUPLEX+Enc1", "PAPER,COLOR,A4,DUPLEX", "BINDING,SPIRAL", "COVER,PVC_TRANSPARENT");
        PrintingSchema ps2_3 = new PrintingSchema("BW+A4+DUPLEX+Enc2", "PAPER,BW,A4,DUPLEX", "BINDING,SPIRAL", "COVER,CRISTAL_ACETATE");

        pr = new PrintRequest();
        pr.setArrivalTimestamp(new Date());
        pr.setConsumer(consumer);
        pr.setPaymentType(PrintRequest.PROXY_PAYMENT);

        Set<Document> docs = new HashSet<>();
        Set<DocumentSpec> docSpcs = new HashSet<>();

        Document doc1 = new Document("doc1.pdf", 120);

        DocumentSpec docSpc = new DocumentSpec(1,1,ps);
        docSpcs.add(docSpc);
        docSpc = new DocumentSpec(1,6,ps1_2);
        docSpcs.add(docSpc);
        docSpc = new DocumentSpec(4,120,ps1_3);
        docSpcs.add(docSpc);
        doc1.setSpecs(docSpcs);

        Document doc2 = new Document("doc2.pdf", 20);
        docSpcs = new HashSet<>();
        docSpc = new DocumentSpec(1,10,ps2_1);
        docSpcs.add(docSpc);
        docSpc = new DocumentSpec(1,10,ps2_2);
        docSpcs.add(docSpc);
        docSpc = new DocumentSpec(1,20,ps2_3);
        docSpcs.add(docSpc);
        doc2.setSpecs(docSpcs);

        docs.add(doc1);
        docs.add(doc2);
        pr.setDocuments(docs);

        List<PrintShop> pshops = new ArrayList<>();
        pshops.add(printshop);

        Map<Long,String> map = pr.calcBudgetsForPrintShops(pshops);

        assertTrue(map.get((long)8).equals("28.82"));
    }
}
