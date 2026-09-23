package com.nova.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(scanBasePackages = "com.nova.mall")
public class NovaMallApplication {
    public static void main(String[] args) {
        SpringApplication.run(NovaMallApplication.class, args);
    }
}
