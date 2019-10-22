/*******************************************************************************
 * Copyright (c) 2015 Development Gateway, Inc and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * https://opensource.org/licenses/MIT
 *
 * Contributors:
 * Development Gateway - initial API and implementation
 *******************************************************************************/
package org.devgateway.ocds.web.spring;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.devgateway.ocds.persistence.mongo.spring.ExcelImportService;
import org.devgateway.ocds.persistence.mongo.spring.ImportResult;
import org.devgateway.ocds.web.util.SettingsUtils;
import org.devgateway.ocvn.persistence.mongo.dao.ImportFileTypes;
import org.devgateway.toolkit.persistence.dao.AdminSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledExcelImportService {

    private static final Logger LOGGER = Logger.getLogger(ScheduledExcelImportService.class);

    private static final String MAIN_FILE = "egp.xlsx";
    private static final String ORGS_FILE = "UM_PUBINSTITU_SUPPLIERS.xlsx";
    private static final String LOCATIONS_FILE = "Location_Table_Geocoded.xlsx";
    private static final String CITY_DEPARTMENT_GROUP_FILE = "OCVN_city_department_group.xlsx";

    @Autowired
    private ExcelImportService excelImportService;

    @Autowired
    private SendEmailService sendEmailService;

    @Autowired
    private SettingsUtils settingsUtils;

    @Scheduled(cron = "0 0 3 * * ?")
    public void excelImportService() {

        AdminSettings settings = settingsUtils.getSettings();

        excelImportService(settings.getImportFilesPath() + File.separator + MAIN_FILE,
                settings.getImportFilesPath() + File.separator + LOCATIONS_FILE,
                settings.getImportFilesPath() + File.separator + ORGS_FILE,
                settings.getImportFilesPath() + File.separator + CITY_DEPARTMENT_GROUP_FILE, false);
    }


    public byte[] getByteArrayForResourceWithPath(String path, Boolean resource) throws IOException {
        return resource ? IOUtils.toByteArray(getClass().getResourceAsStream(path))
                : FileUtils.readFileToByteArray(new File(path));
    }

    public boolean excelImportService(String prototypeDatabasePath, String
            locationsPath, String publicInstitutionsSuppliers, String cdg, Boolean resource) {

        AdminSettings settings = settingsUtils.getSettings();

        if (BooleanUtils.isFalse(settings.getEnableDailyAutomatedImport())) {
            return true;
        }

        ImportResult result = null;

        try {
            result = excelImportService.importAllSheets(ImportFileTypes.ALL_FILE_TYPES,
                    getByteArrayForResourceWithPath(prototypeDatabasePath, resource),
                    getByteArrayForResourceWithPath(locationsPath, resource),
                    getByteArrayForResourceWithPath(publicInstitutionsSuppliers, resource),
                    getByteArrayForResourceWithPath(cdg, resource),
                    true, true, true
            );

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e);
            result = new ImportResult(false, new StringBuffer("Error during import: ").append(e.getMessage()));
        }

        if (!result.getSuccess()) {
            sendEmailService.sendEmail("Excel import failed!", result.getMsgBuffer().toString(),
                    settings.getAdminEmail());
        }

        return result.getSuccess();
    }
}