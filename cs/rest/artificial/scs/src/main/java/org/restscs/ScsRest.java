package org.restscs;

import org.restscs.imp.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class ScsRest {

    @GetMapping(path = "/calc/{op}/{arg1}/{arg2}")
    public ResponseEntity<String> calc(
            @PathVariable("op")
                    String op,
            @PathVariable("arg1")
                    double arg1,
            @PathVariable("arg2")
                    double arg2
    ) {
        String res = Calc.subject(op, arg1, arg2);
        return ResponseEntity.ok(res);
    }


    @GetMapping(path = "/cookie/{name}/{val}/{site}")
    public ResponseEntity<String> cookie(
            @PathVariable("name")
                    String name,
            @PathVariable("val")
                    String val,
            @PathVariable("site")
                    String site
    ) {
        String res = Cookie.subject(name, val, site);
        return ResponseEntity.ok(res);
    }


    @GetMapping(path = "/costfuns/{i}/{s}")
    public ResponseEntity<String> costfuns(
            @PathVariable("i")
                    Integer i,
            @PathVariable("s")
                    String s
    ) {
        String res = Costfuns.subject(i, s);
        return ResponseEntity.ok(res);
    }


    @GetMapping(path = "/dateparse/{dayname}/{monthname}")
    public ResponseEntity<String> dateParse(
            @PathVariable("dayname")
                    String dayname,
            @PathVariable("monthname")
                    String monthname
    ) {
        String res = DateParse.subject(dayname, monthname);
        return ResponseEntity.ok(res);
    }


    @GetMapping(path = "/filesuffix/{directory}/{file}")
    public ResponseEntity<String> fileSuffix(
            @PathVariable("directory")
                    String directory,
            @PathVariable("file")
                    String file
    ) {
        String res = FileSuffix.subject(directory, file);
        return ResponseEntity.ok(res);
    }



    @GetMapping(path = "/notypevar/{i}/{s}")
    public ResponseEntity<String> notyPevar(
            @PathVariable("i")
                    Integer i,
            @PathVariable("s")
                    String s
    ) {
        String res = NotyPevar.subject(i, s);
        return ResponseEntity.ok(res);
    }


    @GetMapping(path = "/ordered4/{w}/{x}/{z}/{y}")
    public ResponseEntity<String> ordered4(
            @PathVariable("w")
                    String w,
            @PathVariable("x")
                    String x,
            @PathVariable("z")
                    String z,
            @PathVariable("y")
                    String y
    ) {
        String res = Ordered4.subject(w, x, z, y);
        return ResponseEntity.ok(res);
    }


    @GetMapping(path = "/pat/{txt}/{pat}")
    public ResponseEntity<String> pat(
            @PathVariable("txt")
                    String txt,
            @PathVariable("pat")
                    String pat
    ) {
        String res = Pat.subject(txt, pat);
        return ResponseEntity.ok(res);
    }


    @GetMapping(path = "/pat/{txt}")
    public ResponseEntity<String> regex(
            @PathVariable("txt")
                    String txt
    ) {
        String res = Regex.subject(txt);
        return ResponseEntity.ok(res);
    }


    @GetMapping(path = "/text2txt/{word1}/{word2}/{word3}")
    public ResponseEntity<String> text2txt(
            @PathVariable("word1")
                    String word1,
            @PathVariable("word2")
                    String word2,
            @PathVariable("word3")
                    String word3
    ) {
        String res = Text2Txt.subject(word1, word2, word3);
        return ResponseEntity.ok(res);
    }


    @GetMapping(path = "/title/{sex}/{title}")
    public ResponseEntity<String> title(
            @PathVariable("sex")
                    String sex,
            @PathVariable("title")
                    String title
    ) {
        String res = Title.subject(sex, title);
        return ResponseEntity.ok(res);
    }

}
