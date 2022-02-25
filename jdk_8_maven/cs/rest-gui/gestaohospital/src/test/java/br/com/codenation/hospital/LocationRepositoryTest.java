package br.com.codenation.hospital;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.codenation.hospital.domain.Hospital;
import br.com.codenation.hospital.domain.Location;
import br.com.codenation.hospital.domain.Product;
import br.com.codenation.hospital.repository.LocationRepository;
import br.com.codenation.hospital.services.HospitalService;
import br.com.codenation.hospital.services.LocationService;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class LocationRepositoryTest {
	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private LocationRepository repo;

	@Autowired 
	MongoTemplate template;
	
	private final HttpHeaders httpHeaders;
	private Location locationTest;

	public LocationRepositoryTest() {
		httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
	}
	
	@Before
	public void setUp() {
		// ensure geospatial index
	    template.indexOps(Location.class).ensureIndex( new GeospatialIndex("position") );
	    // prepare data
	    repo.save( new Location("A", 0.001, -0.002) );
	    repo.save( new Location("B", 1, 1) );
	    repo.save( new Location("C", 0.5, 0.5) );
	    repo.save( new Location("D", -0.5, -0.5) );
	}
	
	@Test public void shouldFindAroundOrigin() {
	    // when
	    //List<Location> locations = repo.findByPositionWithin( new Circle(0,0, 0.75) );
	 
	    // then
	  // assertLocations( locations, "A", "C", "D" );
	}
	 
	  @Test public void shouldFindWithinBox() {
	    // when
	    //List<Location> locations = repo.findByPositionWithin( new Box( new Point(0.25, 0.25), new Point(1,1)) );
	 
	    // then
	    //assertLocations( locations, "B", "C" );
	  }
	  
	  
	  private static void assertLocations(List<Location> locations, String... ids) {
			//assertThat( locations, notNullValue() );
			out("-----------------------------");
			for (Location l : locations) {
				out(l);
			}
			/*assertThat("Mismatch location count", ids.length, is(locations.size()));
			for (String id : ids) {
				assertThat("Location " + id + " not found",
						locations.contains(new Location(id, 0, 0)), is(true));
			}*/
		}

		private static void out(Object o) {
			System.out.println(o);
		}
}
