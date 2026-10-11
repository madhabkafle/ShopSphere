package com.shopsphere.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
public class ProductServiceClient {

    private final RestClient restClient;

    public ProductServiceClient(
            @Qualifier("loadBalancedRestClientBuilder")
            RestClient.Builder restClientBuilder) {

        this.restClient = restClientBuilder.build();
    }

    public String getProductById(Long id) {
        try {
            return restClient.get()
                    .uri(
                            "http://shopsphere-product-service/api/products/{id}",
                            id
                    )
                    .retrieve()
                    .body(String.class);

        } catch (HttpClientErrorException.NotFound ex) {
            return "Product not found with id: " + id;
        }
    }
}