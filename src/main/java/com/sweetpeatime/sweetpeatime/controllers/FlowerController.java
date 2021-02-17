package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.Flower;
import com.sweetpeatime.sweetpeatime.repositories.FlowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="/flower")
public class FlowerController {

    @Autowired
    private FlowerRepository flowerRepository;

    @GetMapping(value="/getAll")
    public List<Flower> getAllFlowers() {
        return this.flowerRepository.findAll();
    }

}
