package com.sweetpeatime.sweetpeatime;

import com.sweetpeatime.sweetpeatime.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SweetpeatimeApplication {

    @Autowired
    StockRepository stockRepository;
    public static void main(String[] args) {
        SpringApplication.run(SweetpeatimeApplication.class, args);
    }
}
