package br.com.codenation.hospital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
// url Swagger - http://localhost:8080/swagger-ui.html
public class SwaggerConfig {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("br.com.codenation.hospital.resource"))
				.paths(PathSelectors.any()).build()
				.apiInfo(apiInfo());
						
	}
	
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Sistema de Gestão Hospital API")
				.description("Documentação da API de acesso aos endpoints da GestaoHospitalAPI - Aceleradev Brasil\r\n" + 
						"Jornada de desafios da Aceleradev Brasil - CodeNation")
				.version("1.0")
				.build();
	}
}
