package com.mknieszner.productconnector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProductConnectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductConnectorApplication.class, args);
    }

}
