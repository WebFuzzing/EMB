/**
 * 
 */
package org.devgateway.toolkit.web;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.devgateway.toolkit.web.rest.controller.DummyController;
import org.devgateway.toolkit.web.rest.entity.Dummy;
import org.devgateway.toolkit.web.spring.AsyncControllerLookupService;
import org.devgateway.toolkit.web.spring.util.AsyncBeanParamControllerMethodCallable;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author mpostelnicu
 *
 */
public class AsyncControllerLookupServiceTest extends AbstractWebTest {

    @Autowired
    private AsyncControllerLookupService lookupService;

    @Autowired
    private DummyController dummyController;

    @Test
    public void testThreadedLookup() throws InterruptedException, ExecutionException {

        Future<Dummy> joe = lookupService.asyncInvoke(new AsyncBeanParamControllerMethodCallable<Dummy, String>() {
            @Override
            public Dummy invokeControllerMethod(final String filter) {
                return dummyController.greeting(filter);
            }
        }, "Joe");

        Future<Dummy> wendy = lookupService.asyncInvoke(new AsyncBeanParamControllerMethodCallable<Dummy, String>() {
            @Override
            public Dummy invokeControllerMethod(final String filter) {
                return dummyController.greeting(filter);
            }
        }, "Wendy");

        lookupService.waitTillDone(joe, wendy);

        Assert.assertEquals("Hello, Joe!", joe.get().getContent());
        Assert.assertEquals("Hello, Wendy!", wendy.get().getContent());
    }

}
