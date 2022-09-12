package org.cbioportal.genome_nexus.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfo;
import org.cbioportal.genome_nexus.service.MyVariantInfoService;
import org.cbioportal.genome_nexus.service.exception.MyVariantInfoNotFoundException;
import org.cbioportal.genome_nexus.service.exception.MyVariantInfoWebServiceException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.cbioportal.genome_nexus.web.config.InternalApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@InternalApi
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "my-variant-info-controller", description = "My Variant Info Controller")
public class MyVariantInfoController
{
    private MyVariantInfoService myVariantInfoService;

    @Autowired
    public void MyVaraintInfoController(MyVariantInfoService myVariantInfoService)
    {
        this.myVariantInfoService = myVariantInfoService;
    }

    @ApiOperation(value = "Retrieves myvariant information for the provided list of variants",
        nickname = "fetchMyVariantInfoAnnotationGET")
    @RequestMapping(value = "/my_variant_info/variant/{variant:.+}",
        method = RequestMethod.GET,
        produces = "application/json")
    public MyVariantInfo fetchMyVariantInfoAnnotationGET(
        @ApiParam(value=". For example 7:g.140453136A>T",
            required = true,
            allowMultiple = true)
        @PathVariable String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException,
        MyVariantInfoWebServiceException, MyVariantInfoNotFoundException
    {
        return this.myVariantInfoService.getMyVariantInfoByHgvsVariant(variant);
    }

    @ApiOperation(value = "Retrieves myvariant information for the provided list of variants",
        nickname = "postMyVariantInfoAnnotation")
    @RequestMapping(value = "/my_variant_info/variant",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<MyVariantInfo> fetchMyVariantInfoAnnotationPOST(
        @ApiParam(value="List of variants. For example [\"7:g.140453136A>T\",\"12:g.25398285C>A\"]",
            required = true,
            allowMultiple = true)
        @RequestBody List<String> variants)
    {
        return this.myVariantInfoService.getMyVariantInfoByHgvsVariant(variants);
    }
}
