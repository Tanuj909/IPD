package com.ipd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {
        "com.ipd",
        "com.user"
})
public class IpdManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(IpdManagementApplication.class, args);
	}
}
