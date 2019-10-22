package org.devgateway.ocds.web.flags.release.vietnam;

import com.google.common.collect.ImmutableMap;
import org.devgateway.ocds.web.flags.release.ReleaseFlagI004Processor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

/**
 * Created by mpost on 12/22/2016.
 */
@Component
public class VietnamReleaseFlagI004Processor extends ReleaseFlagI004Processor {

    @Override
    public ImmutableMap<String, BigDecimal> getSolesourceLimits() {
        ImmutableMap<String, BigDecimal> map = ImmutableMap.<String, BigDecimal>builder()
                .put("1", BigDecimal.valueOf(2000000000d))
                .put("3", BigDecimal.valueOf(5000000000d))
                .put("5", BigDecimal.valueOf(3000000000d))
                .put("10", BigDecimal.valueOf(5000000000d))
                .build();
        return map;
    }

    @Override
    @PostConstruct
    protected void setPredicates() {

    }
}
