package org.devgateway.ocds.persistence.mongo.spring;

import java.util.List;

/**
 * @author idobre
 * @since 5/20/16
 *
 *        Service that imports Excel sheets in OCDS format
 */
public interface ExcelImportService extends ImportService {
    void newMsgBuffer();

    StringBuffer getMsgBuffer();

    void importAllSheets(List<String> fileTypes, byte[] prototypeDatabase, byte[] locations,
            byte[] publicInstitutionsSuppliers, byte[] cdg, Boolean purgeDatabase, Boolean validateData,
            Boolean cleanData) throws InterruptedException;
}
