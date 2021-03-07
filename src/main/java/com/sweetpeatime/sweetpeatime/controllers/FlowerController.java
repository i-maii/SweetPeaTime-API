package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.Flower;
import com.sweetpeatime.sweetpeatime.repositories.FlowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value="/flower")
public class FlowerController {

    @Autowired
    private FlowerRepository flowerRepository;

    @GetMapping(value="/getAll")
    public List<Flower> getAllFlowers() {
        return this.flowerRepository.findAll();
    }

    @GetMapping(value="/getLifeTimeFlower")
    public List<Flower> getLifeTimeFlower(@RequestParam("flowerId") Integer flowerId,@RequestParam("flowerType") String flowerType) throws ParseException {
        return this.flowerRepository.findAllByIdAndFlowerType(flowerId, flowerType);
    }

}
