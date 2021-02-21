package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value="/stock")
public class StockController {

    @Autowired
    private StockRepository stockRepository;

//    @GetMapping(value="/getStockByFlower/{flowerId}")
//    public Stock searchStockByFlower(@PathVariable("flowerId") Integer flowerId){
//        return this.stockRepository.searchFlowerQuantity(flowerId);
//    }
}
