package org.devgateway.ocds.web.cache.generators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

/**
 * @author idobre
 * @since 8/17/16
 *
 * {@link KeyGenerator} that uses all parameters to create a key.
 * This KeyGenerator is used to cache Excel Charts data
 */
public class GenericExcelChartKeyGenerator implements KeyGenerator {
    private final Logger logger = LoggerFactory.getLogger(GenericExcelChartKeyGenerator.class);

    private final ObjectMapper objectMapper;

    public GenericExcelChartKeyGenerator(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Object generate(final Object target, final Method method, final Object... params) {
        if (params.length < 1) {
            throw new RuntimeException(
                    "Wrong parameters received for generating custom GenericExcelChartKeyGenerator key!");
        }

        try {
            StringBuilder key = new StringBuilder(method.toString());
            for (Object param : params) {
                key.append(objectMapper.writeValueAsString(param));
            }

            return key.toString().hashCode();
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
