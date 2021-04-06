package com.sweetpeatime.sweetpeatime.scheduleTask;

import com.sweetpeatime.sweetpeatime.entities.EventPromotion;
import com.sweetpeatime.sweetpeatime.entities.FlowerFormula;
import com.sweetpeatime.sweetpeatime.entities.FlowerFormulaDetail;
import com.sweetpeatime.sweetpeatime.entities.FlowerPrice;
import com.sweetpeatime.sweetpeatime.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class FlowerPriceSchedule {
    private static final Logger log = LoggerFactory.getLogger(FlowerPriceSchedule.class);

    @Autowired
    FlowerFormulaRepository flowerFormulaRepository;

    @Autowired
    FlowerRepository flowerRepository;

    @Autowired
    FlowerFormulaDetailRepository flowerFormulaDetailRepository;

    @Autowired
    FlowerPriceRepository flowerPriceRepository;

    @Autowired
    ConfigurationRepository configurationRepository;

    @Autowired
    PromotionDetailRepository promotionDetailRepository;

    @Autowired
    EventPromotionRepository eventPromotionRepository;


    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    @Scheduled(cron = "0 0 0,5 ? * *")
//    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        Date date = new Date();
        Integer percentProfit = 0;
        List<EventPromotion> eventPromotions = this.eventPromotionRepository.findAll();

        for (EventPromotion eventPromotion : eventPromotions) {
            if ((date.before(eventPromotion.getEndDate()) && date.after(eventPromotion.getStartDate())) || date.equals(eventPromotion.getStartDate()) || date.equals(eventPromotion.getEndDate())){
                percentProfit = eventPromotion.getPercentOfProfit();
            } else {
                percentProfit = this.configurationRepository.findConfigurationsById(3).getValue();
            }
        }

        List<FlowerFormula> flowerFormulas = this.flowerFormulaRepository.findAll();
        Integer flowerFormulaPrice = 0;

        for (FlowerFormula flowerFormula: flowerFormulas) {
            List<FlowerFormulaDetail> flowerFormulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(flowerFormula.getId());
            for (FlowerFormulaDetail flowerFormulaDetail: flowerFormulaDetails) {
                FlowerPrice flowerPrice = this.flowerPriceRepository.findByFlowerId(flowerFormulaDetail.getFlower().getFlowerId());
                int unitQuantityUse = 1;
                int i = flowerFormulaDetail.getQuantity();
                while (i > flowerPrice.getQuantitySaleUnit()) {
                    unitQuantityUse++;
                    i = i - flowerPrice.getQuantitySaleUnit();
                }
                flowerFormulaPrice += flowerPrice.getPrice() * unitQuantityUse;
            }
            flowerFormulaPrice += (flowerFormulaPrice*percentProfit) / 100;

//            if (flowerFormulaPrice % 100 != 0) {
//                flowerFormulaPrice = (flowerFormulaPrice - (flowerFormulaPrice % 100)) + 90;
//            }
            flowerFormula.setPrice(flowerFormulaPrice);
            this.flowerFormulaRepository.saveAndFlush(flowerFormula);
            flowerFormulaPrice = 0;
        }
    }
}
