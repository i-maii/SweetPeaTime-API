package com.sweetpeatime.sweetpeatime;

import com.sweetpeatime.sweetpeatime.repositories.FlowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SweetpeatimeApplication {

    @Autowired
    private FlowerRepository flowerRepository;

    public static void main(String[] args) {
        SpringApplication.run(SweetpeatimeApplication.class, args);
    }

}
