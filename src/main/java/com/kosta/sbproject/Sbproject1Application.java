package com.kosta.sbproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"controller","com.kosta"})
public class Sbproject1Application {

	public static void main(String[] args) {
		SpringApplication.run(Sbproject1Application.class, args);
	}

}
