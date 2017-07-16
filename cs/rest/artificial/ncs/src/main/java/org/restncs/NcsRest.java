package org.restncs;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.restncs.imp.TriangleClassification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class NcsRest {

    @ApiOperation("Check the triangle type of the given three edges")
    @RequestMapping(
            value = "/{a}/{b}/{c}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Dto> checkTriangle(
            @ApiParam("First edge")
            @PathVariable("a") Integer a,
            @ApiParam("Second edge")
            @PathVariable("b") Integer b,
            @ApiParam("Third edge")
            @PathVariable("c") Integer c
    ){

        Dto dto = new Dto();
        dto.resultAsInt = TriangleClassification.classify(a,b,c);

        return ResponseEntity.ok(dto);
    }
}
