package io.github.proxyprint.kitchen.controllers.consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.PrintingSchema;
import io.github.proxyprint.kitchen.models.repositories.ConsumerDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintingSchemaDAO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by daniel on 28-04-2016.
 */
@RestController 
@Transactional
public class PrintingSchemaController {
    @Autowired
    private ConsumerDAO consumers;
    @Autowired
    private PrintingSchemaDAO printingSchemas;
    @Autowired
    private Gson GSON;

    /**
     * Get all the consumer's PrintingSchemas.
     * @param id, the id of the consumer.
     * @return set of the printing schemas belonging to the consumer matched by the id.
     */
    @ApiOperation(value = "Returns a set of the printing schemas.", notes = "This method allows consumers to get his PrintingSchemas.")
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/consumer/{consumerID}/printingschemas", method = RequestMethod.GET)
    public String getConsumerPrintingSchemas(@PathVariable(value = "consumerID") long id) {
        Set<PrintingSchema> consumerSchemas = consumers.findOne(id).getPrintingSchemas();
        JsonObject response = new JsonObject();
        if(consumerSchemas!=null) {
            response.addProperty("success", true);
            response.add("pschemas",GSON.toJsonTree(consumerSchemas));
            return GSON.toJson(response);
        } else {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    /**
     * Add a new PrintingSchema to user's printing schemas collection.
     * Test
     * curl --data "name=MyFancySchema&bindingSpecs=SPIRAL&coverSpecs=CRISTAL_ACETATE&paperSpecs=COLOR,A4,SIMPLEX" -u joao:1234 localhost:8080/consumer/1001/printingschemas
     * @param id, the id of the consumer.
     * @param ps, the PrintingSchema created by the consumer.
     * @return HttpStatus.OK if everything went well.
     */
    @ApiOperation(value = "Returns success/insuccess.", notes = "This method allows consumers to add a new printing schema to his printing schemas collection.")
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/consumer/{consumerID}/printingschemas", method = RequestMethod.POST)
    public String addNewConsumerPrintingSchema(@PathVariable(value = "consumerID") long id, @RequestBody PrintingSchema ps) {
        JsonObject obj = new JsonObject();
        Consumer c = consumers.findOne(id);
        PrintingSchema addedPS = printingSchemas.save(ps);
        boolean res = c.addPrintingSchema(addedPS);
        if(res) {
            consumers.save(c);
            obj.addProperty("success", true);
            obj.addProperty("id", addedPS.getId());
            return GSON.toJson(obj);
        } else {
            obj.addProperty("success", false);
            return GSON.toJson(obj);
        }
    }

    /**
     * Delete a PrintingSchema.
     * Test
     * curl -u joao:1234 -X DELETE localhost:8080/consumer/1001/printingschemas/{printingSchemaID}
     * @param cid, the id of the consumer.
     * @param psid, the id of the printing schema to delete.
     * @return HttpStatus.OK if everything went well.
     */
    @ApiOperation(value = "Returns success/insuccess.", notes = "This method allows consumers to delete a printing schema from his printing schemas collection.")
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/consumer/{consumerID}/printingschemas/{printingSchemaID}", method = RequestMethod.DELETE)
    public String deleteConsumerPrintingSchema(@PathVariable(value = "consumerID") long cid, @PathVariable(value = "printingSchemaID") long psid) {
        JsonObject obj = new JsonObject();
        PrintingSchema ps = printingSchemas.findOne(psid);
        if(!ps.isDeleted()) {
            ps.delete();
            printingSchemas.save(ps);
            obj.addProperty("success", true);
        } else {
            obj.addProperty("false", true);
        }
        return GSON.toJson(obj);
    }

    /**
     * Edit an existing PrintingSchema.
     * Test
     * curl -X PUT --data "name=MyFancyEditedSchema&bindingSpecs=STAPLING&paperSpecs=COLOR,A4,SIMPLEX" -u joao:1234 localhost:8080/consumer/1001/printingschemas/{printingSchemaID}
     * @param cid, the id of the consumer.
     * @param psid, the PrintingSchema id.
     * @return HttpStatus.OK if everything went well.
     */
    @ApiOperation(value = "Returns success/insuccess.", notes = "This method allows consumers to edit a printing schema from his printing schemas collection.")
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/consumer/{consumerID}/printingschemas/{printingSchemaID}", method = RequestMethod.PUT)
    public ResponseEntity<String> editConsumerPrintingSchema(@PathVariable(value = "consumerID") long cid, @PathVariable(value = "printingSchemaID") long psid, @RequestBody PrintingSchema pschema) {
        PrintingSchema ps = printingSchemas.findOne(psid);
        ps.setBindingSpecs(pschema.getBindingSpecs());
        ps.setCoverSpecs(pschema.getCoverSpecs());
        ps.setName(pschema.getName());
        ps.setPaperSpecs(pschema.getPaperSpecs());

        JsonObject obj = new JsonObject();

        PrintingSchema res = printingSchemas.save(ps);
        if(res!=null) {
            obj.addProperty("success", true);
             return new ResponseEntity<>(GSON.toJson(obj), HttpStatus.OK);
        } else {
            obj.addProperty("success", false);
             return new ResponseEntity<>(GSON.toJson(obj), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
