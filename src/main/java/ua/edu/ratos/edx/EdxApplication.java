package ua.edu.ratos.edx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/*@EnableAutoConfiguration(
		exclude = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
)*/
public class EdxApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdxApplication.class, args);
	}
	
}
