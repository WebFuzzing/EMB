/*
 * Copyright (c) 2016 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal Session Service.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.cbioportal.session_service.web;

import org.cbioportal.session_service.domain.*;
import org.cbioportal.session_service.service.exception.*;
import org.cbioportal.session_service.service.SessionService;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.annotations.ApiParam;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Manda Wilson 
 */
@RestController // shorthand for @Controller, @ResponseBody
@RequestMapping(value = "/api/sessions/")
@EnableWebSecurity
public class SessionServiceController  extends WebSecurityConfigurerAdapter {
    @Value("${security.basic.enabled:false}")
    private boolean securityEnabled;

    @Autowired
    private SessionService sessionService;

    @RequestMapping(method = RequestMethod.POST, value="/{source}/{type}")
    @JsonView(Session.Views.IdOnly.class)
    public Session addSession(@PathVariable String source, 
        @PathVariable SessionType type, 
        @RequestBody String data) { 
        return sessionService.addSession(source, type, data);
    }

    @RequestMapping(method = RequestMethod.GET, value="/{source}/{type}")
    @JsonView(Session.Views.Full.class)
    public Iterable<Session> getSessions(@PathVariable String source, 
        @PathVariable SessionType type) {
        return sessionService.getSessions(source, type);
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/{source}/{type}/query")
    @JsonView(Session.Views.Full.class)
    public Iterable<Session> getSessionsByQuery(@PathVariable String source, 
        @PathVariable SessionType type, 
        @RequestParam(name="field") String field,
        @RequestParam(name="value") String value) {
        String query = "{\""+field+"\":\""+value+"\"}";
        return sessionService.getSessionsByQuery(source, type, query);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{source}/{type}/query/fetch")
    @JsonView(Session.Views.Full.class)
    public Iterable<Session> fetchSessionsByQuery(@PathVariable String source,
            @PathVariable SessionType type,
            @ApiParam(required = true, value = "selection filter similar to mongo filter")
            @RequestBody String query) {
        return sessionService.getSessionsByQuery(source, type, query);
    }

    @RequestMapping(value = "/{source}/{type}/{id}", method = RequestMethod.GET)
    @JsonView(Session.Views.Full.class)
    public Session getSession(@PathVariable String source, 
        @PathVariable SessionType type,
        @PathVariable String id) {
        return sessionService.getSession(source, type, id);
    }

    @RequestMapping(value = "/{source}/{type}/{id}", method = RequestMethod.PUT)
    public void updateSession(@PathVariable String source, 
        @PathVariable SessionType type,
        @PathVariable String id, 
        @RequestBody String data) {
        sessionService.updateSession(source, type, id, data);
    }

    @RequestMapping(value = "/{source}/{type}/{id}", method = RequestMethod.DELETE)
    public void deleteSession(@PathVariable String source, 
        @PathVariable SessionType type,
        @PathVariable String id) {
        sessionService.deleteSession(source, type, id);
    } 

    @ExceptionHandler
    public void handleSessionInvalid(SessionInvalidException e, HttpServletResponse response) 
        throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler
    public void handleSessionQueryInvalid(SessionQueryInvalidException e, HttpServletResponse response) 
        throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
    
    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Session not found")
    @ExceptionHandler(SessionNotFoundException.class)
    public void handleSessionNotFound() {}
    
    @ExceptionHandler
    public void handleSessionTypeInvalid(MethodArgumentTypeMismatchException e, HttpServletResponse response)
            throws IOException {
        if (e.getRequiredType() == SessionType.class) {
            List<String> validTypes = Stream.of(SessionType.values()).map(Enum::name).collect(Collectors.toList());
            response.sendError(HttpStatus.BAD_REQUEST.value(), "valid types are: " + String.join(", ", validTypes));
        } else {
            response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (securityEnabled) {
            http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/info").permitAll()
                .anyRequest().authenticated()
                .and().httpBasic();
        } else {
            http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/**")
                .permitAll();

        }
    }
}
