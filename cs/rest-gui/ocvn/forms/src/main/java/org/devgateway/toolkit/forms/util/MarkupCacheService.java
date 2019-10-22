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
package org.devgateway.toolkit.forms.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.Statistics;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.MarkupCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * @author idobre
 * @since 3/3/16
 *
 *        Class the removes the cache created in
 *        org.devgateway.ccrs.web.wicket.page.reports.AbstractReportPage#ResourceStreamPanel#getCacheKey
 *        function
 */
@Component
@Profile("reports")
public class MarkupCacheService {
    protected static final Logger logger = LoggerFactory.getLogger(MarkupCacheService.class);

    /**
     * start-key used to identify the reports markup
     */
    private static final String START_NAME_REPORT_KEY = "REPORTMARKUP";

    /**
     * Flush markup cache for reports page
     */
    public final void flushMarkupCache() {
        final MarkupCache markupCacheClass = (MarkupCache) MarkupCache.get();
        final MarkupCache.ICache<String, Markup> markupCache = markupCacheClass.getMarkupCache();
        final Collection<String> keys = markupCache.getKeys();
        for (String key : keys) {
            // The key for reports markup cache contains the class name so it
            // should end in "ReportPage"
            if (key.startsWith(START_NAME_REPORT_KEY)) {
                markupCacheClass.removeMarkup(key);
            }
        }
    }

    /**
     * Add the content of a report (PDF, Excel, RTF) to cache
     *
     * @param outputType
     * @param reportName
     * @param parameters
     * @param buffer
     */
    public void addReportToCache(final String outputType, final String reportName, final String parameters,
                                 final byte[] buffer) {
        CacheManager cm = CacheManager.getInstance();

        // get the reports cache "reportsCache", declared in ehcache.xml
        Cache cache = cm.getCache("reportsCache");

        cache.put(new Element(createCacheKey(outputType, reportName, parameters), buffer));
    }

    /**
     * Fetch the content of a report from cache
     *
     * @param outputType
     * @param reportName
     * @param parameters
     * @return
     */
    public byte[] getReportFromCache(final String outputType, final String reportName, final String parameters) {
        CacheManager cm = CacheManager.getInstance();

        // get the reports cache "reportsCache", declared in ehcache.xml
        Cache cache = cm.getCache("reportsCache");

        String key = createCacheKey(outputType, reportName, parameters);

        if (cache.isKeyInCache(key)) {
            return (byte[]) cache.get(key).getObjectValue();
        }

        return null;
    }

    /**
     * Display some statistics about reports cache
     */
    public void getReportsStat() {
        CacheManager cm = CacheManager.getInstance();

        // get the reports cache "reportsCache", declared in ehcache.xml
        Cache cache = cm.getCache("reportsCache");

        @SuppressWarnings("unchecked")
        List<String> cacheKeys = cache.getKeys();
        long size = 0;
        for (String k : cacheKeys) {
            logger.info("key: " + k);
            byte[] buf = (byte[]) cache.get(k).getObjectValue();
            size += buf.length;
        }
        Statistics stats = cache.getStatistics();
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("%s objects, %s hits, %s misses\n", stats.getObjectCount(), stats.getCacheHits(),
                stats.getCacheMisses()));

        logger.info(String.valueOf(sb));
        logger.info("cache total size: " + FileUtils.byteCountToDisplaySize(size));
    }

    /**
     * Remove from cache all reports content
     */
    public void clearReportsCache() {
        CacheManager cm = CacheManager.getInstance();

        // get the reports cache "reportsCache", declared in ehcache.xml
        Cache cache = cm.getCache("reportsCache");

        if (cache != null) {
            cache.removeAll();
        }
    }

    /**
     * Remove from cache all reports api content
     */
    public void clearReportsApiCache() {
        CacheManager cm = CacheManager.getInstance();

        // get the reports cache "reportsApiCache", declared in ehcache.xml
        Cache cache = cm.getCache("reportsApiCache");

        if (cache != null) {
            cache.removeAll();
        }
    }

    private String createCacheKey(final String outputType, final String reportName, final String parameters) {
        return reportName + "-" + parameters + "-" + outputType;
    }
}
