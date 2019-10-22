/**
 * 
 */
package org.devgateway.toolkit.web.spring;

import java.util.concurrent.Future;

import org.devgateway.toolkit.web.spring.util.AsyncBeanParamControllerMethodCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

/**
 * @author mpostelnicu Service designed to run a
 *         {@link AsyncBeanParamControllerMethodCallable} using a {@link Object}
 *         as its parameter. and return the results in an {@link Async} way.
 */
@Service
public class AsyncControllerLookupService {

    private static final int PAUSE_BW_CHECKS_MILLIS = 10;

    private static final Logger logger = LoggerFactory.getLogger(AsyncControllerLookupService.class);

    @Async
    public <RET, FILT> Future<RET> asyncInvoke(final AsyncBeanParamControllerMethodCallable<RET, FILT> callable,
            final FILT filter) {
        logger.debug("Invoking " + callable);
        return new AsyncResult<>(callable.invokeControllerMethod(filter));
    }

    /**
     * Convenience method that waits until the entire list of {@link Future}
     * tasks is done executing. that means {@link Future#isDone()} is true for
     * all. Invoking this method will block current thread until all tasks are
     * done.
     * 
     * @param future
     *            the list of {@link Future} tasks to wait execution
     */
    public void waitTillDone(final Future<?>... future) {
        boolean done = false;
        while (!done) {
            done = true;
            for (int i = 0; i < future.length; i++) {
                if (!future[i].isDone()) {
                    done = false;
                    logger.debug("Still waiting for at least one task to finalize!");
                    break;
                }
            }
            try {
                Thread.sleep(PAUSE_BW_CHECKS_MILLIS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        logger.debug("All tasks done!");
    }

}
