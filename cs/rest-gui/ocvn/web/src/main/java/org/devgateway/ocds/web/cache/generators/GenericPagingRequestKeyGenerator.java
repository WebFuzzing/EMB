package org.devgateway.ocds.web.cache.generators;

import java.lang.reflect.Method;

import org.devgateway.ocds.web.rest.controller.request.GenericPagingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author mpostelnicu {@link KeyGenerator} for {@link RequestMapping}S that use
 *         {@link GenericPagingRequest} This will use the default
 *         Jackson's {@link ObjectMapper} to produce JSON from the input bean
 *         plus takes into account the target (
 */
public class GenericPagingRequestKeyGenerator implements KeyGenerator {

    private final Logger logger = LoggerFactory.getLogger(GenericPagingRequestKeyGenerator.class);

    private final ObjectMapper objectMapper;

    public GenericPagingRequestKeyGenerator(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.cache.interceptor.KeyGenerator#generate(java.lang.
     * Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object generate(final Object target, final Method method, final Object... params) {
        if (params.length != 1 || !(params[0] instanceof GenericPagingRequest)) {
            throw new RuntimeException(
                    "Wrong parameters received for generating custom GenericPagingRequest key!");
        }

        try {
            return new StringBuilder(method.toString())
                    .append(objectMapper.writeValueAsString(params[0])).toString().hashCode();
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
