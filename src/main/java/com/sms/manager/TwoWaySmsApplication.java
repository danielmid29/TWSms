package com.sms.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.plivo.api.Plivo;

@SpringBootApplication
@EnableScheduling
public class TwoWaySmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwoWaySmsApplication.class, args);
	}

}
