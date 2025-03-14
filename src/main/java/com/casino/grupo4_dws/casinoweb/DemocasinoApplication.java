package com.casino.grupo4_dws.casinoweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EntityScan(basePackages = "com.casino.grupo4_dws.casinoweb.model")
public class DemocasinoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemocasinoApplication.class, args);
    }

}
