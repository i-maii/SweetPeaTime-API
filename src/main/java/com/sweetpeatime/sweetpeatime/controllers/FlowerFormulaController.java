package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.FlowerFormula;
import com.sweetpeatime.sweetpeatime.entities.FlowerQuantityAvaliableDto;
import com.sweetpeatime.sweetpeatime.repositories.FlowerFormulaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/flowerFormula")
@CrossOrigin(origins = "http://localhost:4200")
public class FlowerFormulaController {

    @Autowired
    FlowerFormulaRepository flowerFormulaRepository;

    @GetMapping(value="/getAll")
    public List<FlowerFormula> getAllFlowerFormular() {
        return this.flowerFormulaRepository.findAll();
    }

    @GetMapping(value="/getQuantityAvailable/{id}")
    public List<FlowerQuantityAvaliableDto> getQuantityAvailable(@PathVariable("id") Integer id) {
        List<FlowerQuantityAvaliableDto> flowerQuantityAvaliableDtos = new ArrayList<>();
        for(int i = 1; i <= this.flowerFormulaRepository.getQuantityAvailable(id); i++){
            FlowerQuantityAvaliableDto flowerQuantityAvaliableDto = new FlowerQuantityAvaliableDto();
            flowerQuantityAvaliableDto.setId(i);
            flowerQuantityAvaliableDto.setFlowerQuantityAvailiable(i);
            flowerQuantityAvaliableDtos.add(flowerQuantityAvaliableDto);
        }
        return flowerQuantityAvaliableDtos;
    }
}
