package org.devgateway.ocds.web.rest.controller.test;

import org.devgateway.ocds.web.spring.ScheduledExcelImportService;
import org.devgateway.ocds.web.spring.SendEmailService;
import org.devgateway.ocds.web.util.SettingsUtils;
import org.devgateway.toolkit.persistence.dao.AdminSettings;
import org.devgateway.toolkit.web.AbstractWebTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;


import static junit.framework.TestCase.assertTrue;

/**
 * @author mpostelnicu
 */
public class ScheduledExcelImportServiceTest extends AbstractWebTest {

    @Mock
    private SettingsUtils settingsUtils;

    @Mock
    private SendEmailService sendEmailService;

    @InjectMocks
    @Autowired
    public ScheduledExcelImportService scheduledExcelImportService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        AdminSettings mockSettings = new AdminSettings();
        mockSettings.setAdminEmail("mpostelnicu@developmentgateway.org");
        mockSettings.setEnableDailyAutomatedImport(true);
        mockSettings.setImportFilesPath("/");
        Mockito.when(settingsUtils.getSettings()).thenReturn(mockSettings);
    }

    @Test
    public void testScheduledExcelImportService() {
        assertTrue(scheduledExcelImportService.excelImportService(VNImportAndEndpointsTest.PROTOTYPE_DB_TEST_FILE,
                VNImportAndEndpointsTest.LOCATION_TEST_FILE,
                VNImportAndEndpointsTest.ORGS_TEST_FILE,
                VNImportAndEndpointsTest.CITY_DEPT_GROUP_TEST_FILE, true));
    }

}
