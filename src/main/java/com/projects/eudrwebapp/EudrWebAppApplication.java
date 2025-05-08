package com.projects.eudrwebapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EudrWebAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(EudrWebAppApplication.class, args);
    }

}
