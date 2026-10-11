package com.shopsphere.shopsphere_service_registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ShopsphereServiceRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopsphereServiceRegistryApplication.class, args);
	}

}
