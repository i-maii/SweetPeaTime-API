package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.*;
import com.sweetpeatime.sweetpeatime.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value="/promotionDetail")
public class PromotionDetailController {

    @Autowired
    PromotionDetailRepository promotionDetailRepository;

    @Autowired
    FlowerFormulaDetailRepository flowerFormulaDetailRepository;

    @Autowired
    FlowerFormulaRepository flowerFormulaRepository;

    @Autowired
    StockRepository stockRepository;

    @Autowired
    FlowerRepository flowerRepository;

    @Autowired
    PromotionProfitRepository promotionProfitRepository;

    @Autowired
    PromotionRepository promotionRepository;

    @Autowired
    ConfigurationsRepository configurationsRepository;

    @Autowired
    FloristRepository floristRepository;

    //@GetMapping(value = "/currentPromotion")
    @GetMapping(value = "/currentPromotion")
    public List<PromotionDetail> getCurrentPromotion() {
        return this.promotionDetailRepository.findPromotionDetailsByStatus("active");
    }

    @PostMapping(value = "/updatePromotion")
    public void updatePromotion(@RequestParam("promotionId") Integer promotionId) {
        //System.out.println("test : " + promotionId);
        PromotionDetail updateStatusPromotion = this.promotionDetailRepository.findAllById(promotionId);
        updateStatusPromotion.setStatus("inactive");
        this.promotionDetailRepository.saveAndFlush(updateStatusPromotion);
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public PromotionDetailController(PromotionDetailRepository promotionDetailRepository) {
        this.promotionDetailRepository = promotionDetailRepository;
    }

    @GetMapping(value="/getPromotion")
    public List<PromotionDetail> getPromotion() throws ParseException {
        String dateInStr = dateFormat.format(new Date());
        Date date = dateFormat.parse(dateInStr);
        List<PromotionDetailDto> promotionDetailDtos = new ArrayList<>();
        int flowerLifeTime = 0;
        int profitFlower = 0;
        int calProfit = 0;
        int numPromotion = 0;
        int availableQuantity = 0;
        int availableQuantitySum = 9999;
        int profitSum = 120;
        int profitFormula = 0;
        int totalProfit = 0;
        int available = 0;
        int availableTotal = 9999;
        String typeFlower = null;

        LocalDate currentDate = LocalDate.now();
        LocalDate dateTime = currentDate.minus(7, ChronoUnit.DAYS);
        Date dateReverse = dateFormat.parse(String.valueOf(dateTime));

        //For Check Duplicate
        LocalDate dateTime1 = currentDate.minus(14, ChronoUnit.DAYS);
        LocalDate dateTime2 = currentDate.minus(1, ChronoUnit.DAYS);
        LocalDate expireDate = currentDate.plus(2, ChronoUnit.DAYS);
        Date dateFrom = dateFormat.parse(String.valueOf(dateTime1));
        Date dateTo = dateFormat.parse(String.valueOf(dateTime2));
        ZoneId zoneId = ZoneId.systemDefault();
        Date expiryDate = Date.from(expireDate.atStartOfDay(zoneId).toInstant());
        List<Promotion> listPromotion = this.promotionRepository.findAllByDateGreaterThanAndDateLessThanEqual(dateFrom, dateTo);

        //Get number of promotion
        List<Configurations> config = this.configurationsRepository.findAllByName("NUMBER_OF_PROMOTION");
        for (Configurations c: config){
            numPromotion = c.getValue();
        }

        List<Florist> florist = this.floristRepository.findAll();
        List<PromotionDetail> promotionDetails = new ArrayList<>();
        List<PromotionDetail> promotionDetailArrayList = new ArrayList<>();
        List<Stock> newStocks = new ArrayList<>();
        for (Florist florist1: florist) {
            List<Stock> stocks = this.stockRepository.findAllByFloristIdOrderByQuantityDesc(florist1.getId());
            List<Integer> newFlower = new ArrayList<>();

            //List ดอกไม้ที่เหลืออยู่ในสต๊อกที่ใกล้หมดอายุ
            outer:
            for (Stock stock : stocks) {
                String rr = stock.getLot().toString();
                Date d2 = dateFormat.parse(rr);
                long chkExp = date.getTime() - d2.getTime();
                int diffDays = (int) (chkExp / (24 * 60 * 60 * 1000));

                //หา Life Time ของดอกไม้ที่ใกล้หมดอายุ และ ชนิดของดอกไม้
                Flower flower = this.flowerRepository.findAllById(stock.getFlower().getFlowerId());

                flowerLifeTime = flower.getLifeTime();
                typeFlower = flower.getFlowerType();



                int expired = flowerLifeTime - diffDays;
                if (expired > 0 && expired <= 3) {
                    newStocks.add(stock);
                    newFlower.add(stock.getFlower().getFlowerId());
                } else {
                    continue outer;
                }
            }

            List<FlowerFormulaDetail> listFormula = new ArrayList<>();
            for (Stock qStock : newStocks) {
                String chk1 = "Y";
                String chk2 = "Y";
                int unit = 0;
                int remain = 0;

                //List flower formula detail
                List<FlowerFormulaDetail> flowerList = this.flowerFormulaDetailRepository.findAllByFlowerIdAndQuantityLessThanEqualOrderByFlowerId(qStock.getFlower().getFlowerId(), qStock.getQuantity());

                //Check Duplicate in List formula
                recalDup:
                for (FlowerFormulaDetail q : flowerList) {
                    for (FlowerFormulaDetail lf: listFormula) {
                        if (q.getFlowerFormula().getId().equals(lf.getFlowerFormula().getId())) {
                            continue recalDup;
                        }
                    }
                    listFormula.add(q);
                }
            }

            int chkSize = 0;
            recalculateFormula:
            for (FlowerFormulaDetail list: listFormula){
                List<FlowerFormulaDetail> formulas = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(list.getFlowerFormula().getId());
                availableQuantitySum = 9999;
                profitSum = 120;
                chkSize = 0;
                for (FlowerFormulaDetail ff : formulas) {
                    //System.out.println("FlowerId : " + ff.getFlower().getFlowerId() + ", FlowerName : " + ff.getFlower().getFlowerName() + ", Flower Formula Name : " + ff.getFlowerFormula().getName());
                    chkFlower:
                    for (Stock pp: newStocks) {

                        if (pp.getFlorist().getId().equals(florist1.getId())){
                            //System.out.println("Test flowerId : "+ pp.getFlower().getFlowerId() + "Test flowerName : " + pp.getFlower().getFlowerName());
                            if (ff.getFlower().getFlowerId().equals(pp.getFlower().getFlowerId())) {
                                chkSize = chkSize + 1;
                                availableQuantity = pp.getQuantity() / ff.getQuantity();

                                String lot = pp.getLot().toString();
                                Date d2 = dateFormat.parse(lot);
                                long chkExp = date.getTime() - d2.getTime();
                                int diffDays = (int) (chkExp / (24 * 60 * 60 * 1000));

                                //หา Life Time ของดอกไม้ที่ใกล้หมดอายุ และ ชนิดของดอกไม้
                                Flower chkFlower = this.flowerRepository.findAllById(pp.getFlower().getFlowerId());
                                flowerLifeTime = chkFlower.getLifeTime();
                                typeFlower = chkFlower.getFlowerType();


                                int expired = flowerLifeTime - diffDays;
                                List<PromotionProfit> promotionProfits = this.promotionProfitRepository.findAllByAgeAndFlowerType(expired, typeFlower);
                                for (PromotionProfit profit : promotionProfits) {
                                    if (profit != null) {
                                        profitFlower = profit.getProfit();
                                    } else {
                                        break;
                                    }
                                }
                            } else {

                                continue chkFlower;
                            }
                        }
                    }
                    //System.out.println("chkSize : " + chkSize);
                    availableQuantitySum = Math.min(availableQuantitySum, availableQuantity);
                    profitSum = Math.min(profitSum, profitFlower);
                    profitFormula = (ff.getFlowerFormula().getPrice() * profitSum) / 100;
                    totalProfit = ((ff.getFlowerFormula().getPrice() * profitSum) / 100) * availableQuantitySum;
                }

                if (formulas.size() == chkSize && availableQuantitySum > 0){

                    PromotionDetail promotionDetail = new PromotionDetail();
                    promotionDetail.setStatus("active");
                    promotionDetail.setProfit((double) profitFormula);
                    promotionDetail.setQuantity(availableQuantitySum);
                    promotionDetail.setExpiryDate(expiryDate);
                    promotionDetail.setFlorist(florist1);
                    promotionDetail.setFlowerFormula(list.getFlowerFormula());
                    promotionDetail.setPrice(profitFormula);
                    promotionDetails.add(promotionDetail);
//
//                    System.out.println("Formula : " + list.getFlowerFormula().getName());
//                    System.out.println("availableQuantity : " + availableQuantitySum);
//                    System.out.println("profitFlower : " + profitSum);
//                    System.out.println("profit : " + profitFormula);
//                    System.out.println("totalProfit : " + totalProfit);
//                    System.out.println("------------------------------");
                }else {
                    continue recalculateFormula;
                }
            }
        }

//        System.out.println("===================================");

        int loop = 0;
        int maxTotal = 0;
        int remain = 0;
        int sizeFormula = 0;
        String flag1 = "Y";
        String flag2 = "Y";
        PromotionDetail promotionDetail = new PromotionDetail();
        sizeFormula = promotionDetails.size();
        for (int i = 0; i < sizeFormula; i++) {
//            System.out.println("Check loop : " + promotionDetails.size());
            maxTotal = 0;
            for (PromotionDetail promotionDetail1 : promotionDetails) {
//                System.out.println("" + i +": Promotion formula : " + promotionDetail1.getFlowerFormula().getName() + ", Quantity : " + promotionDetail1.getQuantity());
                int total = promotionDetail1.getQuantity() * promotionDetail1.getPrice();
                if (maxTotal < total) {
                    maxTotal = total;
                    promotionDetail = promotionDetail1;
                }
            }

            if (i == 0) {
//                System.out.println("List " + i + " : " + promotionDetail.getFlowerFormula().getName() + ", Total profit = " + maxTotal);
                promotionDetailArrayList.add(promotionDetail);
                for (PromotionDetail promotionDetail2: promotionDetailArrayList){
                    List<FlowerFormulaDetail> formulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetail.getFlowerFormula().getId());
                    for(FlowerFormulaDetail formulaDetail: formulaDetails){
                        for(Stock stock: newStocks){
                            if (stock.getFlower().getFlowerId().equals(formulaDetail.getFlower().getFlowerId()) && promotionDetail2.getFlorist().getId().equals(stock.getFlorist().getId())){
                                remain = stock.getQuantity() - (formulaDetail.getQuantity() * promotionDetail2.getQuantity());
//                                System.out.println("Flower = " + formulaDetail.getFlower().getFlowerName() + ", จำนวนที่ต้องใช้ = " +formulaDetail.getQuantity());
//                                System.out.println("ช่อที่สามารถทำได้ = " + promotionDetail2.getQuantity() + ", เหลือจาก stock = " + stock.getQuantity() + ", ดอกไม้คงเหลือ = " + remain);
//                                System.out.println("**");
                                stock.setQuantity(remain);
                            }
                        }
                    }
                }
                promotionDetails.remove(promotionDetail);
            }else{
//                System.out.println("List " + i + " : " + promotionDetail.getFlowerFormula().getName() + ", Total profit = " + maxTotal);
                List<FlowerFormulaDetail> formulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetail.getFlowerFormula().getId());
                flag1 = "Y";
                flag2 = "Y";
                loop = 0;
                for(FlowerFormulaDetail formulaDetail: formulaDetails){
                    for(Stock stock: newStocks){
                        if (stock.getFlower().getFlowerId().equals(formulaDetail.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stock.getFlorist().getId())){
                            loop = loop + 1;
                            remain = stock.getQuantity() - (formulaDetail.getQuantity() * promotionDetail.getQuantity());
                            available = stock.getQuantity() / formulaDetail.getQuantity();
//                            System.out.println("available : " + available);
//                            System.out.println("Flower = " + formulaDetail.getFlower().getFlowerName() + ", จำนวนที่ต้องใช้ = " +formulaDetail.getQuantity());
//                            System.out.println("ช่อที่สามารถทำได้ = " + promotionDetail.getQuantity() + ", เหลือจาก stock = " + stock.getQuantity() + ", ดอกไม้คงเหลือ = " + remain);
//                            System.out.println("**");
                            if (remain < 0) {
                                flag1 = "N";
                            }else{
                                flag2 = "Y";
                            }
                        }
                    }

                    if ((flag1.equals("Y") && flag2.equals("Y")) && (formulaDetails.size() == loop)){
                        for(FlowerFormulaDetail formulaDetail1: formulaDetails) {
                            for (Stock stock : newStocks) {
                                if (stock.getFlower().getFlowerId().equals(formulaDetail1.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stock.getFlorist().getId())) {
                                    remain = stock.getQuantity() - (formulaDetail1.getQuantity() * promotionDetail.getQuantity());
                                    stock.setQuantity(remain);
                                }
                            }
                        }
                    }

                    availableTotal = Math.min(availableTotal, available);
                    if (flag1.equals("N") || flag2.equals("N")) {
                        promotionDetail.setQuantity(availableTotal);
                        for(FlowerFormulaDetail formulaDetail2: formulaDetails) {
                            for (Stock stock : newStocks) {
                                if (stock.getFlower().getFlowerId().equals(formulaDetail2.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stock.getFlorist().getId())) {
                                    remain = stock.getQuantity() - (formulaDetail2.getQuantity() * promotionDetail.getQuantity());
                                    stock.setQuantity(remain);
                                }
                            }
                        }
                    }
                }
//                System.out.println("Test : " + promotionDetail.getFlowerFormula().getName() + ", Quantity : " + promotionDetail.getQuantity());
                promotionDetailArrayList.add(promotionDetail);
                if (promotionDetail.getQuantity().equals(0)){
                    promotionDetailArrayList.remove(promotionDetail);
                }
                promotionDetails.remove(promotionDetail);
            }
        }

        //order by total profit desc
        List<PromotionDetail> promotionDetailArrayList2 = new ArrayList<>();
        PromotionDetail promotionDetailNew = new PromotionDetail();
        int sizeFormulaNew = promotionDetailArrayList.size();
        int max = 0;
        for (int i = 0; i < sizeFormulaNew; i++) {
            max = 0;
            for (PromotionDetail promotionDetail1 : promotionDetailArrayList) {
                int totalNew = promotionDetail1.getQuantity() * promotionDetail1.getPrice();
                if (max < totalNew) {
                    max = totalNew;
                    promotionDetailNew = promotionDetail1;
                }
            }
            promotionDetailArrayList2.add(promotionDetailNew);
            promotionDetailArrayList.remove(promotionDetailNew);
        }

        //getPromotionSuggest(newStocks);
        return promotionDetailArrayList2;
    }

    public List<Stock> getPromotionStock() throws ParseException {
        String dateInStr = dateFormat.format(new Date());
        Date date = dateFormat.parse(dateInStr);
        List<PromotionDetailDto> promotionDetailDtos = new ArrayList<>();
        int flowerLifeTime = 0;
        int profitFlower = 0;
        int calProfit = 0;
        int numPromotion = 0;
        int availableQuantity = 0;
        int availableQuantitySum = 9999;
        int profitSum = 120;
        int profitFormula = 0;
        int totalProfit = 0;
        int available = 0;
        int availableTotal = 9999;
        String typeFlower = null;

        LocalDate currentDate = LocalDate.now();
        LocalDate dateTime = currentDate.minus(7, ChronoUnit.DAYS);
        Date dateReverse = dateFormat.parse(String.valueOf(dateTime));

        //For Check Duplicate
        LocalDate dateTime1 = currentDate.minus(14, ChronoUnit.DAYS);
        LocalDate dateTime2 = currentDate.minus(1, ChronoUnit.DAYS);
        LocalDate expireDate = currentDate.plus(2, ChronoUnit.DAYS);
        Date dateFrom = dateFormat.parse(String.valueOf(dateTime1));
        Date dateTo = dateFormat.parse(String.valueOf(dateTime2));
        ZoneId zoneId = ZoneId.systemDefault();
        Date expiryDate = Date.from(expireDate.atStartOfDay(zoneId).toInstant());
        List<Promotion> listPromotion = this.promotionRepository.findAllByDateGreaterThanAndDateLessThanEqual(dateFrom, dateTo);

        //Get number of promotion
        List<Configurations> config = this.configurationsRepository.findAllByName("NUMBER_OF_PROMOTION");
        for (Configurations c: config){
            numPromotion = c.getValue();
        }

        List<Florist> florist = this.floristRepository.findAll();
        List<PromotionDetail> promotionDetails = new ArrayList<>();
        List<PromotionDetail> promotionDetailArrayList = new ArrayList<>();
        List<Stock> newStocks = new ArrayList<>();
        for (Florist florist1: florist) {
            List<Stock> stocks = this.stockRepository.findAllByFloristIdOrderByQuantityDesc(florist1.getId());
            List<Integer> newFlower = new ArrayList<>();

            //List ดอกไม้ที่เหลืออยู่ในสต๊อกที่ใกล้หมดอายุ
            outer:
            for (Stock stock : stocks) {
                String rr = stock.getLot().toString();
                Date d2 = dateFormat.parse(rr);
                long chkExp = date.getTime() - d2.getTime();
                int diffDays = (int) (chkExp / (24 * 60 * 60 * 1000));

                //หา Life Time ของดอกไม้ที่ใกล้หมดอายุ และ ชนิดของดอกไม้
                Flower flower = this.flowerRepository.findAllById(stock.getFlower().getFlowerId());

                flowerLifeTime = flower.getLifeTime();
                typeFlower = flower.getFlowerType();



                int expired = flowerLifeTime - diffDays;
                if (expired > 0 && expired <= 3) {
                    newStocks.add(stock);
                    newFlower.add(stock.getFlower().getFlowerId());
                } else {
                    continue outer;
                }
            }

            List<FlowerFormulaDetail> listFormula = new ArrayList<>();
            for (Stock qStock : newStocks) {
                String chk1 = "Y";
                String chk2 = "Y";
                int unit = 0;
                int remain = 0;

                //List flower formula detail
                List<FlowerFormulaDetail> flowerList = this.flowerFormulaDetailRepository.findAllByFlowerIdAndQuantityLessThanEqualOrderByFlowerId(qStock.getFlower().getFlowerId(), qStock.getQuantity());

                //Check Duplicate in List formula
                recalDup:
                for (FlowerFormulaDetail q : flowerList) {
                    for (FlowerFormulaDetail lf: listFormula) {
                        if (q.getFlowerFormula().getId().equals(lf.getFlowerFormula().getId())) {
                            continue recalDup;
                        }
                    }
                    listFormula.add(q);
                }
            }

            int chkSize = 0;
            recalculateFormula:
            for (FlowerFormulaDetail list: listFormula){
                List<FlowerFormulaDetail> formulas = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(list.getFlowerFormula().getId());
                availableQuantitySum = 9999;
                profitSum = 120;
                chkSize = 0;
                for (FlowerFormulaDetail ff : formulas) {
                    //System.out.println("FlowerId : " + ff.getFlower().getFlowerId() + ", FlowerName : " + ff.getFlower().getFlowerName() + ", Flower Formula Name : " + ff.getFlowerFormula().getName());
                    chkFlower:
                    for (Stock pp: newStocks) {

                        if (pp.getFlorist().getId().equals(florist1.getId())){
                            //System.out.println("Test flowerId : "+ pp.getFlower().getFlowerId() + "Test flowerName : " + pp.getFlower().getFlowerName());
                            if (ff.getFlower().getFlowerId().equals(pp.getFlower().getFlowerId())) {
                                chkSize = chkSize + 1;
                                availableQuantity = pp.getQuantity() / ff.getQuantity();

                                String lot = pp.getLot().toString();
                                Date d2 = dateFormat.parse(lot);
                                long chkExp = date.getTime() - d2.getTime();
                                int diffDays = (int) (chkExp / (24 * 60 * 60 * 1000));

                                //หา Life Time ของดอกไม้ที่ใกล้หมดอายุ และ ชนิดของดอกไม้
                                Flower chkFlower = this.flowerRepository.findAllById(pp.getFlower().getFlowerId());
                                flowerLifeTime = chkFlower.getLifeTime();
                                typeFlower = chkFlower.getFlowerType();


                                int expired = flowerLifeTime - diffDays;
                                List<PromotionProfit> promotionProfits = this.promotionProfitRepository.findAllByAgeAndFlowerType(expired, typeFlower);
                                for (PromotionProfit profit : promotionProfits) {
                                    if (profit != null) {
                                        profitFlower = profit.getProfit();
                                    } else {
                                        break;
                                    }
                                }
                            } else {

                                continue chkFlower;
                            }
                        }
                    }
                    //System.out.println("chkSize : " + chkSize);
                    availableQuantitySum = Math.min(availableQuantitySum, availableQuantity);
                    profitSum = Math.min(profitSum, profitFlower);
                    profitFormula = (ff.getFlowerFormula().getPrice() * profitSum) / 100;
                    totalProfit = ((ff.getFlowerFormula().getPrice() * profitSum) / 100) * availableQuantitySum;
                }

                if (formulas.size() == chkSize && availableQuantitySum > 0){

                    PromotionDetail promotionDetail = new PromotionDetail();
                    promotionDetail.setStatus("active");
                    promotionDetail.setProfit((double) profitFormula);
                    promotionDetail.setQuantity(availableQuantitySum);
                    promotionDetail.setExpiryDate(expiryDate);
                    promotionDetail.setFlorist(florist1);
                    promotionDetail.setFlowerFormula(list.getFlowerFormula());
                    promotionDetail.setPrice(profitFormula);
                    promotionDetails.add(promotionDetail);
//
//                    System.out.println("Formula : " + list.getFlowerFormula().getName());
//                    System.out.println("availableQuantity : " + availableQuantitySum);
//                    System.out.println("profitFlower : " + profitSum);
//                    System.out.println("profit : " + profitFormula);
//                    System.out.println("totalProfit : " + totalProfit);
//                    System.out.println("------------------------------");
                }else {
                    continue recalculateFormula;
                }
            }
        }

//        System.out.println("===================================");

        int loop = 0;
        int maxTotal = 0;
        int remain = 0;
        int sizeFormula = 0;
        String flag1 = "Y";
        String flag2 = "Y";
        PromotionDetail promotionDetail = new PromotionDetail();
        sizeFormula = promotionDetails.size();
        for (int i = 0; i < sizeFormula; i++) {
//            System.out.println("Check loop : " + promotionDetails.size());
            maxTotal = 0;
            for (PromotionDetail promotionDetail1 : promotionDetails) {
//                System.out.println("" + i +": Promotion formula : " + promotionDetail1.getFlowerFormula().getName() + ", Quantity : " + promotionDetail1.getQuantity());
                int total = promotionDetail1.getQuantity() * promotionDetail1.getPrice();
                if (maxTotal < total) {
                    maxTotal = total;
                    promotionDetail = promotionDetail1;
                }
            }

            if (i == 0) {
//                System.out.println("List " + i + " : " + promotionDetail.getFlowerFormula().getName() + ", Total profit = " + maxTotal);
                promotionDetailArrayList.add(promotionDetail);
                for (PromotionDetail promotionDetail2: promotionDetailArrayList){
                    List<FlowerFormulaDetail> formulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetail.getFlowerFormula().getId());
                    for(FlowerFormulaDetail formulaDetail: formulaDetails){
                        for(Stock stock: newStocks){
                            if (stock.getFlower().getFlowerId().equals(formulaDetail.getFlower().getFlowerId()) && promotionDetail2.getFlorist().getId().equals(stock.getFlorist().getId())){
                                remain = stock.getQuantity() - (formulaDetail.getQuantity() * promotionDetail2.getQuantity());
//                                System.out.println("Flower = " + formulaDetail.getFlower().getFlowerName() + ", จำนวนที่ต้องใช้ = " +formulaDetail.getQuantity());
//                                System.out.println("ช่อที่สามารถทำได้ = " + promotionDetail2.getQuantity() + ", เหลือจาก stock = " + stock.getQuantity() + ", ดอกไม้คงเหลือ = " + remain);
//                                System.out.println("**");
                                stock.setQuantity(remain);
                            }
                        }
                    }
                }
                promotionDetails.remove(promotionDetail);
            }else{
//                System.out.println("List " + i + " : " + promotionDetail.getFlowerFormula().getName() + ", Total profit = " + maxTotal);
                List<FlowerFormulaDetail> formulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetail.getFlowerFormula().getId());
                flag1 = "Y";
                flag2 = "Y";
                loop = 0;
                for(FlowerFormulaDetail formulaDetail: formulaDetails){
                    for(Stock stock: newStocks){
                        if (stock.getFlower().getFlowerId().equals(formulaDetail.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stock.getFlorist().getId())){
                            loop = loop + 1;
                            remain = stock.getQuantity() - (formulaDetail.getQuantity() * promotionDetail.getQuantity());
                            available = stock.getQuantity() / formulaDetail.getQuantity();
//                            System.out.println("available : " + available);
//                            System.out.println("Flower = " + formulaDetail.getFlower().getFlowerName() + ", จำนวนที่ต้องใช้ = " +formulaDetail.getQuantity());
//                            System.out.println("ช่อที่สามารถทำได้ = " + promotionDetail.getQuantity() + ", เหลือจาก stock = " + stock.getQuantity() + ", ดอกไม้คงเหลือ = " + remain);
//                            System.out.println("**");
                            if (remain < 0) {
                                flag1 = "N";
                            }else{
                                flag2 = "Y";
                            }
                        }
                    }

                    if ((flag1.equals("Y") && flag2.equals("Y")) && (formulaDetails.size() == loop)){
                        for(FlowerFormulaDetail formulaDetail1: formulaDetails) {
                            for (Stock stock : newStocks) {
                                if (stock.getFlower().getFlowerId().equals(formulaDetail1.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stock.getFlorist().getId())) {
                                    remain = stock.getQuantity() - (formulaDetail1.getQuantity() * promotionDetail.getQuantity());
                                    stock.setQuantity(remain);
                                }
                            }
                        }
                    }

                    availableTotal = Math.min(availableTotal, available);
                    if (flag1.equals("N") || flag2.equals("N")) {
                        promotionDetail.setQuantity(availableTotal);
                        for(FlowerFormulaDetail formulaDetail2: formulaDetails) {
                            for (Stock stock : newStocks) {
                                if (stock.getFlower().getFlowerId().equals(formulaDetail2.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stock.getFlorist().getId())) {
                                    remain = stock.getQuantity() - (formulaDetail2.getQuantity() * promotionDetail.getQuantity());
                                    stock.setQuantity(remain);
                                }
                            }
                        }
                    }
                }
//                System.out.println("Test : " + promotionDetail.getFlowerFormula().getName() + ", Quantity : " + promotionDetail.getQuantity());
                promotionDetailArrayList.add(promotionDetail);
                if (promotionDetail.getQuantity().equals(0)){
                    promotionDetailArrayList.remove(promotionDetail);
                }
                promotionDetails.remove(promotionDetail);
            }
        }

        //order by total profit desc
        List<PromotionDetail> promotionDetailArrayList2 = new ArrayList<>();
        PromotionDetail promotionDetailNew = new PromotionDetail();
        int sizeFormulaNew = promotionDetailArrayList.size();
        int max = 0;
        for (int i = 0; i < sizeFormulaNew; i++) {
            max = 0;
            for (PromotionDetail promotionDetail1 : promotionDetailArrayList) {
                int totalNew = promotionDetail1.getQuantity() * promotionDetail1.getPrice();
                if (max < totalNew) {
                    max = totalNew;
                    promotionDetailNew = promotionDetail1;
                }
            }
            promotionDetailArrayList2.add(promotionDetailNew);
            promotionDetailArrayList.remove(promotionDetailNew);
        }

        return newStocks;
    }

    @GetMapping(value="/getPromotionSuggest")
    public List<PromotionDetailCurrentDto> getPromotionSuggest() throws ParseException {
        List<Stock> stockList = getPromotionStock();
        System.out.println("++++++ getPromotionSuggest ++++++");
        String dateInStr = dateFormat.format(new Date());
        Date date = dateFormat.parse(dateInStr);
        LocalDate currentDate = LocalDate.now();
        LocalDate dateTime1 = currentDate.minus(4, ChronoUnit.DAYS);
        Date dateFrom = dateFormat.parse(String.valueOf(dateTime1));
        Date dateTo = dateFormat.parse(dateInStr);
        int chkSize = 0;
        int availableQuantity = 0;
        int flowerLifeTime = 0;
        int profitFlower = 0;
        int availableQuantitySum;
        int calQuantity = 0;
        int calProfitCurrent = 0;
        int totalProfit = 0;
        int floristId = 0;
        String floristName = null;
        String typeFlower = null;

        List<Stock> stocks = new ArrayList<>();
        List<FlowerFormulaDetail> formulaDetails = new ArrayList<>();
        List<PromotionDetailCurrentDto> promotionDetailCurrentDtos = new ArrayList<>();
        PromotionDetailCurrentDto promotionDetailCurrentDto = new PromotionDetailCurrentDto();

        for (Stock stock : stockList) {
            if (stock.getQuantity() > 0) {
                stocks.add(stock);
            }
        }

        for (Stock stock : stocks) {
            System.out.println("Stock Florist : " + stock.getFlorist().getName() + ", Flower name : " + stock.getFlower().getFlowerName() + ",Quantity : " + stock.getQuantity());
            Flower flower = this.flowerRepository.findAllById(stock.getFlower().getFlowerId());

                if(flower.getMainCategory().equals("หลัก")){
                    List<FlowerFormulaDetail> flowerList = this.flowerFormulaDetailRepository.findAllByFlowerIdAndQuantityLessThanEqualOrderByFlowerId(stock.getFlower().getFlowerId(), stock.getQuantity());
                    if (flowerList.size() > 0) {
                        recalList:
                        for (FlowerFormulaDetail formulaDetail : flowerList) {
                            for (FlowerFormulaDetail flowerFormulaDetail : formulaDetails) {
                                if (formulaDetail.getFlowerFormula().getId().equals(flowerFormulaDetail.getFlowerFormula().getId())) {
                                    continue recalList;
                                }
                            }
                            formulaDetails.add(formulaDetail);
                        }
                    }
                }else{
                    stocks.remove(stock);
                }

        }

        availableQuantitySum = 9999;
        String quantityFlower = null;
        Integer stockAvailable = 0;
        for (FlowerFormulaDetail flowerFormulaDetail : formulaDetails) {
            List<FlowerFormulaDetail> formulaDetails1 = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(flowerFormulaDetail.getFlowerFormula().getId());
            chkSize = 0;
            int i = 0;
            for (FlowerFormulaDetail formulaDetail : formulaDetails1) {
                i = i +1;
                chkFlower:
                for (Stock stock1 : stocks) {
                    if (stock1.getFlower().getFlowerId().equals(formulaDetail.getFlower().getFlowerId())) {
                        chkSize = chkSize + 1;
                        availableQuantity = stock1.getQuantity() / formulaDetail.getQuantity();
                        availableQuantitySum = Math.min(availableQuantitySum, availableQuantity);

                        floristId = stock1.getFlorist().getId();
                        floristName = stock1.getFlorist().getName();
                        String lot = stock1.getLot().toString();
                        Date d2 = dateFormat.parse(lot);
                        long chkExp = date.getTime() - d2.getTime();
                        int diffDays = (int) (chkExp / (24 * 60 * 60 * 1000));

                        //หา Life Time ของดอกไม้ที่ใกล้หมดอายุ และ ชนิดของดอกไม้
                        Flower chkFlower = this.flowerRepository.findAllById(stock1.getFlower().getFlowerId());
                        flowerLifeTime = chkFlower.getLifeTime();
                        typeFlower = chkFlower.getFlowerType();


                        int expired = flowerLifeTime - diffDays;
                        List<PromotionProfit> promotionProfits = this.promotionProfitRepository.findAllByAgeAndFlowerType(expired, typeFlower);
                        for (PromotionProfit profit : promotionProfits) {
                            if (profit != null) {
                                profitFlower = profit.getProfit();
                            } else {
                                break;
                            }
                        }
                    } else {
                        continue chkFlower;
                    }
                }

                if (i > 1) {
                    calQuantity = availableQuantitySum * formulaDetail.getQuantity();
                    List<Stock> stockList1 = this.stockRepository.findAllByFlowerIdAndLotGreaterThanEqualAndLotLessThanEqualAndFloristId(formulaDetail.getFlower().getFlowerId(),dateFrom, dateTo, floristId);
                    for(Stock stock: stockList1){
                        quantityFlower = formulaDetail.getFlower().getFlowerName() + calQuantity + stock.getUnit();
                        stockAvailable = stock.getQuantity();
                    }
                }
            }

            calProfitCurrent = (flowerFormulaDetail.getFlowerFormula().getPrice() * profitFlower) / 100;
            totalProfit = ((flowerFormulaDetail.getFlowerFormula().getPrice() * profitFlower) / 100) * availableQuantitySum;
            System.out.println("Test : " + flowerFormulaDetail.getFlowerFormula().getName() + ", availableQuantity : " + availableQuantity);

            promotionDetailCurrentDto.setId(flowerFormulaDetail.getFlowerFormula().getId());
            promotionDetailCurrentDto.setFormulaName(flowerFormulaDetail.getFlowerFormula().getName());
            promotionDetailCurrentDto.setSize(flowerFormulaDetail.getFlowerFormula().getSize());
            promotionDetailCurrentDto.setProfit(calProfitCurrent);
            promotionDetailCurrentDto.setQuantity(availableQuantity);
            promotionDetailCurrentDto.setTotalProfit(totalProfit);
            promotionDetailCurrentDto.setPrice(calProfitCurrent);
            promotionDetailCurrentDto.setLocationName(floristName);
            promotionDetailCurrentDto.setImage(flowerFormulaDetail.getFlowerFormula().getImagePath());
            promotionDetailCurrentDto.setQuantityFlower(quantityFlower);
            promotionDetailCurrentDto.setStock(stockAvailable);
            promotionDetailCurrentDtos.add(promotionDetailCurrentDto);
        }

        return promotionDetailCurrentDtos;

    }

    @PostMapping("/addPromotion")
    public void addPromotionDetail(
            @RequestBody AddPromotionDto addPromotionDto
    ) {
        List<PromotionDetail> lastActivePromotions = this.promotionDetailRepository.findPromotionDetailsByStatus("active");

        Promotion promotion = new Promotion();
        promotion.setDate(new Date());
        this.promotionRepository.saveAndFlush(promotion);

        for (PromotionDetail s : lastActivePromotions) {
            PromotionDetail promotionDetail = new PromotionDetail();
            promotionDetail.setProfit(s.getProfit());
            promotionDetail.setPrice(s.getPrice());
            promotionDetail.setQuantity(s.getQuantity());
            promotionDetail.setQuantitySold(s.getQuantitySold());
            promotionDetail.setStatus(s.getStatus());
            promotionDetail.setPromotion(promotion);
            promotionDetail.setFlowerFormula(s.getFlowerFormula());
            promotionDetail.setExpiryDate(s.getExpiryDate());
            promotionDetail.setFlorist(s.getFlorist());
            promotionDetail.setType(s.getType());
            this.promotionDetailRepository.saveAndFlush(promotionDetail);

            s.setStatus("inactive");
            this.promotionDetailRepository.saveAndFlush(s);
        }

        PromotionDetail newPromotionDetail = new PromotionDetail();
        newPromotionDetail.setProfit(addPromotionDto.getProfit());
        newPromotionDetail.setPrice(addPromotionDto.getPrice());
        newPromotionDetail.setQuantity(addPromotionDto.getQuantity());
        newPromotionDetail.setStatus("active");
        newPromotionDetail.setPromotion(promotion);
        newPromotionDetail.setFlowerFormula(this.flowerFormulaRepository.findFlowerFormulaByName(addPromotionDto.getFormulaName()));
        newPromotionDetail.setFlorist(this.floristRepository.findFloristByName(addPromotionDto.getLocationName()));
        this.promotionDetailRepository.saveAndFlush(newPromotionDetail);

        //System.out.println(lastActivePromotions);
    }

    @PostMapping("/recalculatePromotion")
    public List<PromotionDetail> getRecalculatePromotion(
            @RequestBody AddPromotionDto addPromotionDto
    ) throws ParseException {
        System.out.println("=========== Recalculate =========");
        System.out.println("FormulaName : " + addPromotionDto.getFormulaName() + ", Quantity : " + addPromotionDto.getQuantity());

        FlowerFormula flowerFormulas = this.flowerFormulaRepository.findFlowerFormulaByName(addPromotionDto.getFormulaName());

        List<FlowerFormulaDetail> formulaDetailList = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(flowerFormulas.getId());
        for(FlowerFormulaDetail formulaDetail: formulaDetailList){
            System.out.println("flower : " + formulaDetail.getFlower().getFlowerName() + ", จำนวนที่ต้องใช้ : " + formulaDetail.getQuantity());
        }
        System.out.println("-----------");
        //List<PromotionDetail> promotionDetails = new ArrayList<>();

        String dateInStr = dateFormat.format(new Date());
        Date date = dateFormat.parse(dateInStr);

        int flowerLifeTime = 0;
        int profitFlower = 0;
        int calProfit = 0;
        int numPromotion = 0;
        int availableQuantity = 0;
        int availableQuantitySum = 9999;
        int profitSum = 120;
        int profitFormula = 0;
        int totalProfit = 0;
        int available = 0;
        int availableTotal = 9999;
        String typeFlower = null;

        LocalDate currentDate = LocalDate.now();
        LocalDate dateTime = currentDate.minus(7, ChronoUnit.DAYS);
        Date dateReverse = dateFormat.parse(String.valueOf(dateTime));

        //For Check Duplicate
        LocalDate dateTime1 = currentDate.minus(14, ChronoUnit.DAYS);
        LocalDate dateTime2 = currentDate.minus(1, ChronoUnit.DAYS);
        LocalDate expireDate = currentDate.plus(2, ChronoUnit.DAYS);
        Date dateFrom = dateFormat.parse(String.valueOf(dateTime1));
        Date dateTo = dateFormat.parse(String.valueOf(dateTime2));
        ZoneId zoneId = ZoneId.systemDefault();
        Date expiryDate = Date.from(expireDate.atStartOfDay(zoneId).toInstant());
        List<Promotion> listPromotion = this.promotionRepository.findAllByDateGreaterThanAndDateLessThanEqual(dateFrom, dateTo);

        //Get number of promotion
        List<Configurations> config = this.configurationsRepository.findAllByName("NUMBER_OF_PROMOTION");
        for (Configurations c: config){
            numPromotion = c.getValue();
        }

        List<Florist> florist = this.floristRepository.findAll();
        List<PromotionDetail> promotionDetails = new ArrayList<>();
        List<PromotionDetail> promotionDetailArrayList = new ArrayList<>();
        List<Stock> newStocks = new ArrayList<>();
        for (Florist florist1: florist) {
            List<Stock> stocks = this.stockRepository.findAllByFloristIdOrderByQuantityDesc(florist1.getId());
            List<Integer> newFlower = new ArrayList<>();

            //List ดอกไม้ที่เหลืออยู่ในสต๊อกที่ใกล้หมดอายุ
            outer:
            for (Stock stock : stocks) {
                String rr = stock.getLot().toString();
                Date d2 = dateFormat.parse(rr);
                long chkExp = date.getTime() - d2.getTime();
                int diffDays = (int) (chkExp / (24 * 60 * 60 * 1000));

                //หา Life Time ของดอกไม้ที่ใกล้หมดอายุ และ ชนิดของดอกไม้
                Flower flower = this.flowerRepository.findAllById(stock.getFlower().getFlowerId());
                flowerLifeTime = flower.getLifeTime();
                typeFlower = flower.getFlowerType();


                int expired = flowerLifeTime - diffDays;
                if (expired > 0 && expired <= 3) {
                    newStocks.add(stock);
                    newFlower.add(stock.getFlower().getFlowerId());
                    //System.out.println("1) flower name : " + stock.getFlower().getFlowerName() + ", เหลือ : " + stock.getQuantity() + ", location : " + stock.getFlorist().getName());
                    for(FlowerFormulaDetail formulaDetail: formulaDetailList){
                        if (stock.getFlower().getFlowerId().equals(formulaDetail.getFlower().getFlowerId()) && stock.getFlorist().getName().equals(addPromotionDto.getLocationName())) {
                            int remains = stock.getQuantity() - (formulaDetail.getQuantity() * addPromotionDto.getQuantity());
                            stock.setQuantity(remains);
                        }
                    }
                } else {
                    continue outer;
                }
                //System.out.println("-----------");
            }

            /*for (Stock stock: newStocks){
                System.out.println("flower name : " + stock.getFlower().getFlowerName() + ", florist : " + stock.getFlorist().getName() + ", Quantity : " + stock.getQuantity());
            }
            System.out.println("=====");*/

            List<FlowerFormulaDetail> listFormula = new ArrayList<>();
            for (Stock qStock : newStocks) {
                String chk1 = "Y";
                String chk2 = "Y";
                int unit = 0;
                int remain = 0;
//                List<StockDto> stockChk = new ArrayList<>();
//                StockDto stockRecalculate = new StockDto();

                //List flower formula detail
                List<FlowerFormulaDetail> flowerList = this.flowerFormulaDetailRepository.findAllByFlowerIdAndQuantityLessThanEqualOrderByFlowerId(qStock.getFlower().getFlowerId(), qStock.getQuantity());

                //Check Duplicate in List formula
                recalDup:
                for (FlowerFormulaDetail q : flowerList) {
                    for (FlowerFormulaDetail lf: listFormula) {
                        if (q.getFlowerFormula().getId().equals(lf.getFlowerFormula().getId())) {
                            continue recalDup;
                        }
                    }
                    listFormula.add(q);
                }
            }

            //System.out.println("florist1 : " + florist1.getName());
            int chkSize = 0;
            recalculateFormula:
            for (FlowerFormulaDetail list: listFormula){
                List<FlowerFormulaDetail> formulas = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(list.getFlowerFormula().getId());
                availableQuantitySum = 9999;
                profitSum = 120;
                chkSize = 0;
                 for (FlowerFormulaDetail ff : formulas) {
                   chkFlower:
                    for (Stock pp: newStocks) {
                        if (pp.getFlorist().getId().equals(florist1.getId())){
                            if (ff.getFlower().getFlowerId().equals(pp.getFlower().getFlowerId())) {
                                chkSize = chkSize + 1;
                                //System.out.println("chkSize : " + chkSize + ", location name : " + pp.getFlorist().getName() + ", flowername : " + pp.getFlower().getFlowerName() + ", lot : " + pp.getLot());
                                availableQuantity = pp.getQuantity() / ff.getQuantity();
                                String lot = pp.getLot().toString();
                                Date d2 = dateFormat.parse(lot);
                                long chkExp = date.getTime() - d2.getTime();
                                int diffDays = (int) (chkExp / (24 * 60 * 60 * 1000));

                                //หา Life Time ของดอกไม้ที่ใกล้หมดอายุ และ ชนิดของดอกไม้
                                Flower chkFlower = this.flowerRepository.findAllById(pp.getFlower().getFlowerId());
                                flowerLifeTime = chkFlower.getLifeTime();
                                typeFlower = chkFlower.getFlowerType();


                                int expired = flowerLifeTime - diffDays;
                                List<PromotionProfit> promotionProfits = this.promotionProfitRepository.findAllByAgeAndFlowerType(expired, typeFlower);
                                for (PromotionProfit profit : promotionProfits) {
                                    if (profit != null) {
                                        profitFlower = profit.getProfit();
                                    } else {
                                        break;
                                    }
                                }
                            } else {

                                continue chkFlower;
                            }
                        }
                    }
                    availableQuantitySum = Math.min(availableQuantitySum, availableQuantity);
                    profitSum = Math.min(profitSum, profitFlower);
                    profitFormula = (ff.getFlowerFormula().getPrice() * profitSum) / 100;
                    totalProfit = ((ff.getFlowerFormula().getPrice() * profitSum) / 100) * availableQuantitySum;

                }
//                System.out.println("formula 1 : " + list.getFlowerFormula().getName());
//                System.out.println("chkSize : " + chkSize);
//                System.out.println("formulas.size : " + formulas.size());
//                System.out.println("availableQuantitySum : " + availableQuantitySum);
//                System.out.println("------------------------------");
                if (formulas.size() == chkSize && availableQuantitySum > 0){

                    PromotionDetail promotionDetail = new PromotionDetail();
                    promotionDetail.setStatus("active");
                    promotionDetail.setProfit((double) profitFormula);
                    promotionDetail.setQuantity(availableQuantitySum);
                    promotionDetail.setExpiryDate(expiryDate);
                    promotionDetail.setFlorist(florist1);
                    promotionDetail.setFlowerFormula(list.getFlowerFormula());
                    promotionDetail.setPrice(profitFormula);
                    promotionDetails.add(promotionDetail);

                    System.out.println("Formula : " + list.getFlowerFormula().getName());
                    System.out.println("availableQuantity : " + availableQuantitySum);
                    System.out.println("profitFlower : " + profitSum);
                    System.out.println("profit : " + profitFormula);
                    System.out.println("totalProfit : " + totalProfit);
                    System.out.println("------------------------------");
                }else {
                    continue recalculateFormula;
                }
            }
        }

        for(PromotionDetail promotionDetail1: promotionDetails){
            System.out.println("promotion list : " + promotionDetail1.getFlowerFormula().getName() + ",Quantity : " + promotionDetail1.getQuantity() + ", location name : " + promotionDetail1.getFlorist().getName());
        }

        int loop = 0;
        int maxTotal = 0;
        int remain = 0;
        int sizeFormula = 0;
        String flag1 = "Y";
        String flag2 = "Y";
        PromotionDetail promotionDetail = new PromotionDetail();
        sizeFormula = promotionDetails.size();
        for (int i = 0; i < sizeFormula; i++) {
            maxTotal = 0;
            for (PromotionDetail promotionDetail1 : promotionDetails) {
                int total = promotionDetail1.getQuantity() * promotionDetail1.getPrice();
                if (maxTotal < total) {
                    maxTotal = total;
                    promotionDetail = promotionDetail1;
                }
            }

            if (i == 0) {
                promotionDetailArrayList.add(promotionDetail);
                for (PromotionDetail promotionDetail2: promotionDetailArrayList){
                    List<FlowerFormulaDetail> formulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetail.getFlowerFormula().getId());
                    for(FlowerFormulaDetail formulaDetail: formulaDetails){
                        for(Stock stock: newStocks){
                            if (stock.getFlower().getFlowerId().equals(formulaDetail.getFlower().getFlowerId()) && promotionDetail2.getFlorist().getId().equals(stock.getFlorist().getId())){
                                remain = stock.getQuantity() - (formulaDetail.getQuantity() * promotionDetail2.getQuantity());
                                stock.setQuantity(remain);
                            }
                        }
                    }
                }
                promotionDetails.remove(promotionDetail);
            }else{
                List<FlowerFormulaDetail> formulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetail.getFlowerFormula().getId());
                flag1 = "Y";
                flag2 = "Y";
                loop = 0;
                for(FlowerFormulaDetail formulaDetail: formulaDetails){
                    for(Stock stock: newStocks){
                        if (stock.getFlower().getFlowerId().equals(formulaDetail.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stock.getFlorist().getId())){
                            loop = loop + 1;
//                            System.out.println("======");
//                            System.out.println("getFlowerName : " + stock.getFlower().getFlowerName() );
//                            System.out.println("stock getQuantity : " + stock.getQuantity());
//                            System.out.println("formulaDetail getQuantity : " + formulaDetail.getQuantity());
//                            System.out.println("promotionDetail getQuantity : " + promotionDetail.getQuantity());
                            remain = stock.getQuantity() - (formulaDetail.getQuantity() * promotionDetail.getQuantity());
                            available = stock.getQuantity() / formulaDetail.getQuantity();
                            if (remain < 0) {
                                flag1 = "N";
                            }else{
                                flag2 = "Y";
                            }
                        }
                    }

                    System.out.println("flag1 : " + flag1 + ", flag2 : " + flag2);

                    if ((flag1.equals("Y") && flag2.equals("Y")) && (formulaDetails.size() == loop)){
                        for(FlowerFormulaDetail formulaDetail1: formulaDetails) {
                            for (Stock stock : newStocks) {
                                if (stock.getFlower().getFlowerId().equals(formulaDetail1.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stock.getFlorist().getId())) {
                                    remain = stock.getQuantity() - (formulaDetail1.getQuantity() * promotionDetail.getQuantity());
                                    stock.setQuantity(remain);
                                }
                            }
                        }
                    }

                    availableTotal = Math.min(availableTotal, available);
                    if (flag1.equals("N") || flag2.equals("N")) {
                        promotionDetail.setQuantity(availableTotal);
                        for(FlowerFormulaDetail formulaDetail2: formulaDetails) {
                            for (Stock stock : newStocks) {
                                if (stock.getFlower().getFlowerId().equals(formulaDetail2.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stock.getFlorist().getId())) {
                                    remain = stock.getQuantity() - (formulaDetail2.getQuantity() * promotionDetail.getQuantity());
                                    stock.setQuantity(remain);
                                }
                            }
                        }
                    }
                }

                promotionDetailArrayList.add(promotionDetail);
                if (promotionDetail.getQuantity().equals(0)){
                    promotionDetailArrayList.remove(promotionDetail);
                }
                promotionDetails.remove(promotionDetail);
            }
        }

        System.out.println("==========");
        for(PromotionDetail promotionDetail1: promotionDetailArrayList){
            System.out.println("promotion list : " + promotionDetail1.getFlowerFormula().getName() + ",Quantity : " + promotionDetail1.getQuantity() + ", location name : " + promotionDetail1.getFlorist().getName());
        }

        return promotionDetailArrayList;
    }
}
