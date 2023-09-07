package sk.cyrilgavala.reservationsApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.TimeZone;

@SpringBootApplication
@EnableWebSecurity
@EnableMongoRepositories(basePackages = "sk.cyrilgavala.reservationsApi.repository")
@EnableTransactionManagement
@ComponentScan({"sk.cyrilgavala.reservationsApi.config",
	"sk.cyrilgavala.reservationsApi.mapper",
	"sk.cyrilgavala.reservationsApi.security",
	"sk.cyrilgavala.reservationsApi.service",
	"sk.cyrilgavala.reservationsApi.web"})
public class ReservationsApi {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(ReservationsApi.class, args);
	}

}
