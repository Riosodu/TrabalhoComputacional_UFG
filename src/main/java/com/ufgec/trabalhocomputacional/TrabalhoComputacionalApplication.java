package com.ufgec.trabalhocomputacional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;

@SpringBootApplication
public class TrabalhoComputacionalApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrabalhoComputacionalApplication.class, args);
	}

}
