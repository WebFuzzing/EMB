package br.com.codenation.hospital;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.codenation.hospital.domain.Location;
import br.com.codenation.hospital.integration.LocationIQResponse;
import br.com.codenation.hospital.integration.LocationIQService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class LocationIQServiceTest {
	
	@Autowired
	private LocationIQService locationIQService;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	private final HttpHeaders httpHeaders;
	private String search;

	public LocationIQServiceTest() {
		httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
	}
	
	@Before
	public void setUp() {
		search = "Hospital Israelita Albert Einstein";
	}
	
	@Test 
	public void deveConverterJsonParaLocationIQResponse() throws IOException {
		LocationIQResponse locationIQResponse = new LocationIQResponse();
		String json = "{\"place_id\": \"35288236\", \"licence\": \"https://locationiq.com/attribution\", \"osm_type\": \"node\", \"osm_id\": \"2837177940\",\"boundingbox\": [\"41.3438445\", \"41.3439445\", \"-86.3112295\", \"-86.3111295\"], \"lat\": \"41.3438945\", \"lon\": \"-86.3111795\", \"display_name\": \"Statue of Liberty, North Center Street, Plymouth, Marshall County, Indiana, 46563, Estados Unidos da América\", \"class\": \"tourism\", \"type\": \"artwork\", \"importance\": 0.66954659981371, \"icon\": \"https://locationiq.org/static/images/mapicons/tourist_art_gallery2.p.20.png\"}";
		
		 locationIQResponse = new ObjectMapper()
				  .readerFor(LocationIQResponse.class)
				  .readValue(json);
		
		 assertEquals("35288236", locationIQResponse.getPlaceId());
	}
	
	@Test 
	public void deveRetornarLocationIQResponse() throws IOException {
		
		List<LocationIQResponse> locationsResponse = locationIQService.getLocationIQResponse(search);
		
		if (!locationsResponse.isEmpty()) {
			assertEquals("125076245", locationsResponse.get(0).getPlaceId());
		}
	}
}
