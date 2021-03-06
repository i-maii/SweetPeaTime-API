package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.FlowerFormulaDetail;
import com.sweetpeatime.sweetpeatime.entities.FlowerQuantityAvailableDto;
import com.sweetpeatime.sweetpeatime.entities.PromotionDetail;
import com.sweetpeatime.sweetpeatime.entities.Stock;
import com.sweetpeatime.sweetpeatime.repositories.FlowerFormulaDetailRepository;
import com.sweetpeatime.sweetpeatime.repositories.PromotionDetailRepository;
import com.sweetpeatime.sweetpeatime.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Controller
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value="/flowerFormulaDetail")
public class FlowerFormulaDetailController {

    @Autowired
    private FlowerFormulaDetailRepository flowerFormulaDetailRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PromotionDetailRepository promotionDetailRepository;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping(value="/getAll")
    public List<FlowerFormulaDetail> getAll() {
        return this.flowerFormulaDetailRepository.findAll();
    }

    @GetMapping(value="/getFormulaDetail")
    public Integer getFlowerFormularDetail(@RequestParam("formulaId") Integer formulaId, @RequestParam("floristId") Integer floristId, @RequestParam("orderDate") String orderDate) throws ParseException {
        List<FlowerFormulaDetail> flowerFormulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(formulaId);
        List<Stock> stocks = new ArrayList<>();
        List<FlowerQuantityAvailableDto> flowerQuantityAvailableDtos = new ArrayList<>();
        List<PromotionDetail> promotionDetails = this.promotionDetailRepository.findAllByFlowerFormulaIdAndFloristId(formulaId, floristId);

        Date date = this.simpleDateFormat.parse(orderDate);
        int available = 0;

        for( PromotionDetail promotionDetail : promotionDetails) {
            if((date.before(promotionDetail.getExpiryDate())) && (promotionDetail.getFlorist().getId() == floristId) && (promotionDetail.getStatus().equals("active"))) {
                available += promotionDetail.getQuantity();
            } else {
                available = 0;
            }
        }

//        for(FlowerFormulaDetail flowerFormulaDetail: flowerFormulaDetails){
//            Stock stock = this.stockRepository.findStockByFlowerIdAndFloristId(flowerFormulaDetail.getFlower().getFlowerId(), floristId);
//            if(stock != null){
//                stocks.add(stock);
//            }else{
//                break;
//            }
//        }
//        if(stocks.size() == flowerFormulaDetails.size()){
//            for(int i = 0; i < stocks.size(); i++){
//                int temp = stocks.get(i).getQuantity()/flowerFormulaDetails.get(i).getQuantity();
//                if(temp != 0){
//                    available = Math.min(available, temp);
//                }else{
//                    available = 0;
//                }
//            }
//        }else{
//            available = 0;
//        }

        return available;
    }

}
