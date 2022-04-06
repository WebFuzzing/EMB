package br.com.codenation.hospital;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.codenation.hospital.domain.Hospital;
import br.com.codenation.hospital.domain.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.codenation.hospital.constant.Constant;
import br.com.codenation.hospital.dto.HospitalDTO;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class HospitalResourceTest {
	@Autowired
	private TestRestTemplate restTemplate;

	private final HttpHeaders httpHeaders;

	private ResponseEntity<HospitalDTO> response;

	@Mock
	private Hospital hospitalMock;

	@Mock
	private Product productMock;

	public HospitalResourceTest() {
		httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
	}

	@Before
	public void setUp() {
		String hospitalJson = "{\"name\": \"Hospital Um\", \"address\": \"Rua dos Sonhos, 1213\", \"beds\": \"21\", \"availableBeds\": \"5\"}";
		response = restTemplate.exchange(Constant.V1, HttpMethod.POST, new HttpEntity<>(hospitalJson, httpHeaders),
				HospitalDTO.class);
	}

	@Test
	public void deveSalvarHospital() {
		String hospitalJson = "{\"name\": \"Hospital Novo\", \"address\": \"Rua dos Novos, 0001\", \"beds\": \"10\", \"availableBeds\": \"9\"}";
		ResponseEntity<HospitalDTO> salvarResponse = restTemplate.exchange(Constant.V1, HttpMethod.POST,
				new HttpEntity<>(hospitalJson, httpHeaders), HospitalDTO.class);

		assertThat(salvarResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void deveAtualizarHospital() {
		String hospitalJson = "{\"name\": \"Hospital Novo\", \"address\": \"Rua dos Novos, 1000\", \"beds\": \"10\", \"availableBeds\": \"10\"}";
		Map<String, String> param = new HashMap<>();
		ResponseEntity<Void> atualizarResponse = restTemplate.exchange(Constant.V1 + response.getBody().getId(),
				HttpMethod.PUT, new HttpEntity<>(hospitalJson, httpHeaders), Void.class);

		assertThat(atualizarResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void deveRemoverHospital() {
		ResponseEntity<Void> removerResponse = restTemplate.exchange(Constant.V1 + response.getBody().getId(),
				HttpMethod.DELETE, null, Void.class);
		assertThat(removerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void deveListarHospitalPeloId() {
		ResponseEntity<HospitalDTO> getResponse = restTemplate.exchange(Constant.V1 + response.getBody().getId(),
				HttpMethod.GET, null, HospitalDTO.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void deveListarTodosHospitais() {
		ResponseEntity<List<HospitalDTO>> response = restTemplate.exchange(Constant.V1, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<HospitalDTO>>() {
				});
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void naoDeveListarHospital() {
		ResponseEntity<HospitalDTO> getResponse = restTemplate.exchange(Constant.V1 + "0", HttpMethod.GET, null,
				HospitalDTO.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void deveRetornarLeitosDisponiveis() {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> getResponse = restTemplate.exchange(Constant.V1 + "1/leitos", HttpMethod.GET, null,
				Map.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResponse.getBody().get("leitos")).isEqualTo(5);
	}
	
	@Test
	public void naoDeveRetornarLeitosDisponiveis() {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> getResponse = restTemplate.exchange(Constant.V1 + "0/leitos", HttpMethod.GET, null,
				Map.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void naoDeveFazerTransferenciaDoHospitalMaisProximo() {
		ResponseEntity<HospitalDTO> getResponse = restTemplate
				.exchange(Constant.V1 + hospitalMock.getId() + "/transferencia/" + productMock.getId(),
						HttpMethod.POST,
						new HttpEntity<>("{\"quantity\": 5}", httpHeaders),
						HospitalDTO.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

//	@Test
//	public void deveFazerTransferenciaDoHospitalMaisProximo() {
//		ResponseEntity<HospitalDTO> getResponse = restTemplate
//				.exchange(Constant.V1 + "1/transferencia/5cac04a481b2d504d0ed2a5a",
//						HttpMethod.POST,
//						new HttpEntity<>("{\"quantity\": 5}", httpHeaders),
//						HospitalDTO.class);
//		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//	}

	@Test
	public void deveRetornarHospitalMaisProximoComLeitosDisponiveis() {
		ResponseEntity<HospitalDTO> getResponse = restTemplate
				.exchange(Constant.V1 + "/maisProximo?lat=50&lon=50&raioMaximo=50000",
						HttpMethod.GET,
						null,
						HospitalDTO.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}