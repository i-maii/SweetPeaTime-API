package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.FlowerFormulaDetail;
import com.sweetpeatime.sweetpeatime.entities.FlowerQuantityAvailableDto;
import com.sweetpeatime.sweetpeatime.entities.Stock;
import com.sweetpeatime.sweetpeatime.repositories.FlowerFormulaDetailRepository;
import com.sweetpeatime.sweetpeatime.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Controller
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value="/flowerFormulaDetail")
public class FlowerFormulaDetailController {

    @Autowired
    FlowerFormulaDetailRepository flowerFormulaDetailRepository;

    @Autowired
    StockRepository stockRepository;

    @GetMapping(value="/getAll")
    public List<FlowerFormulaDetail> getAll() {
        return this.flowerFormulaDetailRepository.findAll();
    }

    @GetMapping(value="/getFormulaDetail")
    public List<FlowerQuantityAvailableDto> getFlowerFormularDetail(@RequestParam("formulaId") Integer formulaId, @RequestParam("floristId") Integer floristId) {
        List<FlowerFormulaDetail> flowerFormulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(formulaId);
        List<Stock> stocks = new ArrayList<>();
        List<FlowerQuantityAvailableDto> flowerQuantityAvailableDtos = new ArrayList<>();
        int avaliable = 9999;

        for(FlowerFormulaDetail flowerFormulaDetail: flowerFormulaDetails){
            Stock stock = this.stockRepository.findStockByFlowerIdAndFloristId(flowerFormulaDetail.getFlower().getFlowerId(), floristId);
            if(stock != null){
                stocks.add(stock);
            }else{
                break;
            }
        }

        if(stocks.size() == flowerFormulaDetails.size()){
            for(int i = 0; i < stocks.size(); i++){
                int temp = stocks.get(i).getQuantity()/flowerFormulaDetails.get(i).getQuantity();
                if(temp != 0){
                    avaliable = Math.min(avaliable, temp);
                }else{
                    avaliable = 0;
                }
            }
        }else{
            avaliable = 0;
        }

        if(avaliable == 0){
            FlowerQuantityAvailableDto flowerQuantityAvailableDto = new FlowerQuantityAvailableDto();
            flowerQuantityAvailableDto.setId(0);
            flowerQuantityAvailableDto.setFlowerQuantityAvailiable(0);
            flowerQuantityAvailableDtos.add(flowerQuantityAvailableDto);
        }else{
            for(int i = 1; i <= avaliable; i++){
                FlowerQuantityAvailableDto flowerQuantityAvailableDto = new FlowerQuantityAvailableDto();
                flowerQuantityAvailableDto.setId(i);
                flowerQuantityAvailableDto.setFlowerQuantityAvailiable(i);
                flowerQuantityAvailableDtos.add(flowerQuantityAvailableDto);
            }
        }

        return flowerQuantityAvailableDtos;
    }

}
