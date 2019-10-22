package org.devgateway.ocds.web.rest.controller.excelchart;

import java.io.File;
import java.util.List;
import org.apache.log4j.Logger;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.repository.main.ReleaseRepository;
import org.devgateway.ocds.persistence.mongo.spring.json.JsonImportPackage;
import org.devgateway.ocds.persistence.mongo.spring.json.ReleasePackageJsonImport;
import org.devgateway.ocds.web.rest.controller.request.LangGroupingFilterPagingRequest;
import org.devgateway.ocds.web.rest.controller.request.LangYearFilterPagingRequest;
import org.devgateway.ocds.web.spring.TranslationService;
import org.devgateway.ocds.web.util.SettingsUtils;
import org.devgateway.toolkit.web.AbstractWebTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author idobre
 * @since 9/14/16
 *
 * Class that imports some test releases from 'endpoint-data-test.json' file
 * and is used to test each excel chart endpoint.
 */
public abstract class AbstractExcelControllerTest extends AbstractWebTest {
    protected static Logger logger = Logger.getLogger(AbstractExcelControllerTest.class);

    protected MockHttpServletResponse mockHttpServletResponse;

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    protected TranslationService translationService;

    @Before
    public final void setUp() throws Exception {
        // be sure that the release collection is empty
        releaseRepository.deleteAll();

        // clean the cache (we need this especially for endpoints cache)
        if (cacheManager != null) {
            cacheManager.getCacheNames().forEach(c -> cacheManager.getCache(c).clear());
        }

        final ClassLoader classLoader = getClass().getClassLoader();

        final File file = new File(classLoader.getResource("json/endpoint-data-test.json").getFile());
        final JsonImportPackage releasePackageJsonImport = new ReleasePackageJsonImport(releaseRepository, file, true);
        releasePackageJsonImport.importObjects();

        mockHttpServletResponse = new MockHttpServletResponse();
    }

    @After
    public void tearDown() {
        // be sure to clean up the release collection
        releaseRepository.deleteAll();
    }

    @Test
    public void testImportForEndpoints() {
        // just test that the import was done correctly
        final List<Release> importedRelease = releaseRepository.findAll();
        Assert.assertNotNull(importedRelease);
        Assert.assertEquals(3, importedRelease.size());
    }


    public LangYearFilterPagingRequest getLangYearFilterMockRequest() {
        LangYearFilterPagingRequest filter = new LangYearFilterPagingRequest();
        filter.setLanguage(SettingsUtils.DEFAULT_LANGUAGE);
        return filter;
    }

    public LangGroupingFilterPagingRequest getLangGroupingFilterMockRequest() {
        LangGroupingFilterPagingRequest filter = new LangGroupingFilterPagingRequest();
        filter.setLanguage(SettingsUtils.DEFAULT_LANGUAGE);
        return filter;
    }
}
