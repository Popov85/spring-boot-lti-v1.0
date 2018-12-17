package ua.edu.ratos.edx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/*@EnableAutoConfiguration(
		exclude = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
)*/
public class LTIApplication {

	public static void main(String[] args) {
		SpringApplication.run(LTIApplication.class, args);
	}
	
}
