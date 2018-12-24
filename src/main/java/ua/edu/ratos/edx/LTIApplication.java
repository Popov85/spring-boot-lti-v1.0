package ua.edu.ratos.edx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class LTIApplication {

	public static void main(String[] args) {
		SpringApplication.run(LTIApplication.class, args);
		System.setProperty("debug", "1");
	}
	
}
