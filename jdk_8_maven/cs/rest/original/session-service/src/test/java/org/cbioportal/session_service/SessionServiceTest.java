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

package org.cbioportal.session_service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.hamcrest.Matchers.*;
import org.junit.*;
import static org.junit.Assert.assertThat;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Manda Wilson 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
    classes = SessionService.class,
    webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "server.error.include-exception=true",
        "spring.mongodb.embedded.version=3.5.5",
        "spring.mvc.pathmatch.matching-strategy=ANT_PATH_MATCHER"
    }
)
public class SessionServiceTest {

    // get randomly assigned port
    @LocalServerPort
    private int port;

    private URL base;
    private TestRestTemplate template;

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/api/sessions/");
        template = new TestRestTemplate();
    }


    @After
    public void tearDown() throws Exception {
        // get all and delete them
        ResponseEntity<String> response = template.getForEntity(base.toString() + "msk_portal/main_session/", String.class);
        List<String> ids = parseIds(response.getBody());
        for (String id : ids) { 
			template.delete(base.toString() + "msk_portal/main_session/" + id);
		}
    }

    @Test
    public void getSessionsNoData() throws Exception {
        ResponseEntity<String> response = template.getForEntity(base.toString() + "msk_portal/main_session/", String.class);
        assertThat(response.getBody(), equalTo("[]"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void getSessionsData() throws Exception {
        // first add data
        String data = "\"portal-session\":\"my session information\"";
        ResponseEntity<String> response = addData("msk_portal", "main_session", data);

        // now test data is returned by GET /api/sessions/source/type/
        response = template.getForEntity(base.toString() + "msk_portal/main_session/", String.class);
        assertThat(expectedResponse(response.getBody(), "msk_portal", "main_session", data, true), equalTo(true)); 
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }
    
    @Test
    public void addSession() throws Exception {
        // add data
        String data = "\"portal-session\":\"my session information\"";
        ResponseEntity<String> response = addData("msk_portal", "main_session", data);

        // test that the status was 200 
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        // get id
        List<String> ids = parseIds(response.getBody());
        assertThat(ids.size(), equalTo(1));
        String id = ids.get(0);

        // get record
        response = template.getForEntity(base.toString() + "msk_portal/main_session/" + id, String.class);
        assertThat(expectedResponse(response.getBody(), "msk_portal", "main_session", data), equalTo(true)); 
    }

    @Test
    public void addSessionNoData() throws Exception {
        // add {} actually works TODO decide if it should
        String data = "";
        ResponseEntity<String> response = addData("msk_portal", "main_session", data);

        // test that we get an id back and that the status was 200 
        assertThat(response.getBody(), containsString("id"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
       
        response = addData("msk_portal", "main_session", null); 
        assertThat(response.getBody(), containsString("org.springframework.http.converter.HttpMessageNotReadableException"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void addSessionInvalidData() throws Exception {
        ResponseEntity<String> response = addData("msk_portal", "main_session", "\"portal-session\":blah blah blah"); 
        System.out.println("&&&&&&&&&&");
        System.out.println(response);
        assertThat(response.getBody(), containsString("org.cbioportal.session_service.service.exception.SessionInvalidException"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void addSessionInvalidType() throws Exception {
        ResponseEntity<String> response = addData("msk_portal", "invalid_type", "\"portal-session\":\"blah blah blah\""); 
        assertThat(response.getBody(), containsString("org.springframework.web.method.annotation.MethodArgumentTypeMismatchException"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void addSessionUniqueness() throws Exception {
        // add data
        String data = "\"portal-session\":\"my session information\"";
        ResponseEntity<String> response = addData("msk_portal", "main_session", data);

        // test that the status was 200 
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        // get id
        List<String> ids = parseIds(response.getBody());
        assertThat(ids.size(), equalTo(1));
        String id = ids.get(0);

        // get record
        response = template.getForEntity(base.toString() + "msk_portal/main_session/" + id, String.class);
        assertThat(expectedResponse(response.getBody(), "msk_portal", "main_session", data), equalTo(true)); 

        // add same data to same source and type and confirm we get same id
        response = addData("msk_portal", "main_session", data);

        // get new id
        ids = parseIds(response.getBody());
        assertThat(ids.size(), equalTo(1));
        String newId = ids.get(0);

        // make sure we got the same id
        assertThat(newId, equalTo(id));

        // now test with a different source, and make sure we get a different id 
        response = addData("other_portal", "main_session", data);

        // get new id
        ids = parseIds(response.getBody());
        assertThat(ids.size(), equalTo(1));
        String differentId = ids.get(0);

        // make sure we got the same id
        assertThat(differentId, is(not(equalTo(id))));

        // confirm this is case sensitive
        response = addData("MSK_portal", "main_session", data);

        // get new id
        ids = parseIds(response.getBody());
        assertThat(ids.size(), equalTo(1));
        differentId = ids.get(0);

        // make sure we got a new id
        assertThat(differentId, is(not(equalTo(id))));

    }

    @Test
    public void getSession() throws Exception {
        // first add data
        String data = "\"portal-session\":{\"arg1\":\"first argument\"}";
        ResponseEntity<String> response = addData("msk_portal", "main_session", data);

        // get id
        List<String> ids = parseIds(response.getBody());
        assertThat(ids.size(), equalTo(1));
        String id = ids.get(0);

        // now test data is returned by GET /api/sessions/[ID]
        response = template.getForEntity(base.toString() + "msk_portal/main_session/" + id, String.class);
        assertThat(expectedResponse(response.getBody(), "msk_portal", "main_session", data), equalTo(true)); 
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void getSessionInvalidId() throws Exception {
        ResponseEntity<String> response = template.getForEntity(base.toString() + "msk_portal/main_session/" + "id", String.class);
        assertThat(response.getBody(), containsString("org.cbioportal.session_service.service.exception.SessionNotFoundException"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    public void getSessionWithQuery() throws Exception {
        // first add data
        String data = "\"portal-session\":{\"title\":\"my portal session\"}";
        ResponseEntity<String> response = addData("msk_portal", "main_session", data);

        // now query
        response = template.getForEntity(base.toString() + "msk_portal/main_session/" + "query?field=data.portal-session.title&value=my portal session", String.class);
        assertThat(expectedResponse(response.getBody(), "msk_portal", "main_session", data, true), equalTo(true)); 
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void getSessionWithQueryNullCharacterInField() throws Exception {
        // first add data
        String data = "\"portal-session\":{\"title\":\"my portal session\"}";
        ResponseEntity<String> response = addData("msk_portal", "main_session", data);

        // now query
        response = template.getForEntity(base.toString() + "msk_portal/main_session/" + "query?field=data.p\0ortal-session.title&value=my portal session", String.class);
        assertThat(response.getBody(), containsString("org.cbioportal.session_service.service.exception.SessionQueryInvalidException"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getSessionWithQueryFieldStartsWithDollarSign() throws Exception {
        // first add data
        String data = "\"portal-session\":{\"title\":\"my portal session\"}";
        ResponseEntity<String> response = addData("msk_portal", "main_session", data);

        // now query
        response = template.getForEntity(base.toString() + "msk_portal/main_session/" + "query?field=$data.portal-session.title&value=my portal session", String.class);
        assertThat(response.getBody(), containsString("org.cbioportal.session_service.service.exception.SessionQueryInvalidException"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void fetchSessionWithQuery() throws Exception {
        // first add data
        String data = "\"portal-session\":{\"title\":\"my portal session\"}";
        ResponseEntity<String> response = addData("msk_portal", "main_session", data);
        
        HttpEntity<String> entity = prepareData("\"data.portal-session.title\":\"my portal session\"");

        // now query
        response = template.exchange(base.toString() + "msk_portal/main_session/query/fetch", HttpMethod.POST, entity, String.class);
        assertThat(expectedResponse(response.getBody(), "msk_portal", "main_session", data, true), equalTo(true)); 
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void fetchSessionWithQueryNullCharacterInField() throws Exception {
        // first add data
        String data = "\"portal-session\":{\"title\":\"my portal session\"}";
        ResponseEntity<String> response = addData("msk_portal", "main_session", data);
        
        HttpEntity<String> entity = prepareData("\"data.p\\\0ortal-session.title\":\"my portal session\"");

        // now query
        response = template.exchange(base.toString() + "msk_portal/main_session/query/fetch", HttpMethod.POST, entity, String.class);
        assertThat(response.getBody(), containsString("org.cbioportal.session_service.service.exception.SessionQueryInvalidException"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void fetchSessionWithQueryFieldStartsWithDollarSign() throws Exception {
        // first add data
        String data = "\"portal-session\":{\"title\":\"my portal session\"}";
        ResponseEntity<String> response = addData("msk_portal", "main_session", data);

        HttpEntity<String> entity = prepareData("\"$data.portal-session.title\":\"my portal session\"");

        // now query
        response = template.exchange(base.toString() + "msk_portal/main_session/query/fetch", HttpMethod.POST, entity, String.class);
        assertThat(response.getBody(), containsString("org.cbioportal.session_service.service.exception.SessionQueryInvalidException"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void updateSession() throws Exception {
        String data = "\"portal-session\":\"my session information\"";
        ResponseEntity<String> response = addData("msk_portal", "main_session", data);

        // get id
        List<String> ids = parseIds(response.getBody());
        assertThat(ids.size(), equalTo(1));
        String id = ids.get(0);

        // get record
        response = template.getForEntity(base.toString() + "msk_portal/main_session/" + id, String.class);
        assertThat(expectedResponse(response.getBody(), "msk_portal", "main_session", data), equalTo(true)); 

        // update record
        data = "\"portal-session\":\"my session UPDATED information\"";
        HttpEntity<String> entity = prepareData(data);
        response = template.exchange(base.toString() + "msk_portal/main_session/" + id, HttpMethod.PUT, entity, String.class);
        assertThat(response.getBody(), equalTo(null)); 

        // get updated record
        response = template.getForEntity(base.toString() + "msk_portal/main_session/" + id, String.class);
        assertThat(expectedResponse(response.getBody(), "msk_portal", "main_session", data), equalTo(true)); 
        assertThat(response.getBody(), containsString("UPDATED"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void updateSessionInvalidData() throws Exception {
        String data = "\"portal-session\":{\"arg1\":\"first argument\"}";
        ResponseEntity<String> response = addData("msk_portal", "main_session", data);

        // get id
        List<String> ids = parseIds(response.getBody());
        assertThat(ids.size(), equalTo(1));
        String id = ids.get(0);

        HttpEntity<String> entity = prepareData("\"portal-session\":blah blah blah");
        response = template.exchange(base.toString() + "msk_portal/main_session/" + id, HttpMethod.PUT, entity, String.class);
        assertThat(response.getBody(), containsString("org.cbioportal.session_service.service.exception.SessionInvalidException"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void updateSessionInvalidId() throws Exception {
        HttpEntity<String> entity = prepareData("\"portal-session\":\"my session information\"");
        ResponseEntity<String> response = template.exchange(base.toString() + "msk_portal/main_session/id", HttpMethod.PUT, entity, String.class);
        assertThat(response.getBody(), containsString("org.cbioportal.session_service.service.exception.SessionNotFoundException"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    public void updateSessionNoData() throws Exception {
        String data = "\"portal-session\":\"my session information\"";
        ResponseEntity<String> response = addData("msk_portal", "main_session", data);

        // get id
        List<String> ids = parseIds(response.getBody());
        assertThat(ids.size(), equalTo(1));
        String id = ids.get(0);

        HttpEntity<String> entity = prepareData(null);
        response = template.exchange(base.toString() + "msk_portal/main_session/" + id, HttpMethod.PUT, entity, String.class);

        assertThat(response.getBody(), containsString("org.springframework.http.converter.HttpMessageNotReadableException"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void deleteSession() throws Exception {
        // first add data
        String data = "\"portal-session\":{\"arg1\":\"first argument\"}";
        ResponseEntity<String> response = addData("msk_portal", "main_session", data);

        // get id
        List<String> ids = parseIds(response.getBody());
        assertThat(ids.size(), equalTo(1));
        String id = ids.get(0);

        // get record from database
        response = template.getForEntity(base.toString() + "msk_portal/main_session/" + id, String.class);
        assertThat(expectedResponse(response.getBody(), "msk_portal", "main_session", data), equalTo(true)); 

        // delete
        response = template.exchange(base.toString() + "msk_portal/main_session/" + id, HttpMethod.DELETE, null, String.class);
        assertThat(response.getBody(), equalTo(null)); 
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        // confirm record is gone
        response = template.getForEntity(base.toString() + "msk_portal/main_session/" + id, String.class);
        assertThat(response.getBody(), containsString("org.cbioportal.session_service.service.exception.SessionNotFoundException"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    public void deleteSessionInvalidId() throws Exception {
        ResponseEntity<String> response = template.exchange(base.toString() + "msk_portal/main_session/id", HttpMethod.DELETE, null, String.class);
        assertThat(response.getBody(), containsString("org.cbioportal.session_service.service.exception.SessionNotFoundException"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    public void deleteSessionValidIdWrongSource() throws Exception {
        // first add data
        String data = "\"portal-session\":{\"arg1\":\"first argument\"}";
        ResponseEntity<String> response = addData("msk_portal", "virtual_study", data);

        // get id
        List<String> ids = parseIds(response.getBody());
        assertThat(ids.size(), equalTo(1));
        String id = ids.get(0);

        // get record from database
        response = template.getForEntity(base.toString() + "msk_portal/virtual_study/" + id, String.class);
        assertThat(expectedResponse(response.getBody(), "msk_portal", "virtual_study", data), equalTo(true)); 

        // delete with different source
        response = template.exchange(base.toString() + "msk_portal/main_session/" + id, HttpMethod.DELETE, null, String.class);
        assertThat(response.getBody(), containsString("SessionNotFoundException"));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));

        // delete with correct source
        response = template.exchange(base.toString() + "msk_portal/virtual_study/" + id, HttpMethod.DELETE, null, String.class);
        assertThat(response.getBody(), equalTo(null)); 
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    private HttpEntity<String> prepareData(String data) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (data != null) {
            data = "{" + data + "}";
        }
        return new HttpEntity<String>(data, headers);
    }

    private ResponseEntity<String> addData(String source, String type, String data) throws Exception {
        HttpEntity<String> entity = prepareData(data);
        return template.exchange(base.toString() + source + "/" + type + "/", HttpMethod.POST, entity, String.class);
    }

    /*
     * plural is false.
     */
    private boolean expectedResponse(String responseBody, 
            String source,
            String type,
            String data) throws Exception {
        return expectedResponse(responseBody, source, type, data, false);
    }

    private boolean expectedResponse(String responseBody, 
            String source,
            String type,
            String data, 
            boolean plural) throws Exception {
        // { and } are special characters in regexes, but also used in JSON so we need to escape them
        data = data.replaceAll("\\{", "\\\\{");
        data = data.replaceAll("\\}", "\\\\}");
        String pattern = "\\{\"id\":\"([^\"]+)\",\"data\":\\{" 
            + data + "\\},\"source\":\"" + source + "\",\"type\":\"" + type + "\"\\}";
        if (plural) {
            pattern = "\\[" + pattern + "\\]";
        }
        pattern = "^" + pattern + "$";
        Pattern expectedResponsePattern = Pattern.compile(pattern);
        Matcher responseMatcher = expectedResponsePattern.matcher(responseBody);
        return responseMatcher.matches();
    }

    private List<String> parseIds(String json) throws Exception {
        Pattern idPattern = Pattern.compile("\"id\":\"([^\"]+)\"");
        Matcher idMatcher = idPattern.matcher(json);
        List<String> ids = new ArrayList<String>();
        while (idMatcher.find()) {
            ids.add(idMatcher.group(1));
        }
        return ids;
    }
};
