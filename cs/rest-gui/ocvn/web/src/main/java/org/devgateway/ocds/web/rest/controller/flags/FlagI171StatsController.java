package org.devgateway.ocds.web.rest.controller.flags;

import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import org.devgateway.ocds.persistence.mongo.flags.FlagsConstants;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by mpostelnicu on 12/2/2016.
 */
@RestController
@CacheConfig(keyGenerator = "genericPagingRequestKeyGenerator", cacheNames = "genericPagingRequestJson")
@Cacheable
public class FlagI171StatsController extends AbstractFlagStatsController {

    @Override
    protected String getFlagProperty() {
        return FlagsConstants.I171_VALUE;
    }

    @Override
    @ApiOperation(value = "Stats for flag i171")
    @RequestMapping(value = "/api/flags/i171/stats",
            method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json")
    public List<DBObject> flagStats(@ModelAttribute @Valid YearFilterPagingRequest filter) {
        return super.flagStats(filter);
    }
}
