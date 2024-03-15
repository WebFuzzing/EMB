package org.cbioportal.session_service.web;

import org.springframework.web.bind.annotation.*;

/**
 * @author Hongxin Zhang
 */
@RestController
@RequestMapping(value = "/info")
public class InfoController {

    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

    @RequestMapping(method = RequestMethod.GET, value = "")
    public String getInfo() {
        return this.getVersion();
    }
}
