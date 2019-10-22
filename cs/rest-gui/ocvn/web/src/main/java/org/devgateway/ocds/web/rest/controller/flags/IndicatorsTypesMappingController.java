package org.devgateway.ocds.web.rest.controller.flags;

import io.swagger.annotations.ApiOperation;
import org.devgateway.ocds.persistence.mongo.flags.ReleaseFlags;
import org.devgateway.ocds.web.spring.ReleaseFlaggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mpostelnicu on 28-Mar-17.
 */
@RestController
@Cacheable
@CacheConfig(cacheNames = "indicatorTypesMappingJson")
public class IndicatorsTypesMappingController {

    @Autowired
    private ReleaseFlaggingService releaseFlaggingService;

    @RequestMapping(value = "/api/indicatorTypesMapping", method = {RequestMethod.POST,
            RequestMethod.GET}, produces = "application/json")
    @ApiOperation(value = "Returns a stub static mapping of indicator-to-type list. Ignore the rationale part."
            + " This uses the indicator processor over a stub release simply to populate its flags attribute"
            + " with indicator info.")
    public ReleaseFlags indicatorTypesMapping() {
        return releaseFlaggingService.createStubFlagTypes();
    }

}
