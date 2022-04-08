package br.com.codenation.hospital;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.codenation.hospital.constant.Constant;
import br.com.codenation.hospital.domain.Hospital;
import br.com.codenation.hospital.domain.Product;
import br.com.codenation.hospital.services.HospitalService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class ProductResourceTest {
	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private HospitalService hospitalService;

	private final HttpHeaders httpHeaders;
	private Hospital hospitalTest;
	private Product productTest;

	public ProductResourceTest() {
		httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
	}

	@Before
	public void setUp() {
		hospitalTest = hospitalService.findById("1");
		if (hospitalTest != null) {
			List<Product> productList = hospitalTest.getProducts();
			if (productList.size() > 0) {
				hospitalTest.setProducts(productList);
				productTest = productList.get(0);
			}
		}
	}

	@Test
	public void deveListarTodosProdutosDoHospital() {
		ResponseEntity<List<Product>> response = restTemplate.exchange(
				Constant.V1 + hospitalTest.getId() + "/estoque", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Product>>() {
				});
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void deveListarProdutoDoHospital() {
		ResponseEntity<Product> response = restTemplate.exchange(
				Constant.V1 + hospitalTest.getId() + "/estoque/" + productTest.getId(), HttpMethod.GET, null,
				new ParameterizedTypeReference<Product>() {
				});
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void naoDeveListarProdutoDoHospital() {
		ResponseEntity<Product> response = restTemplate.exchange(Constant.V1 + hospitalTest.getId() + "/estoque/0",
				HttpMethod.GET, null, new ParameterizedTypeReference<Product>() {
				});
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void deveAddProdutoNoHospital() {
		ResponseEntity<Void> response = restTemplate.exchange(Constant.V1 + hospitalTest.getId() + "/estoque",
				HttpMethod.POST,
				new HttpEntity<>("{\n" + "\t\"name\": \"produto teste\",\n" + "\t\"description\": \"\", \n"
						+ "\t\"quantity\": 10,\n" + "\t\"productType\": \"COMMON\"\n" + "}", httpHeaders),
				new ParameterizedTypeReference<Void>() {
				});
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void deveAtualizarProdutoNoHospital() {
		ResponseEntity<Void> response = restTemplate.exchange(
				Constant.V1 + hospitalTest.getId() + "/estoque/" + productTest.getId(), HttpMethod.PUT,
				new HttpEntity<>("{\n" + "\t\"name\": \"produto update\",\n" + "\t\"quantity\": 50,\n"
						+ "\t\"productType\": \"COMMON\"\n" + "}", httpHeaders),
				new ParameterizedTypeReference<Void>() {
				});
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void deveDeletarProdutoNoHospital() {
		ResponseEntity<Void> response = restTemplate.exchange(
				Constant.V1 + hospitalTest.getId() + "/estoque/" + productTest.getId(), HttpMethod.DELETE, null,
				new ParameterizedTypeReference<Void>() {
				});
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}