package shopsphere_product_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ShopsphereProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopsphereProductServiceApplication.class, args);
	}

}
