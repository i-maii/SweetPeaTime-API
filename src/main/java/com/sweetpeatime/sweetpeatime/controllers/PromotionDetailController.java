package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.*;
import com.sweetpeatime.sweetpeatime.repositories.*;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.origin.SystemEnvironmentOrigin;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.stream.Collectors;

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

    @Autowired
    PromotionDetailLogRepository promotionDetailLogRepository;

    @Autowired
    SalesOrderRepository salesOrderRepository;

    @Autowired
    SalesOrderDetailRepository salesOrderDetailRepository;

    @Autowired
    EventPromotionRepository eventPromotionRepository;

    @Autowired
    FloristFeeRepository floristFeeRepository;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping(value = "/currentPromotion")
    public List<PromotionDetail> getCurrentPromotion() {
        return this.promotionDetailRepository.findPromotionDetailsByStatusAndQuantityGreaterThanOrderByTotalProfitDesc("active", 0);
    }

    @GetMapping(value = "/maxPromotion")
    public List<Configurations> getMaxPromotion() {
        return this.configurationsRepository.findAllByName("NUMBER_OF_PROMOTION");
    }

    @GetMapping(value = "/getAllPromotion")
    public List<PromotionDetail> getAllPromotion() {


        return this.promotionDetailRepository.findAll();
    }

    @PostMapping(value = "/updatePromotion")
    public void updatePromotion(@RequestParam("promotionId") Integer promotionId) {
        PromotionDetail updateStatusPromotion = this.promotionDetailRepository.findAllById(promotionId);
        updateStatusPromotion.setStatus("inactive");
        this.promotionDetailRepository.saveAndFlush(updateStatusPromotion);
    }

    public PromotionDetailController(PromotionDetailRepository promotionDetailRepository) {
        this.promotionDetailRepository = promotionDetailRepository;
    }

    @GetMapping(value="/getPromotion")
    public List<PromotionDetail> getPromotion() throws ParseException {
        Date date = new Date();
        //List<PromotionDetailDto> promotionDetailDtos = new ArrayList<>();
        int flowerLifeTime = 0;
        int profitFlower = 0;
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
        LocalDate dateTime2 = currentDate.minus(7, ChronoUnit.DAYS);
        LocalDate expireDate = currentDate.plus(2, ChronoUnit.DAYS);
        LocalDate createDate = currentDate.plus(0, ChronoUnit.DAYS);
        LocalDate ranking = currentDate.minus(1, ChronoUnit.DAYS);
        LocalDate rating = currentDate.minus(365, ChronoUnit.DAYS);

        ZoneId zoneId = ZoneId.systemDefault();
        Date dateFrom = Date.from(dateTime1.atStartOfDay(zoneId).toInstant());
        Date dateTo = Date.from(dateTime2.atStartOfDay(zoneId).toInstant());
        Date expiryDate = Date.from(expireDate.atStartOfDay(zoneId).toInstant());
        Date createtDate = Date.from(createDate.atStartOfDay(zoneId).toInstant());
        Date rankingTime = Date.from(ranking.atStartOfDay(zoneId).toInstant());
        Date ratingSalesOrder = Date.from(rating.atStartOfDay(zoneId).toInstant());

        List<StockRemainDto> stockRemainDtos = new ArrayList<>();
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
                long chkExp = date.getTime() - stock.getLot().getTime();
                int diffDays = (int) (chkExp / (24 * 60 * 60 * 1000));

                //หา Life Time ของดอกไม้ที่ใกล้หมดอายุ และ ชนิดของดอกไม้
                Flower flower = this.flowerRepository.findAllById(stock.getFlower().getFlowerId());
                flowerLifeTime = flower.getLifeTime();
                typeFlower = flower.getFlowerType();

                int expired = flowerLifeTime - diffDays;
                if (typeFlower.equals("ดอกไม้สด")){
                    if (expired > 0 && expired <= 3) {
                        newStocks.add(stock);
                        newFlower.add(stock.getFlower().getFlowerId());
                        StockRemainDto stockRemainDto = new StockRemainDto();
                        stockRemainDto.setId(stock.getFlower().getFlowerId());
                        stockRemainDto.setFlowerName(stock.getFlower().getFlowerName());
                        stockRemainDto.setRemainQuantity(stock.getQuantity());
                        stockRemainDto.setFloristId(stock.getFlorist().getId());
                        stockRemainDto.setFloristName(stock.getFlorist().getName());
                        stockRemainDto.setLot(stock.getLot());
                        stockRemainDtos.add(stockRemainDto);
                    } else {
                        continue outer;
                    }
                }else{
                    PromotionProfit profit = this.promotionProfitRepository.findAllByFlowerType("ดอกไม้แห้ง");
                    int ageFlower = profit.getAge();
                    int expiredFlower = (flowerLifeTime * ageFlower / 100);
                    if (expired > 0 && expired <= expiredFlower) {
                        newStocks.add(stock);
                        newFlower.add(stock.getFlower().getFlowerId());
                        StockRemainDto stockRemainDto = new StockRemainDto();
                        stockRemainDto.setId(stock.getFlower().getFlowerId());
                        stockRemainDto.setFlowerName(stock.getFlower().getFlowerName());
                        stockRemainDto.setRemainQuantity(stock.getQuantity());
                        stockRemainDto.setFloristId(stock.getFlorist().getId());
                        stockRemainDto.setFloristName(stock.getFlorist().getName());
                        stockRemainDto.setLot(stock.getLot());
                        stockRemainDtos.add(stockRemainDto);
                    }
                }
            }

            List<PromotionDetail> promotionDetailList1 = this.promotionDetailRepository.findPromotionDetailsByStatus("active");
            List<FlowerFormulaDetail> listFormula = new ArrayList<>();
            List<FlowerFormulaDetail> formulaDetails = new ArrayList<>();
            for (Stock qStock : newStocks) {
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

                    //Check Duplicate week
                    List<Promotion> promotions = this.promotionRepository.findAllByDateGreaterThanAndDateLessThanEqual(dateFrom, dateTo);
                    for(Promotion promotion: promotions){
                        List<PromotionDetail> promotionDetailList = this.promotionDetailRepository.findAllByPromotionId(promotion.getId());
                        for(PromotionDetail promotionDetail1: promotionDetailList){
                            if(q.getFlowerFormula().getId().equals(promotionDetail1.getFlowerFormula().getId())){
                                continue recalDup;
                            }
                        }
                    }

                    //ไม่นำ formula ที่ถูกเลือกเป็นโปรโมชั่นปัจจุบันมาแสดงอีก
                    for (PromotionDetail promotionDetail: promotionDetailList1){
                        if(promotionDetail.getFlowerFormula().getId().equals(q.getFlowerFormula().getId())){
                            continue recalDup;
                        }
                    }

                    listFormula.add(q);
                }
            }

            int chkSize = 0;
            Date lot = null;
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
                                availableQuantity = pp.getQuantity() / ff.getQuantity();

                                if (ff.getFlower().getMainCategory().equals("หลัก")){
                                    lot = pp.getLot();
                                }

                                long chkExp = date.getTime() - pp.getLot().getTime();
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
                    profitFormula = ff.getFlowerFormula().getPrice() - ((ff.getFlowerFormula().getPrice() * profitSum) / 100);
                }

                if (formulas.size() == chkSize && availableQuantitySum > 0){

                    Configurations configurations = this.configurationsRepository.getValueByName("MAX_QUANTITY_PROMOTION");
                    int maxQuantity = configurations.getValue();
                    if (availableQuantitySum > maxQuantity){
                        availableQuantitySum = maxQuantity;
                    }

                    PromotionDetail promotionDetail = new PromotionDetail();
                    promotionDetail.setStatus("active");
                    promotionDetail.setProfit((double) profitFormula);
                    promotionDetail.setQuantity(availableQuantitySum);
                    promotionDetail.setExpiryDate(expiryDate);
                    promotionDetail.setFlorist(florist1);
                    promotionDetail.setFlowerFormula(list.getFlowerFormula());
                    promotionDetail.setPrice(profitFormula);
                    promotionDetail.setLotStock(lot);
                    promotionDetail.setTotalProfit((int) (availableQuantitySum * (double) profitFormula));
                    promotionDetails.add(promotionDetail);

                }else {
                    continue recalculateFormula;
                }
            }
        }

        //Check Ranking
        int chkRanking = 0;
        int checkLoop = 0;
        List<PromotionDetail> sortPromotionDetail = promotionDetails.stream()
                .sorted(Comparator.comparing(PromotionDetail::getTotalProfit).reversed())
                .collect(Collectors.toList());
        List<PromotionDetailLog> promotionDetailLogs = this.promotionDetailLogRepository.findPromotionDetailLogsByStatusAndCreateDateLessThanEqualOrderByTotalProfitDesc("active", rankingTime);
        for(PromotionDetailLog promotionDetailLog: promotionDetailLogs){
            checkLoop = checkLoop + 1;
            if (checkLoop <= 3) {
                for (PromotionDetail promotionDetail : sortPromotionDetail) {
                    if (promotionDetailLog.getFlowerFormula().getId().equals(promotionDetail.getFlowerFormula().getId())) {
                        chkRanking = chkRanking + 1;
                    }
                }
            }else{
                break;
            }
        }

        if (chkRanking < 3) {
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
                    for (PromotionDetail promotionDetail2 : promotionDetailArrayList) {
                        List<FlowerFormulaDetail> formulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetail.getFlowerFormula().getId());
                        for (FlowerFormulaDetail formulaDetail : formulaDetails) {
                            for (StockRemainDto stockRemainDto1 : stockRemainDtos) {
                                if (stockRemainDto1.getId().equals(formulaDetail.getFlower().getFlowerId()) && promotionDetail2.getFlorist().getId().equals(stockRemainDto1.getFloristId())) {
                                    remain = stockRemainDto1.getRemainQuantity() - (formulaDetail.getQuantity() * promotionDetail2.getQuantity());
                                    stockRemainDto1.setRemainQuantity(remain);
                                }
                            }
                        }
                    }
                    promotionDetails.remove(promotionDetail);
                } else {
                    List<FlowerFormulaDetail> formulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetail.getFlowerFormula().getId());
                    flag1 = "Y";
                    flag2 = "Y";
                    loop = 0;
                    for (FlowerFormulaDetail formulaDetail : formulaDetails) {
                        for (StockRemainDto stockRemainDto1 : stockRemainDtos) {
                            if (stockRemainDto1.getId().equals(formulaDetail.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stockRemainDto1.getFloristId())) {
                                loop = loop + 1;
                                remain = stockRemainDto1.getRemainQuantity() - (formulaDetail.getQuantity() * promotionDetail.getQuantity());
                                available = stockRemainDto1.getRemainQuantity() / formulaDetail.getQuantity();
                                if (remain < 0) {
                                    flag1 = "N";
                                } else {
                                    flag2 = "Y";
                                }
                            }
                        }

                        if ((flag1.equals("Y") && flag2.equals("Y")) && (formulaDetails.size() == loop)) {
                            for (FlowerFormulaDetail formulaDetail1 : formulaDetails) {
                                for (StockRemainDto stockRemainDto1 : stockRemainDtos) {
                                    if (stockRemainDto1.getId().equals(formulaDetail1.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stockRemainDto1.getFloristId())) {
                                        remain = stockRemainDto1.getRemainQuantity() - (formulaDetail1.getQuantity() * promotionDetail.getQuantity());
                                        stockRemainDto1.setRemainQuantity(remain);
                                    }
                                }
                            }
                        }

                        availableTotal = Math.min(availableTotal, available);
                        if (flag1.equals("N") || flag2.equals("N")) {
                            promotionDetail.setQuantity(availableTotal);
                            for (FlowerFormulaDetail formulaDetail2 : formulaDetails) {
                                for (StockRemainDto stockRemainDto1 : stockRemainDtos) {
                                    if (stockRemainDto1.getId().equals(formulaDetail2.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stockRemainDto1.getFloristId())) {
                                        remain = stockRemainDto1.getRemainQuantity() - (formulaDetail2.getQuantity() * promotionDetail.getQuantity());
                                        stockRemainDto1.setRemainQuantity(remain);
                                    }
                                }
                            }
                        }
                    }
                    promotionDetailArrayList.add(promotionDetail);
                    if (promotionDetail.getQuantity().equals(0)) {
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

            //Check Rating Sales Order
            List<SalesOrder> salesOrderList = new ArrayList<>();
            List<PromotionDetail> promotionDetails1 = new ArrayList<>();
            List<PromotionDetail> promotionDetailList = new ArrayList<>();
            List<SalesOrder> salesOrders = this.salesOrderRepository.findAllByReceiverDateTimeGreaterThanEqualAndReceiverDateTimeLessThanAndStatus(ratingSalesOrder, createtDate,"Complete");
            List<EventPromotion> eventPromotions = this.eventPromotionRepository.findAll();
            salesOrder:
            for(SalesOrder salesOrder: salesOrders){
                salesOrderList.add(salesOrder);
                for(EventPromotion eventPromotion: eventPromotions){
                    int resultStartDate = salesOrder.getReceiverDateTime().compareTo(eventPromotion.getStartDate());
                    int resultEndDate = salesOrder.getReceiverDateTime().compareTo(eventPromotion.getEndDate());
                    //resultStartDate > 0 : salesOrder.getReceiverDateTime is after eventPromotion.getStartDate()
                    //resultEndDate < 0 : salesOrder.getReceiverDateTime is before eventPromotion.getStartDate()
                    if(resultStartDate > 0 && resultEndDate < 0){
                        salesOrderList.remove(salesOrder);
                        continue salesOrder;
                    }
                }
            }

            int total = 0;
            int totalProfit1;
            String flowerCategory = null;
            int formulaId = 0;
            int cntSales = 0;
            int cntSalesOrder = 0;
            int chkStock1 = 0;
            int chkStock2 = 0;
            for(PromotionDetail promotionDetail1: promotionDetailArrayList2){
                promotionDetailList.add(promotionDetail1);
                totalProfit1 = (int) (promotionDetail1.getQuantity() * promotionDetail1.getProfit());
                 if(totalProfit1 == total){
                     List<FlowerFormulaDetail> formulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetail1.getFlowerFormula().getId());
                     for(FlowerFormulaDetail formulaDetail: formulaDetails){
                         if(formulaDetail.getFlower().getMainCategory().equals("หลัก")){
                             flowerCategory = formulaDetail.getFlower().getFlowerCategory();
                         }
                     }

                     for(PromotionDetail promotionDetail2: promotionDetailArrayList2){
                        int totalProfit2 = (int) (promotionDetail2.getQuantity() * promotionDetail2.getProfit());
                        if(totalProfit1 == totalProfit2 && !promotionDetail2.getFlowerFormula().getId().equals(promotionDetail1.getFlowerFormula().getId())){
                            List<FlowerFormulaDetail> formulaDetails1 = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetail2.getFlowerFormula().getId());
                            for(FlowerFormulaDetail flowerFormulaDetail: formulaDetails1){
                                if(flowerFormulaDetail.getFlower().getMainCategory().equals("หลัก") &&  flowerFormulaDetail.getFlower().getFlowerCategory().equals(flowerCategory)){
                                    formulaId = promotionDetail2.getFlowerFormula().getId();
                                    promotionDetails1.add(promotionDetail2);
                                    break;
                                }
                            }
                        }
                    }

                     for(SalesOrder salesOrder: salesOrderList){
                         List<SalesOrderDetail> salesOrderDetails = this.salesOrderDetailRepository.findAllBySalesOrderId(salesOrder.getId());
                         for(SalesOrderDetail salesOrderDetail: salesOrderDetails){
                             if (promotionDetail1.getFlowerFormula().getId().equals(salesOrderDetail.getFlowerFormula().getId())){
                                 cntSales = cntSales + 1;
                             }else if (salesOrderDetail.getFlowerFormula().getId().equals(formulaId)){
                                 cntSalesOrder= cntSalesOrder + 1;
                             }
                         }
                    }

                     if(cntSales > cntSalesOrder){
                         for(PromotionDetail promotionDetail2: promotionDetails1){
                             promotionDetailList.remove(promotionDetail2);
                         }
;                     }else if (cntSales < cntSalesOrder){
                         promotionDetailList.remove(promotionDetail1);
                     }else{
                         List<FlowerFormulaDetail> formulaDetails1 = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetail1.getFlowerFormula().getId());
                         for(FlowerFormulaDetail formula: formulaDetails1){
                             if(formula.getFlower().getMainCategory().equals("หลัก")){
                                 for (Stock qStock : newStocks) {
                                     if (formula.getFlower().getFlowerId().equals(qStock.getFlower().getFlowerId()) && qStock.getFlorist().getId().equals(promotionDetail1.getFlorist().getId())){
                                         chkStock1 = qStock.getQuantity();
                                     }
                                 }
                             }
                         }

                         for(PromotionDetail promotionDetail2: promotionDetails1){
                             List<FlowerFormulaDetail> formulaDetailList = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetail2.getFlowerFormula().getId());
                             for(FlowerFormulaDetail formulaDetail: formulaDetailList){
                                 if(formulaDetail.getFlower().getMainCategory().equals("หลัก")){
                                     for (Stock qStock : newStocks) {
                                         if (formulaDetail.getFlower().getFlowerId().equals(qStock.getFlower().getFlowerId()) && qStock.getFlorist().getId().equals(promotionDetail1.getFlorist().getId())){
                                             chkStock2 = qStock.getQuantity();
                                         }
                                     }
                                 }
                             }
                         }

                         if (chkStock1 > chkStock2){
                             for(PromotionDetail promotionDetail2: promotionDetails1){
                                 promotionDetailList.remove(promotionDetail2);
                             }
                         }else if (chkStock1 < chkStock2){
                             promotionDetailList.remove(promotionDetail1);
                         }
                     }
                 }
                total = totalProfit1;
            }

            for (PromotionDetail promotionDetail1: promotionDetailList){

                FloristFee floristFee = this.floristFeeRepository.findFloristFeeByFloristIdAndSize(promotionDetail1.getFlorist().getId(), promotionDetail1.getFlowerFormula().getSize());
                int promotionPrice = (int) (promotionDetail1.getProfit() + floristFee.getFee());
                if (promotionPrice % 100 != 0) {
                    promotionPrice = (promotionPrice - (promotionPrice % 100)) + 90;
                }else{
                    promotionPrice = promotionPrice + 90;
                }

                totalProfit = (int) (promotionDetail1.getQuantity() * promotionDetail1.getProfit());
                PromotionDetailLog promotionDetailLog = new PromotionDetailLog();
                promotionDetailLog.setProfit(promotionDetail1.getProfit());
                promotionDetailLog.setPrice((double) promotionPrice);
                promotionDetailLog.setQuantity(promotionDetail1.getQuantity());
                promotionDetailLog.setStatus("active");
                promotionDetailLog.setFlowerFormula(promotionDetail1.getFlowerFormula());
                promotionDetailLog.setExpiryDate(promotionDetail1.getExpiryDate());
                promotionDetailLog.setFlorist(promotionDetail1.getFlorist());
                promotionDetailLog.setPromotionType("normal");
                promotionDetailLog.setCreateDate(createtDate);
                promotionDetailLog.setTotalProfit(totalProfit);
                promotionDetailLog.setLotStock(promotionDetail1.getLotStock());
                this.promotionDetailLogRepository.saveAndFlush(promotionDetailLog);
            }

            List<PromotionDetailLog> promotionDetailLogList = this.promotionDetailLogRepository.findPromotionDetailLogsByStatusAndCreateDateLessThanEqualOrderByTotalProfitDesc("active", rankingTime);
            for(PromotionDetailLog promotionDetailLog: promotionDetailLogList){
                promotionDetailLog.setStatus("inactive");
                this.promotionDetailLogRepository.saveAndFlush(promotionDetailLog);
            }

            getPromotionSuggest(stockRemainDtos, "getPromotion");
            return promotionDetailList;
        }else{
            return null;
        }
    }

    @GetMapping(value="/getPromotionSuggest")
    public void getPromotionSuggest(List<StockRemainDto>  stockRemainDtos, String type) throws ParseException {

        String dateInStr = dateFormat.format(new Date());
        Date date = new Date();
        LocalDate currentDate = LocalDate.now();
        LocalDate dateTime1 = currentDate.minus(4, ChronoUnit.DAYS);
        LocalDate createDate = currentDate.plus(0, ChronoUnit.DAYS);
        LocalDate expireDate = currentDate.plus(2, ChronoUnit.DAYS);

        //For Check Duplicate
        LocalDate dateTimeDupFrom = currentDate.minus(14, ChronoUnit.DAYS);
        LocalDate dateTimeDupTo = currentDate.minus(7, ChronoUnit.DAYS);

        ZoneId zoneId = ZoneId.systemDefault();
        Date dateFrom = Date.from(dateTime1.atStartOfDay(zoneId).toInstant());
        Date createtDate = Date.from(createDate.atStartOfDay(zoneId).toInstant());
        Date expiryDate = Date.from(expireDate.atStartOfDay(zoneId).toInstant());
        Date dateTo = dateFormat.parse(dateInStr);
        Date dateTimeFrom = Date.from(dateTimeDupFrom.atStartOfDay(zoneId).toInstant());
        Date dateTimeTo = Date.from(dateTimeDupTo.atStartOfDay(zoneId).toInstant());

        int chkSize = 0;
        int availableQuantity = 0;
        int flowerLifeTime = 0;
        int profitFlower = 0;
        int availableQuantitySum;
        int calProfitCurrent = 0;
        int floristId = 0;
        Date lot = null;
        String floristName = null;
        String typeFlower = null;

        List<FlowerFormulaDetail> formulaDetails = new ArrayList<>();
        List<PromotionDetailCurrentDto> promotionDetailCurrentDtos = new ArrayList<>();
        List<StockRemainDto> stockRemainDtoList = new ArrayList<>();
        PromotionDetailCurrentDto promotionDetailCurrentDto = new PromotionDetailCurrentDto();

        for (StockRemainDto stockRemainDto: stockRemainDtos) {
            if (stockRemainDto.getRemainQuantity() > 0) {
                stockRemainDtoList.add(stockRemainDto);
            }
        }

        int loop = 0;
        other:
        for (StockRemainDto stock : stockRemainDtoList) {
            Flower flower = this.flowerRepository.findAllById(stock.getId());
            loop = loop + 1;
            if(flower.getMainCategory().equals("หลัก")){
                List<FlowerFormulaDetail> flowerList = this.flowerFormulaDetailRepository.findAllByFlowerIdAndQuantityLessThanEqualOrderByFlowerId(stock.getId(), stock.getRemainQuantity());
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
                continue other;
            }
        }

        availableQuantitySum = 9999;
        String quantityFlower = null;
        Integer stockAvailable = 0;
        recalDup:
        for (FlowerFormulaDetail flowerFormulaDetail : formulaDetails) {
            List<FlowerFormulaDetail> formulaDetails1 = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(flowerFormulaDetail.getFlowerFormula().getId());
            chkSize = 0;
            stockAvailable = 0;
            int i = 0;
            for (FlowerFormulaDetail formulaDetail : formulaDetails1) {
                i = i +1;
                chkFlower:
                for (StockRemainDto stockRemainDto : stockRemainDtoList) {
                    if (stockRemainDto.getId().equals(formulaDetail.getFlower().getFlowerId())) {
                        chkSize = chkSize + 1;
                        availableQuantity = stockRemainDto.getRemainQuantity() / formulaDetail.getQuantity();

                        Configurations configurations = this.configurationsRepository.getValueByName("MAX_QUANTITY_PROMOTION");
                        int maxQuantity = configurations.getValue();
                        if (availableQuantity > maxQuantity){
                            availableQuantity = maxQuantity;
                        }

                        availableQuantitySum = Math.min(availableQuantitySum, availableQuantity);
                        lot = stockRemainDto.getLot();
                        if(i == 1) {
                            floristId = stockRemainDto.getFloristId();
                            floristName = stockRemainDto.getFloristName();
                        }
                        long chkExp = date.getTime() - stockRemainDto.getLot().getTime();
                        int diffDays = (int) (chkExp / (24 * 60 * 60 * 1000));

                        //หา Life Time ของดอกไม้ที่ใกล้หมดอายุ และ ชนิดของดอกไม้
                        Flower chkFlower = this.flowerRepository.findAllById(stockRemainDto.getId());
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
                    List<Stock> stockList1 = this.stockRepository.findAllByFlowerIdAndLotGreaterThanEqualAndLotLessThanEqualAndFloristId(formulaDetail.getFlower().getFlowerId(),dateFrom, dateTo, floristId);
                    for(Stock stock: stockList1){
                        stockAvailable = stock.getQuantity();
                    }
                }
            }

            //Check Duplicate week
            List<Promotion> promotions = this.promotionRepository.findAllByDateGreaterThanAndDateLessThanEqual(dateTimeFrom, dateTimeTo);
            for(Promotion promotion: promotions){
                List<PromotionDetail> promotionDetailList = this.promotionDetailRepository.findAllByPromotionId(promotion.getId());
                for(PromotionDetail promotionDetail1: promotionDetailList){
                    if(flowerFormulaDetail.getFlowerFormula().getId().equals(promotionDetail1.getFlowerFormula().getId())){
                        continue recalDup;
                    }
                }
            }

            calProfitCurrent = flowerFormulaDetail.getFlowerFormula().getPrice() - ((flowerFormulaDetail.getFlowerFormula().getPrice() * profitFlower) / 100);
            Florist florist = this.floristRepository.findFloristById(floristId);
            if (availableQuantity > 0 && stockAvailable > 0) {
                FloristFee floristFee = this.floristFeeRepository.findFloristFeeByFloristIdAndSize(florist.getId(), flowerFormulaDetail.getFlowerFormula().getSize());
                int promotionPrice = (int) (calProfitCurrent + floristFee.getFee());
                if (promotionPrice % 100 != 0) {
                    promotionPrice = (promotionPrice - (promotionPrice % 100)) + 90;
                }else{
                    promotionPrice = promotionPrice + 90;
                }

                if (type.equals("getPromotion")) {
                    PromotionDetailLog promotionDetailLog = new PromotionDetailLog();
                    promotionDetailLog.setProfit((double) calProfitCurrent);
                    promotionDetailLog.setPrice((double) promotionPrice);
                    promotionDetailLog.setQuantity(availableQuantity);
                    promotionDetailLog.setStatus("active");
                    promotionDetailLog.setFlowerFormula(flowerFormulaDetail.getFlowerFormula());
                    promotionDetailLog.setExpiryDate(expiryDate);
                    promotionDetailLog.setPromotionType("remain");
                    promotionDetailLog.setFlorist(florist);
                    promotionDetailLog.setCreateDate(createtDate);
                    promotionDetailLog.setTotalProfit(availableQuantity * calProfitCurrent);
                    promotionDetailLog.setLotStock(lot);
                    this.promotionDetailLogRepository.saveAndFlush(promotionDetailLog);
                }else{
                    PromotionDetailLog promotionDetailLogs = this.promotionDetailLogRepository.findPromotionDetailLogsByStatusAndFlowerFormulaIdAndFloristId("active", flowerFormulaDetail.getFlowerFormula().getId(), florist.getId());
                    if(promotionDetailLogs != null){
                        promotionDetailLogs.setQuantity(availableQuantity);
                        promotionDetailLogs.setTotalProfit(availableQuantity * calProfitCurrent);
                        this.promotionDetailLogRepository.saveAndFlush(promotionDetailLogs);
                    }else{
                        PromotionDetailLog promotionDetailLog = new PromotionDetailLog();
                        promotionDetailLog.setProfit((double) calProfitCurrent);
                        promotionDetailLog.setPrice((double) promotionPrice);
                        promotionDetailLog.setQuantity(availableQuantity);
                        promotionDetailLog.setStatus("active");
                        promotionDetailLog.setFlowerFormula(flowerFormulaDetail.getFlowerFormula());
                        promotionDetailLog.setExpiryDate(expiryDate);
                        promotionDetailLog.setPromotionType("remain");
                        promotionDetailLog.setFlorist(florist);
                        promotionDetailLog.setCreateDate(createtDate);
                        promotionDetailLog.setTotalProfit(availableQuantity * calProfitCurrent);
                        promotionDetailLog.setLotStock(lot);
                        this.promotionDetailLogRepository.saveAndFlush(promotionDetailLog);
                    }

                }
            }
        }
    }

    @PostMapping("/addPromotion")
    public void addPromotionDetail(
            @RequestBody AddPromotionDto addPromotionDto
    ) {
        Promotion promotion = this.promotionRepository.findFirstByOrderByDateDesc();
        FlowerFormula flowerFormula = this.flowerFormulaRepository.findFlowerFormulaByName(addPromotionDto.getFormulaName());
        Florist florist = this.floristRepository.findFloristByName(addPromotionDto.getLocationName());
        PromotionDetailLog promotionDetailLog = this.promotionDetailLogRepository.findPromotionDetailLogsByStatusAndFlowerFormulaIdAndFloristId("active", flowerFormula.getId(), florist.getId());

        if (!dateFormat.format(promotion.getDate()).equals(dateFormat.format(new Date()))) {
            List<PromotionDetail> lastActivePromotions = this.promotionDetailRepository.findPromotionDetailsByStatus("active");

            promotion = new Promotion();
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
                promotionDetail.setTotalProfit((int) (s.getQuantity() * s.getProfit()));
                this.promotionDetailRepository.saveAndFlush(promotionDetail);

                s.setStatus("inactive");
                this.promotionDetailRepository.saveAndFlush(s);
            }
        }

        PromotionDetail newPromotionDetail = new PromotionDetail();
        newPromotionDetail.setProfit(addPromotionDto.getProfit());
        newPromotionDetail.setPrice(addPromotionDto.getPrice());
        newPromotionDetail.setQuantity(addPromotionDto.getQuantity());
        newPromotionDetail.setQuantitySold(0);
        newPromotionDetail.setStatus("active");
        newPromotionDetail.setPromotion(promotion);
        newPromotionDetail.setFlowerFormula(this.flowerFormulaRepository.findFlowerFormulaByName(addPromotionDto.getFormulaName()));
        newPromotionDetail.setFlorist(this.floristRepository.findFloristByName(addPromotionDto.getLocationName()));
        newPromotionDetail.setTotalProfit((int) (addPromotionDto.getQuantity() * addPromotionDto.getProfit()));
        newPromotionDetail.setExpiryDate(promotionDetailLog.getExpiryDate());
        this.promotionDetailRepository.saveAndFlush(newPromotionDetail);
    }

    @PostMapping("/recalculatePromotion")
    public List<PromotionDetail> getRecalculatePromotion(
            @RequestBody AddPromotionDto addPromotionDto
    ) throws ParseException {

        FlowerFormula flowerFormulas = this.flowerFormulaRepository.findFlowerFormulaByName(addPromotionDto.getFormulaName());

        Date date = new Date();
        int flowerLifeTime = 0;
        int profitFlower = 0;
        int numPromotion = 0;
        int availableQuantity = 0;
        int availableQuantitySum = 9999;
        int profitSum = 120;
        int profitFormula = 0;
        int available = 0;
        int availableTotal = 9999;
        int totalProfit = 0;
        int calProfitCurrent = 0;
        Date lot = null;
        String typeFlower = null;

        LocalDate currentDate = LocalDate.now();
        LocalDate dateTime = currentDate.minus(7, ChronoUnit.DAYS);
        Date dateReverse = dateFormat.parse(String.valueOf(dateTime));
        LocalDate createDate = currentDate.plus(0, ChronoUnit.DAYS);

        //For Check Duplicate
        LocalDate dateTime1 = currentDate.minus(14, ChronoUnit.DAYS);
        LocalDate dateTime2 = currentDate.minus(1, ChronoUnit.DAYS);
        LocalDate expireDate = currentDate.plus(2, ChronoUnit.DAYS);

        ZoneId zoneId = ZoneId.systemDefault();
        Date dateFrom = Date.from(dateTime1.atStartOfDay(zoneId).toInstant());
        Date dateTo = Date.from(dateTime2.atStartOfDay(zoneId).toInstant());
        Date expiryDate = Date.from(expireDate.atStartOfDay(zoneId).toInstant());
        Date createtDate = Date.from(createDate.atStartOfDay(zoneId).toInstant());

        //Get number of promotion
        List<Configurations> config = this.configurationsRepository.findAllByName("NUMBER_OF_PROMOTION");
        for (Configurations c: config){
            numPromotion = c.getValue();
        }

        List<FlowerFormulaDetail> formulaDetailList = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(flowerFormulas.getId());
        List<StockRemainDto> stockRemainDtos = new ArrayList<>();
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
                long chkExp = date.getTime() - stock.getLot().getTime();
                int diffDays = (int) (chkExp / (24 * 60 * 60 * 1000));

                //หา Life Time ของดอกไม้ที่ใกล้หมดอายุ และ ชนิดของดอกไม้
                Flower flower = this.flowerRepository.findAllById(stock.getFlower().getFlowerId());
                flowerLifeTime = flower.getLifeTime();
                typeFlower = flower.getFlowerType();

                int expired = flowerLifeTime - diffDays;
                if (typeFlower.equals("ดอกไม้สด")) {
                    if (expired > 0 && expired <= 3) {
                        newStocks.add(stock);
                        StockRemainDto stockRemainDto = new StockRemainDto();
                        stockRemainDto.setId(stock.getFlower().getFlowerId());
                        stockRemainDto.setFlowerName(stock.getFlower().getFlowerName());
                        stockRemainDto.setRemainQuantity(stock.getQuantity());
                        stockRemainDto.setFloristId(stock.getFlorist().getId());
                        stockRemainDto.setFloristName(stock.getFlorist().getName());
                        stockRemainDto.setLot(stock.getLot());
                        stockRemainDtos.add(stockRemainDto);
                    } else {
                        continue outer;
                    }
                }else{
                    PromotionProfit profit = this.promotionProfitRepository.findAllByFlowerType("ดอกไม้แห้ง");
                    int ageFlower = profit.getAge();
                    int expiredFlower = (flowerLifeTime * ageFlower / 100);
                    if (expired > 0 && expired <= expiredFlower) {
                        newStocks.add(stock);
                        StockRemainDto stockRemainDto = new StockRemainDto();
                        stockRemainDto.setId(stock.getFlower().getFlowerId());
                        stockRemainDto.setFlowerName(stock.getFlower().getFlowerName());
                        stockRemainDto.setRemainQuantity(stock.getQuantity());
                        stockRemainDto.setFloristId(stock.getFlorist().getId());
                        stockRemainDto.setFloristName(stock.getFlorist().getName());
                        stockRemainDto.setLot(stock.getLot());
                        stockRemainDtos.add(stockRemainDto);
                    }
                }
            }
        }

        //Check ดอกไม้จาก promotion ที่ถูกเลือกเพื่อไม่นำมา calculate ใหม่
        List<PromotionDetail> promotionDetailList = this.promotionDetailRepository.findPromotionDetailsByStatus("active");
        recalPromotion:
        for (PromotionDetail promotionDetail: promotionDetailList){
            List<FlowerFormulaDetail> flowerList = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetail.getFlowerFormula().getId());
            for (StockRemainDto stockRemainDto1 : stockRemainDtos){
                for (FlowerFormulaDetail formulaDetail: flowerList){
                    if (stockRemainDto1.getId().equals(formulaDetail.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stockRemainDto1.getFloristId())){
                        int remains = stockRemainDto1.getRemainQuantity() - (formulaDetail.getQuantity() * promotionDetail.getQuantity());
                        stockRemainDto1.setRemainQuantity(remains);
                    }
                }
            }
        }

        for (Florist florist1: florist) {
            List<FlowerFormulaDetail> listFormula = new ArrayList<>();
            for (StockRemainDto stockRemainDto1 : stockRemainDtos) {
                //List flower formula detail
                List<FlowerFormulaDetail> flowerList = this.flowerFormulaDetailRepository.findAllByFlowerIdAndQuantityLessThanEqualOrderByFlowerId(stockRemainDto1.getId(), stockRemainDto1.getRemainQuantity());

                //Check Duplicate in List formula
                recalDup:
                for (FlowerFormulaDetail q : flowerList) {
                    for (FlowerFormulaDetail lf: listFormula) {
                        if (q.getFlowerFormula().getId().equals(lf.getFlowerFormula().getId())) {
                            continue recalDup;
                        }
                    }

                    //Check Duplicate week
                    List<Promotion> promotions = this.promotionRepository.findAllByDateGreaterThanAndDateLessThanEqual(dateFrom, dateTo);
                    for(Promotion promotion: promotions){
                        List<PromotionDetail> promotionDetails1 = this.promotionDetailRepository.findAllByPromotionId(promotion.getId());
                        for(PromotionDetail promotionDetail: promotionDetails1){
                            if(q.getFlowerFormula().getId().equals(promotionDetail.getFlowerFormula().getId())){
                                continue recalDup;
                            }
                        }
                    }

                    //ไม่นำ formula ที่ถูกเลือกเป็นโปรโมชั่นปัจจุบันมาแสดงอีก
                    for (PromotionDetail promotionDetail: promotionDetailList){
                        if(promotionDetail.getFlowerFormula().getId().equals(q.getFlowerFormula().getId())){
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
                    chkFlower:
                    for (StockRemainDto stockRemainDto1 : stockRemainDtos) {
                        if (stockRemainDto1.getFloristId().equals(florist1.getId())){
                            if (ff.getFlower().getFlowerId().equals(stockRemainDto1.getId())) {
                                chkSize = chkSize + 1;
                                availableQuantity = stockRemainDto1.getRemainQuantity() / ff.getQuantity();
                                lot = stockRemainDto1.getLot();
                                long chkExp = date.getTime() - stockRemainDto1.getLot().getTime();
                                int diffDays = (int) (chkExp / (24 * 60 * 60 * 1000));

                                //หา Life Time ของดอกไม้ที่ใกล้หมดอายุ และ ชนิดของดอกไม้
                                Flower chkFlower = this.flowerRepository.findAllById(stockRemainDto1.getId());
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
                    profitFormula = ff.getFlowerFormula().getPrice() - ((ff.getFlowerFormula().getPrice() * profitSum) / 100);
                    totalProfit = profitFormula * availableQuantitySum;

                }
                if (formulas.size() == chkSize && availableQuantitySum > 0){

                    Configurations configurations = this.configurationsRepository.getValueByName("MAX_QUANTITY_PROMOTION");
                    int maxQuantity = configurations.getValue();
                    if (availableQuantitySum > maxQuantity){
                        availableQuantitySum = maxQuantity;
                    }

                    PromotionDetail promotionDetail = new PromotionDetail();
                    promotionDetail.setStatus("active");
                    promotionDetail.setProfit((double) profitFormula);
                    promotionDetail.setQuantity(availableQuantitySum);
                    promotionDetail.setExpiryDate(expiryDate);
                    promotionDetail.setFlorist(florist1);
                    promotionDetail.setFlowerFormula(list.getFlowerFormula());
                    promotionDetail.setPrice(profitFormula);
                    promotionDetail.setLotStock(lot);
                    promotionDetails.add(promotionDetail);
                }else {
                    continue recalculateFormula;
                }
            }
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
                        for(StockRemainDto stockRemainDto1 : stockRemainDtos){
                            if (stockRemainDto1.getId().equals(formulaDetail.getFlower().getFlowerId()) && promotionDetail2.getFlorist().getId().equals(stockRemainDto1.getFloristId())){
                                remain = stockRemainDto1.getRemainQuantity() - (formulaDetail.getQuantity() * promotionDetail2.getQuantity());
                                stockRemainDto1.setRemainQuantity(remain);
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
                    for(StockRemainDto stockRemainDto1 : stockRemainDtos){
                        if (stockRemainDto1.getId().equals(formulaDetail.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stockRemainDto1.getFloristId())){
                            loop = loop + 1;
                            remain = stockRemainDto1.getRemainQuantity() - (formulaDetail.getQuantity() * promotionDetail.getQuantity());
                            available = stockRemainDto1.getRemainQuantity() / formulaDetail.getQuantity();
                            if (remain < 0) {
                                flag1 = "N";
                            }else{
                                flag2 = "Y";
                            }
                        }
                    }

                    if ((flag1.equals("Y") && flag2.equals("Y")) && (formulaDetails.size() == loop)){
                        for(FlowerFormulaDetail formulaDetail1: formulaDetails) {
                            for (StockRemainDto stockRemainDto1 : stockRemainDtos) {
                                if (stockRemainDto1.getId().equals(formulaDetail1.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stockRemainDto1.getFloristId())) {
                                    remain = stockRemainDto1.getRemainQuantity() - (formulaDetail1.getQuantity() * promotionDetail.getQuantity());
                                    stockRemainDto1.setRemainQuantity(remain);
                                }
                            }
                        }
                    }

                    availableTotal = Math.min(availableTotal, available);
                    if (flag1.equals("N") || flag2.equals("N")) {
                        promotionDetail.setQuantity(availableTotal);
                        for(FlowerFormulaDetail formulaDetail2: formulaDetails) {
                            for (StockRemainDto stockRemainDto1 : stockRemainDtos) {
                                if (stockRemainDto1.getId().equals(formulaDetail2.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stockRemainDto1.getFloristId())) {
                                    remain = stockRemainDto1.getRemainQuantity() - (formulaDetail2.getQuantity() * promotionDetail.getQuantity());
                                    stockRemainDto1.setRemainQuantity(remain);
                                }
                            }
                        }
                    }
                }

                promotionDetailArrayList.add(promotionDetail);
                if (promotionDetail.getQuantity() < 1){
                    promotionDetailArrayList.remove(promotionDetail);
                }
                promotionDetails.remove(promotionDetail);

            }
        }

        List<PromotionDetailLog> promotionDetailLogs1 = this.promotionDetailLogRepository.findPromotionDetailLogsByStatus("active");
        for(PromotionDetailLog promotionDetailLog: promotionDetailLogs1){
            promotionDetailLog.setQuantity(0);
            promotionDetailLog.setTotalProfit(0);
            this.promotionDetailLogRepository.saveAndFlush(promotionDetailLog);
        }

        for(PromotionDetail promotionDetail1: promotionDetailArrayList){
            PromotionDetailLog promotionDetailLogs = this.promotionDetailLogRepository.findPromotionDetailLogsByStatusAndFlowerFormulaIdAndFloristId("active", promotionDetail1.getFlowerFormula().getId(), promotionDetail1.getFlorist().getId());
            if(promotionDetailLogs != null){
                promotionDetailLogs.setQuantity(promotionDetail1.getQuantity());
                promotionDetailLogs.setTotalProfit((int) (promotionDetail1.getQuantity() * promotionDetail1.getProfit()));
                promotionDetailLogs.setPromotionType("normal");
                this.promotionDetailLogRepository.saveAndFlush(promotionDetailLogs);
            }else{
                calProfitCurrent = promotionDetail1.getFlowerFormula().getPrice() - ((promotionDetail1.getFlowerFormula().getPrice() * profitFlower) / 100);
                FloristFee floristFee = this.floristFeeRepository.findFloristFeeByFloristIdAndSize(promotionDetail1.getFlorist().getId(), promotionDetail1.getFlowerFormula().getSize());
                int promotionPrice = (int) (calProfitCurrent + floristFee.getFee());
                if (promotionPrice % 100 != 0) {
                    promotionPrice = (promotionPrice - (promotionPrice % 100)) + 90;
                }else{
                    promotionPrice = promotionPrice + 90;
                }

                PromotionDetailLog promotionDetailLog = new PromotionDetailLog();
                promotionDetailLog.setProfit(promotionDetail1.getProfit());
                promotionDetailLog.setPrice((double) promotionPrice);
                promotionDetailLog.setQuantity(promotionDetail1.getQuantity());
                promotionDetailLog.setStatus("active");
                promotionDetailLog.setFlowerFormula(promotionDetail1.getFlowerFormula());
                promotionDetailLog.setExpiryDate(promotionDetail1.getExpiryDate());
                promotionDetailLog.setPromotionType("normal");
                promotionDetailLog.setFlorist(promotionDetail1.getFlorist());
                promotionDetailLog.setCreateDate(createtDate);
                promotionDetailLog.setTotalProfit((int) (promotionDetail1.getProfit() * promotionDetail1.getQuantity()));
                promotionDetailLog.setLotStock(promotionDetail1.getLotStock());
                this.promotionDetailLogRepository.saveAndFlush(promotionDetailLog);
            }
        }

        getPromotionSuggest(stockRemainDtos, "recalculate");
        return promotionDetailArrayList;
    }

    //Create by Nink
    @GetMapping(value="/getPromotionByDate")
    public List<PromotionDetail> getPromotionByDate(@RequestParam("startDate") String startD,@RequestParam("endDate") String endD) throws ParseException {
        Date date = new Date();
        Date startDate = null;
        Date endDate = null;
        List<PromotionDetail> promotionDetailList = new ArrayList<>();
        List<Promotion> promotionList = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");

        if (!startD.isEmpty()) {
            startDate = format.parse(startD);
        }
        if (!endD.isEmpty()) {
            endDate = format.parse(endD);
        }
        StringBuilder selectQueryStr = new StringBuilder("SELECT p FROM Promotion p WHERE 1 = 1 ");
        if (startDate == null && endDate == null) {
            promotionList = this.promotionRepository.findAll();
        } else {
            promotionList = this.promotionRepository.findAllByDateGreaterThanAndDateLessThanEqual(startDate, endDate);
        }

           for (Promotion promotionItem: promotionList)
            {

                List<PromotionDetail> promotionDetailResult = this.promotionDetailRepository.findAllByPromotionId(promotionItem.getId());
                promotionDetailList.addAll(promotionDetailResult);
            }

           return promotionDetailList;
//
//        List<PromotionDetailDto> promotionDetailDtos = new ArrayList<>();
//        int flowerLifeTime = 0;
//        int profitFlower = 0;
//        int numPromotion = 0;
//        int availableQuantity = 0;
//        int availableQuantitySum = 9999;
//        int profitSum = 120;
//        int profitFormula = 0;
//        int totalProfit = 0;
//        int available = 0;
//        int availableTotal = 9999;
//        String typeFlower = null;
    }
}
