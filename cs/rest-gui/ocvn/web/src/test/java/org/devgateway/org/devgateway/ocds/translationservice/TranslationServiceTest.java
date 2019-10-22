package org.devgateway.org.devgateway.ocds.translationservice;

import org.devgateway.ocds.web.spring.TranslationService;
import org.devgateway.toolkit.web.AbstractWebTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by mpostelnicu on 2/15/17.
 */
public class TranslationServiceTest extends AbstractWebTest {

    @Autowired
    private TranslationService translationService;

    @Test
    public void testKeys() {

        Assert.assertEquals("Log in", translationService.getValue("en_US", "general:login"));

        Assert.assertEquals("đăng nhập", translationService.getValue("vn_VN", "general:login"));

    }
}
