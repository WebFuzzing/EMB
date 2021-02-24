package io.github.proxyprint.kitchen.controllers.consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.models.Admin;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.PrintingSchema;
import io.github.proxyprint.kitchen.models.consumer.printrequest.Document;
import io.github.proxyprint.kitchen.models.consumer.printrequest.DocumentSpec;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.pricetable.Item;
import io.github.proxyprint.kitchen.models.printshops.pricetable.PaperItem;
import io.github.proxyprint.kitchen.models.repositories.*;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.print.Doc;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by daniel on 20-05-2016.
 */
@RestController 
@Transactional
public class PrintRequestController {

    @Autowired
    private Environment environment;
    @Autowired
    private ConsumerDAO consumers;
    @Autowired
    private PrintingSchemaDAO printingSchemas;
    @Autowired
    private DocumentDAO documents;
    @Autowired
    private DocumentSpecDAO documentsSpecs;
    @Autowired
    private PrintRequestDAO printRequests;
    @Autowired
    private PrintShopDAO printShops;
    @Autowired
    private PrintRequestDAO printrequests;
    @Autowired
    private AdminDAO admin;
    @Autowired
    private Gson GSON;

    /*------------------------------------
        Budget
    * -----------------------------------*/
    @ApiOperation(value = "Returns a set of budgets", notes = "This method calculates budgets for a given and already specified print request. The budgets are calculated for specific printshops also passed along as parameters.")
    @Secured("ROLE_USER")
    @RequestMapping(value = "/consumer/budget", method = RequestMethod.POST)
    public String calcBudgetForPrintRequest(HttpServletRequest request, Principal principal, @RequestPart("printRequest") String requestJSON) throws IOException {
        JsonObject response = new JsonObject();
        Consumer consumer = consumers.findByUsername(principal.getName());

        PrintRequest printRequest = new PrintRequest();
        printRequest.setConsumer(consumer);
        printRequest = printRequests.save(printRequest);

        List<Long> pshopIDs = null;
        Map prequest = new Gson().fromJson(requestJSON, Map.class);

        // PrintShops
        List<Double> tmpPshopIDs = (List<Double>) prequest.get("printshops");
        pshopIDs = new ArrayList<>();
        for (double doubleID : tmpPshopIDs) {
            pshopIDs.add((long) Double.valueOf((double) doubleID).intValue());
        }
        // Process files
        Map<String, Long> documentsIds = processSumitedFiles(printRequest, request);
        // Parse and store documents and specifications
        storeDocumentsAndSpecs(prequest, documentsIds);
        // Finally calculate the budgets :D
        List<PrintShop> pshops = getListOfPrintShops(pshopIDs);
        Map<Long, String> budgets = printRequest.calcBudgetsForPrintShops(pshops);

        response.addProperty("success", true);
        response.add("budgets", GSON.toJsonTree(budgets));
        response.addProperty("printRequestID", printRequest.getId());

        return GSON.toJson(response);
    }

    public Map<String, Long> processSumitedFiles(PrintRequest printRequest, HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, Long> documentsIds = new HashMap<String, Long>();
        for (Collection<MultipartFile> files : multipartRequest.getMultiFileMap().values()) {
            for (MultipartFile file : files) {
                singleFileHandle(file, printRequest, documentsIds);
            }
        }
        return documentsIds;
    }

    public void storeDocumentsAndSpecs(Map prequest, Map<String, Long> documentsIds) {
        Map<String, Map> mdocuments = (Map) prequest.get("files");
        for (Map.Entry<String, Map> documentSpecs : mdocuments.entrySet()) {
            String fileName = documentSpecs.getKey();
            List<Map<String, String>> specs = (List) documentSpecs.getValue().get("specs");

            for (Map<String, String> entry : specs) {
                Object tmpid = entry.get("id");
                Object tmpinfLim = entry.get("from");
                Object tmpsupLim = entry.get("to");

                long id = (long) Double.valueOf((double) tmpid).intValue();

                int infLim = 0;
                if (tmpinfLim != null) {
                    infLim = Double.valueOf((double) tmpinfLim).intValue();
                }

                int supLim = 0;
                if (tmpsupLim != null) {
                    supLim = Double.valueOf((double) tmpsupLim).intValue();
                }

                // Get printing schema by its id
                PrintingSchema tmpschema = printingSchemas.findOne(id);

                // Create DocumentSpec and associate it with respective Document
                DocumentSpec tmpdc = new DocumentSpec(infLim, supLim, tmpschema);
                documentsSpecs.save(tmpdc);
                if (documentsIds.containsKey(fileName)) {
                    long did = documentsIds.get(fileName);
                    Document tmpdoc = documents.findOne(did);
                    tmpdoc.addSpecification(tmpdc);
                    documents.save(tmpdoc);
                }
            }
        }
    }

    public List<PrintShop> getListOfPrintShops(List<Long> pshopsIDs) {
        List<PrintShop> pshops = new ArrayList<>();
        for (long pid : pshopsIDs) {
            pshops.add(printShops.findOne(pid));
        }
        return pshops;
    }

    private void singleFileHandle(MultipartFile file, PrintRequest printRequest, Map<String, Long> documentsIds) {
        String filetype = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!filetype.equalsIgnoreCase("pdf")) {
            return;
        }
        String name = FilenameUtils.removeExtension(file.getOriginalFilename());

        if (!file.isEmpty()) {
            try {
                PDDocument pdf = PDDocument.load(file.getInputStream());
                int count = pdf.getNumberOfPages();
                pdf.close();

                Document doc = new Document(name, count, printRequest);
                doc = this.documents.save(doc);
                printRequest.addDocument(doc);
                documentsIds.put(doc.getName() + ".pdf", doc.getId());

                FileOutputStream fos = new FileOutputStream(new File(Document.DIRECTORY_PATH + doc.getId() + ".pdf"));
                IOUtils.copy(file.getInputStream(), fos);
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(ConsumerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {

        }
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    @ApiOperation(value = "Returns success/insuccess", notes = "This method allow clients to POST a print request and associate it to a given printshop with a given budget, the payment may or not occur according to the payment method.")
    @Secured("ROLE_USER")
    @RequestMapping(value = "/consumer/printrequest/{printRequestID}/submit", method = RequestMethod.POST)
    public String finishAndSubmitPrintRequest(@PathVariable(value = "printRequestID") long prid, HttpServletRequest request, Principal principal) {
        JsonObject response = new JsonObject();
        PrintRequest printRequest = printRequests.findOne(prid);
        Consumer consumer = consumers.findByUsername(principal.getName());

        String requestJSON = null;
        try {
            requestJSON = IOUtils.toString(request.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map mrequest = new Gson().fromJson(requestJSON, Map.class);

        long pshopID = (long) Double.valueOf((double) mrequest.get("printshopID")).intValue();
        double cost = round((Double) mrequest.get("budget"), 2);
        String paymentMethod = (String) mrequest.get("paymentMethod");

        if (printRequest != null && consumer != null) {
            PrintShop pshop = printShops.findOne(pshopID);

            if (pshop != null) {
                // Final attributes for given print request
                printRequest.setArrivalTimestamp(new Date());
                printRequest.setStatus(PrintRequest.Status.NOT_PAYED);
                printRequest.setCost(cost);

                if (paymentMethod.equals(PrintRequest.PROXY_PAYMENT)) {
                    if (consumer.getBalance().getMoneyAsDouble() < cost) {
                        response.addProperty("success", false);
                        response.addProperty("message", "Não possuí saldo suficiente para efetuar o pedido.");
                        return GSON.toJson(response);
                    } else {
                        consumer.getBalance().subtractDoubleQuantity(cost);
                        Admin master = admin.findAll().iterator().next();
                        master.getBalance().addDoubleQuantity(cost);
                        consumers.save(consumer);
                        admin.save(master);
                        printRequest.setStatus(PrintRequest.Status.PENDING);
                    }
                } else {
                    printRequest.setPaymentType(PrintRequest.PAYPAL_PAYMENT);
                }

                printRequests.save(printRequest);
                pshop.addPrintRequest(printRequest);

                printShops.save(pshop);
                response.addProperty("success", true);
                return GSON.toJson(response);
            }
        }

        response.addProperty("success", false);
        return GSON.toJson(response);
    }

    protected double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /*-------------------------------------------------Integration----------------------------------------------------*/
    @ApiOperation(value = "Returns a Print Request ID", notes = "This method allows other platforms to print a document using ProxyPrint")
    @RequestMapping(value = "/printdocument", method = RequestMethod.POST)
    public String printDocument(HttpServletRequest request) throws IOException {
        JsonObject response = new JsonObject();

        PrintRequest printRequest = new PrintRequest();
        printRequest = printRequests.save(printRequest);

        // Process files
        Map<String, Long> documentsIds = processSumitedFiles(printRequest, request);

        // Parse and store documents and specifications
        storeDocumentsWithDefaultSpecs(documentsIds);

        response.addProperty("success", true);
        response.addProperty("printRequestID", printRequest.getId());
        return GSON.toJson(response);
    }

    public void storeDocumentsWithDefaultSpecs(Map<String, Long> documentsIds) {

        PaperItem p1 = new PaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.BW);
        PrintingSchema tmpschema = new PrintingSchema("A4+2LAD+PB", p1.genKey(), "BINDING,STAPLING,0,0", "");
        printingSchemas.save(tmpschema);

        for (String fileName : documentsIds.keySet()) {

            // Create DocumentSpec and associate it with respective Document
            DocumentSpec tmpdc = new DocumentSpec(0, 0, tmpschema);
            documentsSpecs.save(tmpdc);

            long did = documentsIds.get(fileName);
            Document tmpdoc = documents.findOne(did);
            tmpdoc.addSpecification(tmpdc);
            documents.save(tmpdoc);
        }
    }

    @ApiOperation(value = "Returns a document", notes = "This method returns the document from a print request ")
    @RequestMapping(value = "/printdocument/{id}", method = RequestMethod.GET)
    public String getDocument(@PathVariable(value = "id") long id) throws IOException {
        JsonObject response = new JsonObject();

        PrintRequest printRequest = printRequests.findOne(id);

        if (printRequest == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }

        response.addProperty("success", true);
        response.add("documents", GSON.toJsonTree(printRequest.getDocuments()));

        return GSON.toJson(response);
    }

    @ApiOperation(value = "Returns a set of budgets", notes = "This method calculates budgets for a given and already specified print request. The budgets are calculated for specific printshops also passed along as parameters.")
    @Secured("ROLE_USER")
    @RequestMapping(value = "/printdocument/{id}/budget", method = RequestMethod.POST)
    public String calcBudgetForPrintRecipe(Principal principal, @PathVariable(value = "id") long id, HttpServletRequest request) throws IOException {
        JsonObject response = new JsonObject();
        Consumer consumer = consumers.findByUsername(principal.getName());

        PrintRequest printRequest = printRequests.findOne(id);
        printRequest.setConsumer(consumer);
        printRequest = printRequests.save(printRequest);

        String requestJSON = IOUtils.toString(request.getInputStream());

        // PrintShops
        List<Double> tmpPshopIDs = GSON.fromJson(requestJSON, List.class);
        List<Long> pshopIDs = new ArrayList<>();

        for (Double doubleID : tmpPshopIDs) {
            pshopIDs.add((long) Double.valueOf(doubleID).intValue());
        }

        // Finally calculate the budgets :D
        List<PrintShop> pshops = getListOfPrintShops(pshopIDs);
        Map<Long, String> budgets = printRequest.calcBudgetsForPrintShops(pshops);

        response.addProperty("success", true);
        response.add("budgets", GSON.toJsonTree(budgets));
        response.addProperty("printRequestID", printRequest.getId());

        //se nao estiver no heroku, fazer tunel
        //if (this.environment.acceptsProfiles("!heroku")) {
        //    response.addProperty("externalURL", NgrokConfig.getExternalUrl());
        //}
        return GSON.toJson(response);
    }

}
