package com.dairyfarm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DairyFarmErpApplication {
    public static void main(String[] args) {
        SpringApplication.run(DairyFarmErpApplication.class, args);
    }
}

