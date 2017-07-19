package io.github.proxyprint.kitchen.controllers.printshops;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.printshops.Employee;
import io.github.proxyprint.kitchen.models.printshops.Manager;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.repositories.EmployeeDAO;
import io.github.proxyprint.kitchen.models.repositories.ManagerDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintRequestDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by daniel on 27-04-2016.
 */
@RestController 
@Transactional
public class ManagerController {

    @Autowired
    PrintShopDAO printshops;
    @Autowired
    EmployeeDAO employees;
    @Autowired
    PrintRequestDAO printRequests;
    @Autowired
    ManagerDAO managers;
    @Autowired
    private Gson GSON;

    /*---------------------
        Employees
     ---------------------*/
    @ApiOperation(value = "Returns list of employees.", notes = "This method allows a manager to get the list of employees from his printshop.")
    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{printShopID}/employees", method = RequestMethod.GET)
    public String getEmployees(@PathVariable(value = "printShopID") long psid) {
        PrintShop pshop = printshops.findOne(psid);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            List<Employee> employeesList = employees.findByPrintShop(pshop);
            response.add("employees", GSON.toJsonTree(employeesList));
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    @ApiOperation(value = "Returns success/insuccess.", notes = "This method allows a manager to add a employee to his printshop.")
    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{printShopID}/employees", method = RequestMethod.POST)
    public String addEmployee(@PathVariable(value = "printShopID") long psid, HttpServletRequest request) {
        PrintShop pshop = printshops.findOne(psid);
        JsonObject response = new JsonObject();

        String requestJSON = null;
        Employee newEmp = null;
        try {
            requestJSON = IOUtils.toString( request.getInputStream());
            newEmp = GSON.fromJson(requestJSON, Employee.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(pshop!=null && newEmp!=null && newEmp.getName()!=null && newEmp.getUsername()!=null && newEmp.getPassword()!=null) {
            Employee e = employees.findByUsername(newEmp.getUsername());
            if(e==null) {
                e = new Employee(newEmp.getUsername(), newEmp.getPassword(), newEmp.getName(), pshop);
                employees.save(e);
                e = employees.findByUsername(e.getUsername());
                response.addProperty("success", true);
                response.addProperty("id", e.getId());
                return GSON.toJson(response);
            } else {
                response.addProperty("success", false);
                response.addProperty("message", "Empregado já existe");
                return GSON.toJson(response);
            }
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    @ApiOperation(value = "Returns success/insuccess.", notes = "This method allows a manager to edit a employee from his printshop.")
    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{printShopID}/employees", method = RequestMethod.PUT)
    public String editEmployee(@PathVariable(value = "printShopID") long psid, HttpServletRequest request) {
        PrintShop pshop = printshops.findOne(psid);
        JsonObject response = new JsonObject();

        String requestJSON = null;
        Employee editedEmp = null;
        try {
            requestJSON = IOUtils.toString( request.getInputStream());
            editedEmp = GSON.fromJson(requestJSON, Employee.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(pshop!=null && editedEmp!=null && editedEmp.getName()!=null && editedEmp.getUsername()!=null && editedEmp.getPassword()!=null) {
            Employee e = employees.findOne(editedEmp.getId());
            if(e==null) {
                response.addProperty("success", false);
                response.addProperty("message", "Empregado não existe");
                return GSON.toJson(response);
            } else {
                // Update fields
                e.setName(editedEmp.getName());
                e.setUsername(editedEmp.getUsername());
                e.setPassword(editedEmp.getPassword());
                employees.save(e);
                response.addProperty("success", true);
                return GSON.toJson(response);
            }
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    @ApiOperation(value = "Returns success/insuccess.", notes = "This method allows a manager to delete a employee from his printshop.")
    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{printShopID}/employees/{employeeID}", method = RequestMethod.DELETE)
    public String deleteEmployee(@PathVariable(value = "printShopID") long psid, @PathVariable(value = "employeeID") long eid) {
        Employee emp = employees.findOne(eid);
        JsonObject response = new JsonObject();

        if(emp!=null) {
            employees.delete(eid);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    @ApiOperation(value = "Returns success/insuccess.", notes = "This method allows a manager to get the statistics info from his printshop.")
    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/stats", method = RequestMethod.GET)
    public String getPrintShopStatistics(Principal principal) {
        JsonObject response = new JsonObject();
        Manager manager = managers.findByUsername(principal.getName());

        if(manager!=null) {
            PrintShop pshop = manager.getPrintShop();

            if (pshop != null) {
                // Print Request
                List<PrintRequest> listPrintRequests = printRequests.findByPrintshop(pshop);
                if (listPrintRequests != null) {
                    List<PrintRequest.Status> statusesPend = new ArrayList<PrintRequest.Status>() {{
                        add(PrintRequest.Status.PENDING);
                    }};
                    List<PrintRequest.Status> statusesInProgress = new ArrayList<PrintRequest.Status>() {{
                        add(PrintRequest.Status.IN_PROGRESS);
                    }};
                    List<PrintRequest.Status> statusesFinished = new ArrayList<PrintRequest.Status>() {{
                        add(PrintRequest.Status.FINISHED);
                    }};

                    response.addProperty("nPendingRequests", printRequests.findByStatusInAndPrintshop(statusesPend, pshop).size());
                    response.addProperty("nInProgressRequests", printRequests.findByStatusInAndPrintshop(statusesInProgress, pshop).size());
                    response.addProperty("nFinished", printRequests.findByStatusInAndPrintshop(statusesFinished, pshop).size());
                }

                // Employees
                response.addProperty("nEmployees", employees.findByPrintShop(pshop).size());

                // WE MUST ADD THIS TO OUR POJOs (Future Work)
                // response.addProperty("pshopProfit", pshop.getTotalRequests); ... by day, week, month, year
                response.addProperty("pshopProfit", pshop.getPrintShopProfit());

                response.addProperty("success", true);
                return GSON.toJson(response);
            }
        }
        response.addProperty("success", false);
        return GSON.toJson(response);
    }

    @ApiOperation(value = "Returns a printshop", notes = "This method allows manager to get his printshop info")
    @Secured({"ROLE_MANAGER"})
    @RequestMapping(value = "/printshop", method = RequestMethod.GET)
    public String getManagerPrintShop(Principal principal) {
        Manager m = managers.findByUsername(principal.getName());
        PrintShop pShop = m.getPrintShop();

        JsonObject response = new JsonObject();

        if (pShop == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }

        response.add("printshop", GSON.toJsonTree(pShop));
        response.addProperty("success", true);
        return GSON.toJson(response);
    }
}
