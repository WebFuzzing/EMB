package io.github.proxyprint.kitchen.controllers.consumer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.github.proxyprint.kitchen.models.Admin;
import io.github.proxyprint.kitchen.models.Money;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.printrequest.Document;
import io.github.proxyprint.kitchen.models.consumer.printrequest.DocumentSpec;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.pricetable.BudgetCalculator;
import io.github.proxyprint.kitchen.models.repositories.*;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.Principal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by daniel on 04-04-2016.
 */
@RestController 
@Transactional
public class ConsumerController {

    @Resource(name = "documentsPath")
    private String documentsPath;
    @Autowired
    private ConsumerDAO consumers;
    @Autowired
    private AdminDAO admin;
    @Autowired
    private PrintingSchemaDAO printingSchemas;
    @Autowired
    private DocumentDAO documents;
    @Autowired
    private PrintShopDAO printShops;
    @Autowired
    private PrintRequestDAO printrequests;
    @Autowired
    private Gson GSON;

    @ApiOperation(value = "Returns success/insuccess", notes = "This method allows consumer registration.")
    @RequestMapping(value = "/consumer/register", method = RequestMethod.POST)
    public String addUser(WebRequest request) {
        boolean success = false;

        JsonObject response = new JsonObject();
        String username = request.getParameter("username");
        Consumer c = consumers.findByUsername(username);

        if (c == null) {
            String password = request.getParameter("password");
            String email = request.getParameter("email");
            String name = request.getParameter("name");
            String lat = request.getParameter("latitude");
            String lon = request.getParameter("longitude");
            c = new Consumer(name, username, password, email, lat, lon);
            consumers.save(c);
            response.add("consumer", GSON.toJsonTree(c));
            success = true;
        } else {
            response.addProperty("error", "O username já se encontra em uso.");
            success = false;
        }

        response.addProperty("success", success);
        return GSON.toJson(response);
    }

    @ApiOperation(value = "Returns all the user information", notes = "This method allows consumers to get their personal information.")
    @Secured("ROLE_USER")
    @RequestMapping(value = "/consumer/info", method = RequestMethod.GET)
    public String getConsumerInfo(Principal principal) {
        boolean success = false;

        JsonObject response = new JsonObject();

        if(principal.getName()!=null) {
            Consumer c = consumers.findByUsername(principal.getName());
            if(c!=null) {
                response.add("consumer", GSON.toJsonTree(c));
                success = true;
            }
        }
        response.addProperty("success", success);
        return GSON.toJson(response);
    }

    @ApiOperation(value = "Updates the consumer information", notes = "This method allows consumers to update their personal information.")
    @Secured("ROLE_USER")
    @RequestMapping(value = "/consumer/info/update", method = RequestMethod.PUT)
    public String updateConsumerInfo(Principal principal, HttpServletRequest request) {
        boolean success = false;

        JsonObject response = new JsonObject();

        String requestJSON = null;
        Consumer editedConsumer= null;
        try {
            requestJSON = IOUtils.toString(request.getInputStream());
            editedConsumer = GSON.fromJson(requestJSON, Consumer.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(principal.getName()!=null) {
            Consumer c = consumers.findByUsername(principal.getName());
            if(c!=null) {
                if(!c.getName().equals(editedConsumer.getName())) {
                    c.setName(editedConsumer.getName());
                }
                if(!c.getUsername().equals(editedConsumer.getUsername())) {
                    c.setUsername(editedConsumer.getUsername());
                }
                if(!c.getPassword().equals(editedConsumer.getPassword())) {
                    c.setPassword(editedConsumer.getPassword());
                }
                if(!c.getEmail().equals(editedConsumer.getEmail())) {
                    c.setEmail(editedConsumer.getEmail());
                }
                consumers.save(c);
                success = true;
            }
        }
        response.addProperty("success", success);
        return GSON.toJson(response);
    }

    private void singleFileHandle(MultipartFile file, PrintRequest printRequest, Map<String, Long> documentsIds) {
        String filetype = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!filetype.equals("pdf")) {
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

                System.out.println();
                FileOutputStream fos = new FileOutputStream(new File(this.documentsPath+doc.getFile().getName()));
                IOUtils.copy(file.getInputStream(), fos);
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(ConsumerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {

        }
    }

    private Map<Long, String> calcBudgetsForPrintShops(List<Long> pshopIDs, PrintRequest printRequest) {
        Map<Long, String> budgets = new HashMap<>();

        Set<Document> prDocs = printRequest.getDocuments();
        for (long pshopID : pshopIDs) {
            PrintShop printShop = printShops.findOne(pshopID);
            BudgetCalculator budgetCalculator = new BudgetCalculator(printShop);
            float totalCost = 0; // In the future we may specifie the budget by file its easy!
            for (Document document : prDocs) {
                for (DocumentSpec documentSpec : document.getSpecs()) {
                    float specCost = 0;
                    if (documentSpec.getFirstPage() != 0 && documentSpec.getLastPage() != 0) {
                        // Partial calculation
                        specCost = budgetCalculator.calculatePrice(documentSpec.getFirstPage(), documentSpec.getLastPage(), documentSpec.getPrintingSchema());
                    } else {
                        // Total calculation
                        specCost = budgetCalculator.calculatePrice(1, document.getTotalPages(), documentSpec.getPrintingSchema());
                    }
                    if (specCost != -1) totalCost += specCost;
                    else {
                        budgets.put(pshopID, "Esta reprografia não pode satisfazer o pedido");
                    }
                }
            }
            if (totalCost > 0) budgets.put(pshopID, String.valueOf(totalCost)); // add to budgets
        }

        return budgets;
    }

    @ApiOperation(value = "Returns success/insuccess.", notes = "This method allows consumers to remove all notifications.")
    @Secured("ROLE_USER")
    @RequestMapping(value = "/consumer/{username}/notifications", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAllNotifications (@PathVariable(value = "username") String username) {
        JsonObject response = new JsonObject();
        Consumer c = consumers.findByUsername(username);
        c.removeAllNotifications();
        consumers.save(c);

        response.addProperty("success", true);
        return new ResponseEntity<>(GSON.toJson(response), HttpStatus.OK);
    }

    @ApiOperation(value = "Returns success/insuccess.", notes = "This method allows consumers to mark as read all notifications.")
    @Secured("ROLE_USER")
    @RequestMapping(value ="/consumer/{username}/notifications", method = RequestMethod.PUT)
    public ResponseEntity<String> readAllNotifications (@PathVariable(value = "username") String username) {

        JsonObject response = new JsonObject();
        Consumer c = consumers.findByUsername(username);
        c.readAllNotifications();
        consumers.save(c);

        response.addProperty("success", true);
        return new ResponseEntity<>(GSON.toJson(response), HttpStatus.OK);
    }

    @ApiOperation(value = "Returns pending requests.", notes = "This method retrieves to the consumer his pending requests.")
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/consumer/requests", method = RequestMethod.GET)
    public String getRequests(Principal principal) {

        JsonObject response = new JsonObject();
        Consumer consumer = consumers.findByUsername(principal.getName());

        if (consumer == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }


        List<PrintRequest.Status> status = new ArrayList<>();
        status.add(PrintRequest.Status.PENDING);

        List<PrintRequest> printRequestsList = printrequests.findByStatusInAndConsumer(status, consumer);
        Type listOfPRequests = new TypeToken<List<PrintShop>>(){}.getType();

        response.add("printrequests", GSON.toJsonTree(printRequestsList,listOfPRequests));
        response.addProperty("success", true);
        return GSON.toJson(response);
    }

    @ApiOperation(value = "Returns success/insuccess.", notes = "This method allows consumers to cancel a pending request.")
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/consumer/requests/cancel/{id}", method = RequestMethod.DELETE)
    public String cancelRequests(@PathVariable(value = "id") long id, Principal principal) {

        JsonObject response = new JsonObject();
        Consumer consumer = consumers.findByUsername(principal.getName());

        if (consumer == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }

        PrintRequest printRequest = printrequests.findByIdInAndConsumer(id,consumer);

        if (printRequest == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }

        if(printRequest.getStatus() == PrintRequest.Status.PENDING){

            PrintShop p = printRequest.getPrintshop();
            p.getPrintRequests().remove(printRequest);
            printShops.save(p);

            consumer.getPrintRequests().remove(printRequest);

            // Refund consumer with proxyprint money :D
            Admin master = admin.findAll().iterator().next();
            double returnQuantity = printRequest.getCost();
            master.getBalance().subtractDoubleQuantity(returnQuantity);
            admin.save(master);
            consumer.getBalance().addDoubleQuantity(returnQuantity);
            consumers.save(consumer);

            response.addProperty("success", true);
        } else{
            response.addProperty("success", false);
        }

        return GSON.toJson(response);
    }

    @ApiOperation(value = "Returns satisfied requests.", notes = "This method retrieves the history of satisfied requests from a consumer.")
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/consumer/satisfied", method = RequestMethod.GET)
    public String getPrintShopSatisfiedRequests(Principal principal) {
        JsonObject response = new JsonObject();

        Consumer consumer = consumers.findByUsername(principal.getName());

        if (consumer == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }

        List<PrintRequest.Status> status = new ArrayList<>();
        status.add(PrintRequest.Status.FINISHED);
        status.add(PrintRequest.Status.LIFTED);

        List<PrintRequest> printRequestsList = printrequests.findByStatusInAndConsumer(status, consumer);

        JsonArray jsonArray = new JsonArray();
        for (PrintRequest satisfiedRequest : printRequestsList){
            JsonObject aux = new JsonObject();
            aux.add("printrequest", GSON.toJsonTree(satisfiedRequest));
            aux.addProperty("printshop", satisfiedRequest.getPrintshop().getName());
            jsonArray.add(aux);
        }

        response.add("satisfiedrequests", jsonArray);
        response.addProperty("success", true);
        return GSON.toJson(response);
    }


    @ApiOperation(value = "Returns a certain consumer's balance.", notes = "Returns the consumer's balance, normaly used for update purposes.")
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/consumer/balance", method = RequestMethod.GET)
    public String getConsumerBalance(Principal principal) {
        JsonObject response = new JsonObject();
        Consumer consumer = consumers.findByUsername(principal.getName());
        if (consumer != null) {
            Money balance = consumer.getBalance();
            response.add("balance", GSON.toJsonTree(balance));
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        response.addProperty("success", false);
        return GSON.toJson(response);
    }

}
