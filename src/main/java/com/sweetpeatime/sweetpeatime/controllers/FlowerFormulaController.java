package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.FlowerFormula;
import com.sweetpeatime.sweetpeatime.entities.PriceOfSalesOrder;
import com.sweetpeatime.sweetpeatime.repositories.FlowerFormulaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Controller
@RequestMapping(value="/flowerFormula")
@CrossOrigin(origins = "http://localhost:4200")
public class FlowerFormulaController {

    @Autowired
    FlowerFormulaRepository flowerFormulaRepository;

    @GetMapping(value="/getAll")
    public List<FlowerFormula> getAllFlowerFormular() {
        return this.flowerFormulaRepository.findAll();
    }

    @GetMapping(value="/priceOfSalesOrder")
    public PriceOfSalesOrder getQuantityAvailable(
            @RequestParam("formulaId") Integer formulaId,
            @RequestParam("floristId") Integer floristId,
            @RequestParam("totalOrder") Integer totalOrder) {
        PriceOfSalesOrder priceOfSalesOrder = new PriceOfSalesOrder();
        if(totalOrder == 0){
            priceOfSalesOrder.setFlowerPrice(0.0);
            priceOfSalesOrder.setFeePrice(0.0);
            priceOfSalesOrder.setTotalPrice(0.0);
        }else{
            int flowerPrice = this.flowerFormulaRepository.getFlowerPrice(formulaId);
            double shippingFee = 0;

            if(floristId == 1){
                shippingFee = 100;
            }else{
                shippingFee = 200;
            }

            double totalPrice = (flowerPrice * totalOrder) + shippingFee;

            priceOfSalesOrder.setFlowerPrice((double) flowerPrice);
            priceOfSalesOrder.setFeePrice(shippingFee);
            priceOfSalesOrder.setTotalPrice(totalPrice);
        }

        return priceOfSalesOrder;
    }
}
