package com.mknieszner.checklist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.mknieszner.checklist", "com.mknieszner.productconnector"})
@EnableScheduling
@EntityScan({"com.mknieszner.checklist", "com.mknieszner.productconnector"})
public class ChecklistApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChecklistApplication.class, args);
    }
}
