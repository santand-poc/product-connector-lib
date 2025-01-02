package com.mknieszner.limitprocess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.mknieszner.limitprocess", "com.mknieszner.productconnector"})
@EnableScheduling
@EntityScan({"com.mknieszner.limitprocess", "com.mknieszner.productconnector"})
public class LimitProcessApplication {
    public static void main(String[] args) {
        SpringApplication.run(LimitProcessApplication.class, args);
    }
}
