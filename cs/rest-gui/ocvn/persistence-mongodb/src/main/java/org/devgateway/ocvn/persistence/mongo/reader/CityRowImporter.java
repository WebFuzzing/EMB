package org.devgateway.ocvn.persistence.mongo.reader;

import java.text.ParseException;

import org.devgateway.ocds.persistence.mongo.reader.ImportWarningRuntimeException;
import org.devgateway.ocds.persistence.mongo.reader.RowImporter;
import org.devgateway.ocds.persistence.mongo.spring.ImportService;
import org.devgateway.ocvn.persistence.mongo.dao.City;
import org.devgateway.ocvn.persistence.mongo.repository.main.CityRepository;

/**
 * @author mpostelnicu Specific {@link RowImporter} Cities. Organization#address
 */
public class CityRowImporter extends RowImporter<City, Integer, CityRepository> {
    
    public CityRowImporter(final CityRepository repository, final ImportService importService,
            final int skipRows) {
        super(repository, importService, skipRows);
    }
    
    @Override
    public void importRow(final String[] row) throws ParseException {
        if (getRowCell(row, 1) == null) {
            throw new RuntimeException("Main identifier empty!");
        }
        City city = repository.findOne(getInteger(getRowCell(row, 1)));
        
        if (city != null) {
            throw new ImportWarningRuntimeException("Duplicate identifer for city " + city);
        }
        city = new City();
        
        city.setId(getInteger(getRowCell(row, 1)));
        city.setName(getRowCell(row, 2));
        
        repository.insert(city);
        
    }
    
}
