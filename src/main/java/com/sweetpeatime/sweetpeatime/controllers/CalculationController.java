package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.Configurations;
import com.sweetpeatime.sweetpeatime.entities.FlowerFormula;
import com.sweetpeatime.sweetpeatime.repositories.ConfigurationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@RestController
@Controller
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value="/calculation")
public class CalculationController {
    @Autowired
    ConfigurationsRepository configurationsRepository;

    @GetMapping(value="/getAllConfigurations")
    public List<Configurations> getAllConfigurations() {
        return this.configurationsRepository.findAll();
    }

    @GetMapping(value="/calculateTotalPrice")
    public int calculateTotalPrice(@RequestParam("deliveryFee") Integer deliveryFee, @RequestParam("flowerFormulaPrice[]") ArrayList<Number> flowerFormulaPrice) {
        Integer totalPrice = 0;
        int i;
        if(flowerFormulaPrice.size()> 0) {
            for (i = 0; i < flowerFormulaPrice.size(); i++)
                totalPrice = totalPrice + flowerFormulaPrice.get(i).intValue();
        }
        totalPrice = totalPrice + deliveryFee;
        return totalPrice;
    }

    @GetMapping(value="/calculateDeliveryFee")
    public int calculateDeliveryFee(@RequestParam("distance") Integer distance) {
        Integer deliveryFee = 0;
        Integer deliveryRate;
      //  Integer distance = 12;
        if (distance <= 10) {
            deliveryRate = 100;
            //deliveryRate = this.configurationsRepository.getValueByName("MESSENGER_10").getValue();
        }
        else if (distance >= 10 && distance <= 20)
        {
            deliveryRate = 150;
           // deliveryRate = this.configurationsRepository.getValueByName("MESSENGER_20").getValue();

        }
        else if (distance >= 20 && distance <= 30)
        {
            deliveryRate = 300;
           // deliveryRate = this.configurationsRepository.getValueByName("MESSENGER_30").getValue();

        }
        else
        {
            deliveryRate = 501;
        }
        return deliveryRate;
    }
}

