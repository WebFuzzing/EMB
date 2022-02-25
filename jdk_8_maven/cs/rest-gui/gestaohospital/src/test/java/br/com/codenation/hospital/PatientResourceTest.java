package br.com.codenation.hospital;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

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
import br.com.codenation.hospital.domain.Hospital;
import br.com.codenation.hospital.domain.Patient;
import br.com.codenation.hospital.services.HospitalService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class PatientResourceTest {
	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private HospitalService hospitalService;

	private final HttpHeaders httpHeaders;
	private Hospital hospitalTest;
	private Patient patientTest;

	@Mock
	private Hospital hospitalMock;

	@Mock
	private Patient pacienteMock;

	public PatientResourceTest() {
		httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

	}

	@Before
	public void setUp() {
		hospitalTest = hospitalService.findById("1");
		if (hospitalTest != null) {
			List<Patient> patientList = hospitalTest.getPatients();
			if (!patientList.isEmpty()) {
				patientTest = patientList.get(0);
			}
		}
	}

	@Test
	public void deveListarTodosPacienteDoHospital() {
		ResponseEntity<List<Patient>> response = restTemplate.exchange(
				Constant.V1 + hospitalTest.getId() + "/pacientes", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Patient>>() {
				});
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void deveListarPacienteDoHospital() {
		ResponseEntity<Patient> response = restTemplate.exchange(
				Constant.V1 + hospitalTest.getId() + "/pacientes/" + patientTest.getId(), HttpMethod.GET, null,
				new ParameterizedTypeReference<Patient>() {
				});
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getId()).isEqualTo(patientTest.getId());
	}

	@Test // not working, only god knows why
	public void naoDeveListarPacienteDoHospital() {
		ResponseEntity<Patient> response = restTemplate.exchange(
				Constant.V1 + hospitalTest.getId() + "/pacientes/0", HttpMethod.GET, null,
				new ParameterizedTypeReference<Patient>() {
				});
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void deveFazerCheckinNoHospital() {
		Patient paciente = new Patient("nome", "cpf", new Date(), "sexo");
		//int nPacientes = hospitalTest.getPatients().size();
		ResponseEntity<Patient> response = restTemplate.postForEntity(
				Constant.V1 + hospitalTest.getId() + "/pacientes/checkin", paciente, Patient.class,
				hospitalTest.getId());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void deveFazerCheckoutNoHospital() {

		ResponseEntity<Patient> response = restTemplate.postForEntity(
				Constant.V1 + hospitalTest.getId() + "/pacientes/checkout", patientTest.getId(), Patient.class,
				hospitalTest.getId());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test // not working, only god knows why
	public void deveAtualizarPacienteNoHospital() {
		ResponseEntity<Void> response = restTemplate.exchange(
				Constant.V1 + hospitalTest.getId() + "/pacientes/" + patientTest.getId(), HttpMethod.PUT,
				new HttpEntity<>("{\n" + "\t\"name\": \"paciente update\",\n" + "\t\"cpf\": \"cpf update\",\n" + "}",
						httpHeaders),
				Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}