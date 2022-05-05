package com.adidas.tsar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class PlanogramApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlanogramApplication.class, args);
	}

}
