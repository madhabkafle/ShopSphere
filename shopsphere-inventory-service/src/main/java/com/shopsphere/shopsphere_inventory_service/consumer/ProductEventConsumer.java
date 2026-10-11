package com.shopsphere.shopsphere_inventory_service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProductEventConsumer {

    @KafkaListener(topics = "product-events")
    public void consumeProductEvent(String message) {
        System.out.println("Received product event: " + message);
    }
}