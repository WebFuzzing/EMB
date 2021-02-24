package org.restncs;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.restncs.imp.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
public class NcsRest {

    @ApiOperation("Check the triangle type of the given three edges")
    @GetMapping(
            value = "/triangle/{a}/{b}/{c}",
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


    @GetMapping(
            value = "/bessj/{n}/{x}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Dto> bessj(
            @PathVariable("n") Integer n,
            @PathVariable("x") Double x
    ){
        if(n <= 2 || n > 1000){
            return ResponseEntity.status(400).build();
        }

        Dto dto = new Dto();
        Bessj bessj = new Bessj();
        dto.resultAsDouble = bessj.bessj(n, x);

        return ResponseEntity.ok(dto);
    }


    @GetMapping(
            value = "/expint/{n}/{x}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Dto> expint(
            @PathVariable("n") Integer n,
            @PathVariable("x") Double x
    ){

        try{
            Dto dto = new Dto();
            dto.resultAsDouble = Expint.exe(n,x);
            return ResponseEntity.ok(dto);
        }catch (RuntimeException e){
            return ResponseEntity.status(400).build();
        }
    }


    @GetMapping(
            value = "/fisher/{m}/{n}/{x}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Dto> fisher(
            @PathVariable("m") Integer m,
            @PathVariable("n") Integer n,
            @PathVariable("x") Double x
    ){

        if(m > 1000 || n > 1000){
            return ResponseEntity.status(400).build();
        }

        try{
            Dto dto = new Dto();
            dto.resultAsDouble = Fisher.exe(m, n, x);
            return ResponseEntity.ok(dto);
        }catch (RuntimeException e){
            return ResponseEntity.status(400).build();
        }
    }


    @GetMapping(
            value = "/gammq/{a}/{x}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Dto> gammq(
            @PathVariable("a") Double a,
            @PathVariable("x") Double x
    ){

        try{
            Dto dto = new Dto();
            Gammq gammq = new Gammq();
            dto.resultAsDouble = gammq.exe(a, x);
            return ResponseEntity.ok(dto);
        }catch (RuntimeException e){
            return ResponseEntity.status(400).build();
        }
    }


    @GetMapping(
            value = "/remainder/{a}/{b}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Dto> remainder(
            @PathVariable("a") Integer a,
            @PathVariable("b") Integer b
    ){
        int lim = 10_000;
        if(a > lim || a < -lim || b > lim || b < -lim){
            return ResponseEntity.status(400).build();
        }

        Dto dto = new Dto();
        dto.resultAsInt = Remainder.exe(a,b);

        return ResponseEntity.ok(dto);
    }
}
