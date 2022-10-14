package com.adidas.tsar;

import com.adidas.logging.annotation.EnableLoggingFeatures;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableFeignClients
@EnableAsync
@EnableLoggingFeatures(features = {EnableLoggingFeatures.Feature.MDC, EnableLoggingFeatures.Feature.RESPONSE_TRACKING})
public class PlanogramApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlanogramApplication.class, args);
	}

}
