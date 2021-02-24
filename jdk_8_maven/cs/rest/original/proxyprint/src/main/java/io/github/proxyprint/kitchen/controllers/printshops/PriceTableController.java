package io.github.proxyprint.kitchen.controllers.printshops;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.controllers.printshops.pricetable.CoverTableItem;
import io.github.proxyprint.kitchen.controllers.printshops.pricetable.PaperTableItem;
import io.github.proxyprint.kitchen.controllers.printshops.pricetable.RingTableItem;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.pricetable.BindingItem;
import io.github.proxyprint.kitchen.models.printshops.pricetable.CoverItem;
import io.github.proxyprint.kitchen.models.printshops.pricetable.Item;
import io.github.proxyprint.kitchen.models.printshops.pricetable.RangePaperItem;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by daniel on 10-05-2016.
 */
@RestController 
@Transactional
public class PriceTableController {
    @Autowired
    private PrintShopDAO printshops;
    @Autowired
    private Gson GSON;

    /*------------------------------------------
    Paper
    ----------------------------------------*/
    @ApiOperation(value = "Returns success/insuccess", notes = "Addition of a new Paper Item to a price table of a certain print shop.")
    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/papers", method = RequestMethod.POST)
    public String addNewPaperItem(@PathVariable(value = "id") long id, @RequestBody PaperTableItem pti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            pshop.insertPaperTableItemsInPriceTable(pti);
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    @ApiOperation(value = "Returns success/insuccess", notes = "Edition of a new Paper Item from a price table of a certain print shop.")
    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/papers", method = RequestMethod.PUT)
    public String editPaperItem(@PathVariable(value = "id") long id, @RequestBody PaperTableItem pti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            pshop.insertPaperTableItemsInPriceTable(pti);
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    @ApiOperation(value = "Returns success/insuccess", notes = "Delete a new Paper Item from a price table of a certain print shop.")
    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/deletepaper", method = RequestMethod.POST)
    public String deletePaperItem(@PathVariable(value = "id") long id, @RequestBody PaperTableItem pti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            // Remove price items
            List<RangePaperItem> itemsToDelete = pshop.convertPaperTableItemToPaperItems(pti);
            for(RangePaperItem pi : itemsToDelete) {
                pshop.getPriceTable().remove(pi.genKey());
            }
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }


    /*------------------------------------------
    Rings
    ----------------------------------------*/

    @ApiOperation(value = "Returns success/insuccess", notes = "Addition of a new Rings Item to a price table of a certain print shop.")
    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/rings", method = RequestMethod.POST)
    public String addNewRingsItem(@PathVariable(value = "id") long id, @RequestBody RingTableItem rti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            BindingItem newBi = new BindingItem(Item.RingType.valueOf(rti.getRingType()), rti.getInfLim(), rti.getSupLim());
            pshop.addItemPriceTable(newBi.genKey(),Float.parseFloat(rti.getPrice()));
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    @ApiOperation(value = "Returns success/insuccess", notes = "Edition of an existing Rings Item to a price table of a certain print shop.")
    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/rings", method = RequestMethod.PUT)
    public String editRingsItem(@PathVariable(value = "id") long id, @RequestBody RingTableItem rti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            BindingItem newBi = new BindingItem(Item.RingType.valueOf(rti.getRingType()), rti.getInfLim(), rti.getSupLim());
            pshop.addItemPriceTable(newBi.genKey(),Float.parseFloat(rti.getPrice()));
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    @ApiOperation(value = "Returns success/insuccess", notes = "Delete an existing Rings Item to a price table of a certain print shop.")
    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/deletering", method = RequestMethod.POST)
    public String deleteRingItem(@PathVariable(value = "id") long id, @RequestBody RingTableItem rti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            // Remove price items
            BindingItem newBi = new BindingItem(RingTableItem.getRingTypeForPresentationString(rti.getRingType()), rti.getInfLim(), rti.getSupLim());
            pshop.getPriceTable().remove(newBi.genKey());
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }


    /*------------------------------------------
    Cover
    ----------------------------------------*/
    @ApiOperation(value = "Returns success/insuccess", notes = "Add a new Cover Item to a price table of a certain print shop.")
    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/covers", method = RequestMethod.POST)
    public String addNewCoverItem(@PathVariable(value = "id") long id, @RequestBody CoverTableItem cti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            List<CoverItem> newCoverItems = cti.convertToCoverItems();
            for(CoverItem newCi : newCoverItems) {
                if(newCi.getFormat() == Item.Format.A4) {
                    pshop.addItemPriceTable(newCi.genKey(), Float.parseFloat(cti.getPriceA4()));
                } else {
                    pshop.addItemPriceTable(newCi.genKey(), Float.parseFloat(cti.getPriceA3()));
                }
            }
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    @ApiOperation(value = "Returns success/insuccess", notes = "Edit an existent Cover Item to a price table of a certain print shop.")
    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/covers", method = RequestMethod.PUT)
    public String editCoverItem(@PathVariable(value = "id") long id, @RequestBody CoverTableItem cti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            List<CoverItem> newCoverItems = cti.convertToCoverItems();
            for(CoverItem newCi : newCoverItems) {
                if(newCi.getFormat() == Item.Format.A4) {
                    pshop.addItemPriceTable(newCi.genKey(), Float.parseFloat(cti.getPriceA4()));
                } else {
                    pshop.addItemPriceTable(newCi.genKey(), Float.parseFloat(cti.getPriceA3()));
                }
            }
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    @ApiOperation(value = "Returns success/insuccess", notes = "Delete an existent Cover Item to a price table of a certain print shop.")
    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/deletecover", method = RequestMethod.POST)
    public String deleteCoverItem(@PathVariable(value = "id") long id, @RequestBody CoverTableItem cti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            // Remove price items
            List<CoverItem> toRemoveCoverItems = cti.convertToCoverItems();
            for(CoverItem oldCi : toRemoveCoverItems) {
                pshop.getPriceTable().remove(oldCi.genKey());
            }
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    /*------------------------------------------
    Stapling
    ----------------------------------------*/

    @ApiOperation(value = "Returns success/insuccess", notes = "Edit the stapling price property table of a certain print shop.")
    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{printShopID}/pricetable/editstapling", method = RequestMethod.PUT)
    public String editStaplingPrice(@PathVariable(value = "printShopID") long psid, @RequestBody String newStaplingPrice) {
        PrintShop pshop = printshops.findOne(psid);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            // Edit stapling price
            pshop.getPriceTable().put("BINDING,STAPLING,0,0", Float.parseFloat(newStaplingPrice));
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }
}
