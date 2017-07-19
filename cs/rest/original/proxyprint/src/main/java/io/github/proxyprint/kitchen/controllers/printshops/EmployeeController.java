package io.github.proxyprint.kitchen.controllers.printshops;

import com.google.gson.Gson;
import io.github.proxyprint.kitchen.models.consumer.printrequest.Document;
import io.github.proxyprint.kitchen.models.printshops.Employee;
import io.github.proxyprint.kitchen.models.repositories.DocumentDAO;
import io.github.proxyprint.kitchen.models.repositories.EmployeeDAO;
import java.io.File;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import javax.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by daniel on 14-05-2016.
 */
@RestController 
@Transactional
public class EmployeeController {

    @Resource(name = "documentsPath")
    private String documentsPath;
    @Autowired
    private EmployeeDAO employees;
    @Autowired
    private DocumentDAO documents;
    @Autowired
    private Gson GSON;

    @ApiOperation(value = "Returns a document", notes = "This method allows employees to get a document by a given id.")
    @Secured("ROLE_EMPLOYEE")
    @RequestMapping(value = "/documents/{id}", method = RequestMethod.GET)
    public ResponseEntity<FileSystemResource> getEmployees(@PathVariable(value = "id") long id, Principal principal) {
        HttpHeaders headers = new HttpHeaders();

        Employee emp = this.employees.findByUsername(principal.getName());
        Document doc = this.documents.findOne(id);

        if (doc == null) {
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity(headers, HttpStatus.NOT_FOUND);
        } else if (emp.getPrintShop().getId() != doc.getPrintRequest().getPrintshop().getId()) {
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity(headers, HttpStatus.UNAUTHORIZED);
        } else {
            headers.setContentType(MediaType.parseMediaType("application/pdf"));
            headers.add("Content-Disposition", "inline;filename=" + doc.getName() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity(new FileSystemResource(new File(this.documentsPath+doc.getFile().getName())), headers, HttpStatus.OK);
        }
    }
}
