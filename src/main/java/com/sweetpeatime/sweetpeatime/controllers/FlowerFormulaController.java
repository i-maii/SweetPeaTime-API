package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.FlowerFormula;
import com.sweetpeatime.sweetpeatime.repositories.FlowerFormulaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="/flowerFormula")
public class FlowerFormulaController {

    @Autowired
    FlowerFormulaRepository flowerFormulaRepository;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(value="/getAll")
    public List<FlowerFormula> getAllFlowerFormular() {
        return this.flowerFormulaRepository.findAll();
    }
}
