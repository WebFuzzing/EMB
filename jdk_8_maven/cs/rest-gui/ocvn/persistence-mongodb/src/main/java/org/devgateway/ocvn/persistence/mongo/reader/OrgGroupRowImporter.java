package org.devgateway.ocvn.persistence.mongo.reader;

import java.text.ParseException;
import org.devgateway.ocds.persistence.mongo.reader.ImportWarningRuntimeException;
import org.devgateway.ocds.persistence.mongo.reader.RowImporter;
import org.devgateway.ocds.persistence.mongo.spring.ImportService;
import org.devgateway.ocvn.persistence.mongo.dao.OrgGroup;
import org.devgateway.ocvn.persistence.mongo.repository.main.OrgGroupRepository;

/**
 * @author mpostelnicu Specific {@link RowImporter} Cities. Organization#address
 */
public class OrgGroupRowImporter extends RowImporter<OrgGroup, Integer, OrgGroupRepository> {
    
    public OrgGroupRowImporter(final OrgGroupRepository repository, final ImportService importService,
            final int skipRows) {
        super(repository, importService, skipRows);
    }
    
    @Override
    public void importRow(final String[] row) throws ParseException {
        if (getRowCell(row, 1) == null) {
            throw new RuntimeException("Main identifier empty!");
        }
        OrgGroup g = repository.findOne(getInteger(getRowCell(row, 1)));
        
        if (g != null) {
            throw new ImportWarningRuntimeException("Duplicate identifer for org group " + g);
        }
        g = new OrgGroup();
        
        g.setId(getInteger(getRowCell(row, 1)));
        g.setName(getRowCell(row, 2));
        
        repository.insert(g);
        
    }
    
}
