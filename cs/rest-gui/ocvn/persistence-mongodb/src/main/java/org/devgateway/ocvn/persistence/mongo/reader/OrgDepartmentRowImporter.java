package org.devgateway.ocvn.persistence.mongo.reader;

import java.text.ParseException;
import org.devgateway.ocds.persistence.mongo.reader.ImportWarningRuntimeException;
import org.devgateway.ocds.persistence.mongo.reader.RowImporter;
import org.devgateway.ocds.persistence.mongo.spring.ImportService;
import org.devgateway.ocvn.persistence.mongo.dao.OrgDepartment;
import org.devgateway.ocvn.persistence.mongo.repository.main.OrgDepartmentRepository;

/**
 * @author mpostelnicu Specific {@link RowImporter} OrgDepartments. Organization#address
 */
public class OrgDepartmentRowImporter extends RowImporter<OrgDepartment, Integer, OrgDepartmentRepository> {
    
    public OrgDepartmentRowImporter(final OrgDepartmentRepository repository, final ImportService importService,
            final int skipRows) {
        super(repository, importService, skipRows);
    }
    
    @Override
    public void importRow(final String[] row) throws ParseException {
        if (getRowCell(row, 1) == null) {
            throw new RuntimeException("Main identifier empty!");
        }
        OrgDepartment d = repository.findOne(getInteger(getRowCell(row, 1)));
        
        if (d != null) {
            throw new ImportWarningRuntimeException("Duplicate identifer for organization department " + d);
        }
        d = new OrgDepartment();
        
        d.setId(getInteger(getRowCell(row, 1)));
        d.setName(getRowCell(row, 2));
        
        repository.insert(d);
        
    }
    
}
