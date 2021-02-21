package com.sweetpeatime.sweetpeatime;

import com.sweetpeatime.sweetpeatime.repositories.FlowerRepository;
import com.sweetpeatime.sweetpeatime.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@SpringBootApplication
public class SweetpeatimeApplication {

    @Autowired
    StockRepository stockRepository;
    public static void main(String[] args) {
        SpringApplication.run(SweetpeatimeApplication.class, args);
    }
}
