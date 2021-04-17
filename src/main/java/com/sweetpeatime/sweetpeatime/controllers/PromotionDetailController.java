package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.*;
import com.sweetpeatime.sweetpeatime.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value="/promotionDetail")
public class PromotionDetailController {

    @Autowired
    PromotionDetailRepository promotionDetailRepository;

    @Autowired
    FlowerFormulaDetailRepository flowerFormulaDetailRepository;

    @Autowired
    StockRepository stockRepository;

    @Autowired
    FlowerRepository flowerRepository;

    @Autowired
    PromotionProfitRepository promotionProfitRepository;

    //@GetMapping(value = "/currentPromotion")
    @GetMapping(value = "/currentPromotion")
    public List<PromotionDetail> getCurrentPromotion() {
        return this.promotionDetailRepository.findPromotionDetailsByStatus("active");
    }

    @GetMapping(value = "/getAllPromotion")
    public List<PromotionDetail> getAllPromotion() {


        return this.promotionDetailRepository.findAll();
    }

    @PostMapping(value = "/updatePromotion")
    public void updatePromotion(@RequestParam("promotionId") Integer promotionId) {
        //System.out.println("test : " + promotionId);
        PromotionDetail updateStatusPromotion = this.promotionDetailRepository.findAllById(promotionId);
        updateStatusPromotion.setStatus("inactive");
        this.promotionDetailRepository.saveAndFlush(updateStatusPromotion);
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public PromotionDetailController(PromotionDetailRepository promotionDetailRepository) {
        this.promotionDetailRepository = promotionDetailRepository;
    }

    @GetMapping(value="/getPromotion")
    public List<PromotionDetailDto> getPromotion() throws ParseException {
        String dateInStr = this.dateFormat.format(new Date());
        Date date = this.dateFormat.parse(dateInStr);
        List<PromotionDetailDto> promotionDetailDtos = new ArrayList<>();
        int flowerLifeTime = 0;
        int profitFlower = 0;
        int calProfit = 0;
        String typeFlower = null;

        List<Stock> stocks = this.stockRepository.findAllByFloristId(1);
        List<Stock> newStocks = new ArrayList<>();
        List<Integer> newFlower = new ArrayList<>();
        outer:
        for(Stock stock: stocks){
            String rr = stock.getLot().toString();
            Date d2 = this.dateFormat.parse(rr);
            long chkExp = date.getTime()  - d2.getTime();
            int diffDays = (int) (chkExp / (24 * 60 * 60 * 1000));
            //System.out.println("Test 2 : " + diffDays);
            //หา Life Time ของดอกไม้ที่ใกล้หมดอายุ และ ชนิดของดอกไม้
            List<Flower> flowers = this.flowerRepository.findAllById(stock.getFlower().getFlowerId());
            for (Flower flower: flowers){
                flowerLifeTime = flower.getLifeTime();
                typeFlower = flower.getFlowerType();
            }

            int expired = flowerLifeTime - diffDays;
            List<PromotionProfit> promotionProfits = this.promotionProfitRepository.findAllByAgeAndFlowerType(expired, typeFlower);
            for (PromotionProfit profit: promotionProfits){
                if (profit != null) {
                    //System.out.println("อายุคงเหลือ = " + expired);
                    profitFlower = profit.getProfit();
                }else{
                    break;
                }
            }

            if(expired > 0 && expired <= 3){
//                System.out.println("profitFlower = " + profitFlower);
//                System.out.println("Diff Date : " + diffDays + ", Test : " + stock.getFlower().getFlowerName());
//                System.out.println("-------------------------------");
                newStocks.add(stock);
                newFlower.add(stock.getFlower().getFlowerId());
            }else{
                continue outer;
            }
        }

        //System.out.println("newFlower : " + newFlower);
        List<FlowerFormulaDetail> flower = new ArrayList<>();
        for (Stock qStock: newStocks){
//            System.out.println("Flower ID : " + qStock.getFlower().getFlowerId());
//            System.out.println("Name : " + qStock.getFlower().getFlowerName());
//            System.out.println("จำนวนคงเหลือใน Stock : " + qStock.getQuantity());
            List<FlowerFormulaDetail> flowerList  = this.flowerFormulaDetailRepository.findAllByFlowerIdAndQuantityLessThanEqualOrderByQuantityDesc(qStock.getFlower().getFlowerId(),qStock.getQuantity());
            for (FlowerFormulaDetail q: flowerList) {
                List<FlowerFormulaDetail> formulas = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(q.getFlowerFormula().getId());
                int unit = qStock.getQuantity() / q.getQuantity();
                //System.out.println("จำนวนที่สามารถทำได้ : " + unit);
                if (unit > 0) {
                    if(unit > 5){
                        unit = 5;
                    }
                    flower.add(q);
//                    System.out.println("FlowerFormula Name : " + q.getFlowerFormula().getName());
                }

                calProfit = (int) ((q.getFlowerFormula().getPrice()) - (int) ((q.getFlowerFormula().getPrice() * profitFlower)/ 100));
                PromotionDetailDto promotionDetailDto = new PromotionDetailDto();
                promotionDetailDto.setId(q.getFlowerFormula().getId());
                promotionDetailDto.setFormulaName(q.getFlowerFormula().getName());
                promotionDetailDto.setSize(q.getFlowerFormula().getSize());
                promotionDetailDto.setQuantity(unit);
                promotionDetailDto.setProfit(calProfit);
                promotionDetailDto.setTotalProfit((calProfit * unit));
                promotionDetailDto.setPrice(calProfit);
                promotionDetailDto.setLocationName(qStock.getFlorist().getName());
                promotionDetailDto.setImage(q.getFlowerFormula().getImagePath());
                promotionDetailDtos.add(promotionDetailDto);

//                System.out.println("--");
//                System.out.println("Flower FormulaId ID : " + q.getFlowerFormula().getId());
//                System.out.println("Name : " + q.getFlowerFormula().getName());
//                System.out.println("Size : " + q.getFlowerFormula().getSize());
//                System.out.println("Quantity : " + unit);
//                System.out.println("Profit : " + q.getFlowerFormula().getPrice());
//                System.out.println("Total Profit : " + (q.getFlowerFormula().getPrice() * unit));
//                System.out.println("Price : " + q.getFlowerFormula().getPrice());
//                System.out.println("Location : " + qStock.getFlorist().getName());
//                System.out.println("imagePath : " + q.getFlowerFormula().getImagePath());
//                System.out.println("======================================");

            }
        }

        return promotionDetailDtos;
    }

    @GetMapping(value="/getPromotionSuggest")
    public List<PromotionDetailCurrentDto> getPromotionSuggest() throws ParseException {
        String dateInStrs = this.dateFormat.format(new Date());
        Date date = this.dateFormat.parse(dateInStrs);
        List<PromotionDetailCurrentDto> promotionDetailCurrentDtos = new ArrayList<>();
        int flowerLifeTime = 0;
        int profitFlower = 0;
        int calProfitCurrent = 0;
        String typeFlower = null;
        int unit = 0;

        //List<Stock> stocks = this.stockRepository.findAllByFloristId(1);
        List<Stock> stocks = this.stockRepository.findAllByFlowerId(12);
        List<Stock> newStocks = new ArrayList<>();
        List<Integer> newFlower = new ArrayList<>();
        outer:
        for(Stock stock: stocks){
            String rr = stock.getLot().toString();
            Date d2 = this.dateFormat.parse(rr);
            long chkExp = date.getTime()  - d2.getTime();
            int diffDays = (int) (chkExp / (24 * 60 * 60 * 1000));
            List<Flower> flowers = this.flowerRepository.findAllById(stock.getFlower().getFlowerId());
            for (Flower flower: flowers){
                flowerLifeTime = flower.getLifeTime();
                typeFlower = flower.getFlowerType();
            }

            int expired = flowerLifeTime - diffDays;
            List<PromotionProfit> promotionProfits = this.promotionProfitRepository.findAllByAgeAndFlowerType(expired, typeFlower);
            for (PromotionProfit profit: promotionProfits){
                if (profit != null) {
                    profitFlower = profit.getProfit();
                }else{
                    break;
                }
            }

            if(expired > 0 && expired <= 3){
                newStocks.add(stock);
                newFlower.add(stock.getFlower().getFlowerId());
            }else{
                continue outer;
            }
        }

        List<FlowerFormulaDetail> flower = new ArrayList<>();
        for (Stock qStock: newStocks){
            //System.out.println("Flower Id : " + qStock.getFlower().getFlowerId());
            //System.out.println("Quantity : " + qStock.getQuantity());
            //1
            List<FlowerFormulaDetail> flowerList  = this.flowerFormulaDetailRepository.findAllByFlowerIdAndQuantityLessThanEqualOrderByQuantityDesc(qStock.getFlower().getFlowerId(),qStock.getQuantity());
            for (FlowerFormulaDetail q: flowerList) {
                //2
                List<FlowerFormulaDetail> formulas = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(q.getFlowerFormula().getId());
                PromotionDetailCurrentDto promotionDetailCurrentDto = new PromotionDetailCurrentDto();
                for (FlowerFormulaDetail f: formulas) {
                    if (f.getFlower().getMainCategory().equals("รอง")) {
                        List<Stock> stockCat = this.stockRepository.findStockByFlowerId(f.getFlower().getFlowerId());
                        unit = qStock.getQuantity() / q.getQuantity();
                        if (unit > 0) {
                            if (unit > 5) {
                                unit = 5;
                            }
                            flower.add(q);
                        }

                        int ss = 0;
                        for (Stock cc: stockCat) {
                            ss = cc.getQuantity();
                        }

                        String quantityDto = f.getFlower().getFlowerName() + f.getQuantity() + f.getFlower().getUnit();
                        String stockDto = ss + f.getFlower().getUnit();
                        promotionDetailCurrentDto.setQuantity(unit);
                        promotionDetailCurrentDto.setQuantityFlower(quantityDto);
                        promotionDetailCurrentDto.setStock(stockDto);

                    }

                    calProfitCurrent = (int) (q.getFlowerFormula().getPrice() - ((int) (q.getFlowerFormula().getPrice() * profitFlower / 100)));
                    promotionDetailCurrentDto.setId(q.getFlowerFormula().getId());
                    promotionDetailCurrentDto.setFormulaName(q.getFlowerFormula().getName());
                    promotionDetailCurrentDto.setSize(q.getFlowerFormula().getSize());
                    promotionDetailCurrentDto.setProfit(calProfitCurrent);
                    promotionDetailCurrentDto.setTotalProfit(calProfitCurrent * unit);
                    promotionDetailCurrentDto.setPrice(calProfitCurrent);
                    promotionDetailCurrentDto.setLocationName(qStock.getFlorist().getName());
                    promotionDetailCurrentDto.setImage(q.getFlowerFormula().getImagePath());

                }
                promotionDetailCurrentDtos.add(promotionDetailCurrentDto);
            }
        }

        return promotionDetailCurrentDtos;
    }

}
