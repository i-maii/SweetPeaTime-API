package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.*;
import com.sweetpeatime.sweetpeatime.repositories.ConfigurationsPromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value="/configurationsPromotion")
public class ConfigurationsPromotionController {

    @Autowired
    ConfigurationsPromotionRepository configurationsPromotionRepository;

    @GetMapping(value = "/getProfit")
    public List<ConfigurationsPromotion> getProfit(@RequestParam("lifeTime") Integer lifeTime,@RequestParam("flowerType") String flowerType) {
        return this.configurationsPromotionRepository.findAllByLifeTimeAndFlowerType(lifeTime, flowerType);
    }

}
