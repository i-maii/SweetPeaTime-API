package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.dto.AddStockDTO;
import com.sweetpeatime.sweetpeatime.dto.ChangeStockDTO;
import com.sweetpeatime.sweetpeatime.dto.DeleteStockDTO;
import com.sweetpeatime.sweetpeatime.entities.*;
import com.sweetpeatime.sweetpeatime.repositories.*;
import com.sweetpeatime.sweetpeatime.wrapper.StockWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value="/stock")
public class StockController {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private FlowerRepository flowerRepository;

    @Autowired
    private FloristRepository floristRepository;

    @Autowired
    private FlowerPriceRepository flowerPriceRepository;

    @Autowired
    PromotionDetailLogRepository promotionDetailLogRepository;

    @Autowired
    FlowerFormulaRepository flowerFormulaRepository;

    @Autowired
    FlowerFormulaDetailRepository flowerFormulaDetailRepository;

    @Autowired
    ConfigurationsRepository configurationsRepository;

    @Autowired
    PromotionDetailRepository promotionDetailRepository;

    @Autowired
    PromotionProfitRepository promotionProfitRepository;

    @PersistenceContext
    EntityManager entityManager;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping("")
    public List<Stock> getAllStock() {
        StringBuilder selectQueryStr = new StringBuilder("SELECT " +
                "s.flowerId, " +
                "s.floristId, " +
                "SUM(s.quantity) as quantity " +
                "FROM Stock s " +
                "GROUP BY s.flowerId, s.floristId");
        Query selectQuery = entityManager.createNativeQuery(selectQueryStr.toString(), "stockMapping");

        List<StockWrapper> stockList = selectQuery.getResultList();

        List<Stock> stock = new ArrayList<>();
        for (StockWrapper stockWrapper: stockList) {
            Stock s = new Stock();
            s.setFlower(this.flowerRepository.findOneById(stockWrapper.getFlowerId()));
            s.setFlorist(this.floristRepository.findFloristById(stockWrapper.getFloristId()));
            s.setQuantity(stockWrapper.getQuantity());

            stock.add(s);
        }

        return stock;
    }

    @PostMapping("/deleteStock")
    public List<ChangeStockDTO> deleteStockQuantity(@RequestBody List<DeleteStockDTO> deleteStock) throws ParseException {
        List<ChangeStockDTO> changeStockDTOList = new ArrayList<>();

        int dlQuantity = 0;
        for (DeleteStockDTO ds: deleteStock) {
            List<Stock> stock = this.stockRepository.findAllByFlowerIdAndFloristIdOrderByLotAsc(ds.getFlowerId(), ds.getFloristId());
            Integer deleteQuantity = ds.getDeleteQuantity();
            for (Stock s: stock) {
                if (s.getQuantity() != 0) {
                    if (deleteQuantity > s.getQuantity()) {
                        s.setQuantity(0);
                        deleteQuantity = deleteQuantity - s.getQuantity();
                        this.stockRepository.saveAndFlush(s);
                    } else if (deleteQuantity < s.getQuantity()) {
                        dlQuantity = s.getQuantity() - deleteQuantity;
                        s.setQuantity(s.getQuantity() - deleteQuantity);
                        this.stockRepository.saveAndFlush(s);
                        break;
                    } else {
                        s.setQuantity(0);
                        this.stockRepository.saveAndFlush(s);
                        break;
                    }
                }
            }

            List<PromotionDetail> promotionDetails = this.promotionDetailRepository.findAllByStatus("active");
            for (PromotionDetail promotionDetail: promotionDetails){
                boolean recal = false;
                List<FlowerFormulaDetail> formulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetail.getFlowerFormula().getId());
                for(FlowerFormulaDetail formulaDetail: formulaDetails){
                    if(formulaDetail.getFlower().getFlowerId().equals(ds.getFlowerId())){
                        recal = true;
                        int available = formulaDetail.getQuantity() * promotionDetail.getQuantity();
                        if (dlQuantity >= available){
                            promotionDetail.setQuantity(promotionDetail.getQuantity());
                        }else{
                            int availablePromotion = dlQuantity / formulaDetail.getQuantity();
                            ChangeStockDTO changeStockDTO = new ChangeStockDTO();
                            if(availablePromotion > 0){ //มีการเปลี่ยนแปลงจำนวนช่อโปรโมชั่น ชื่อ จำนวนที่เปลี่ยน
                                changeStockDTO.setFormulaName(promotionDetail.getFlowerFormula().getName());
                                changeStockDTO.setBeforeQuantity(promotionDetail.getQuantity());
                                changeStockDTO.setRemainQuantity(availablePromotion);
                                changeStockDTO.setStatus("change");
                                changeStockDTOList.add(changeStockDTO);
                                promotionDetail.setQuantity(availablePromotion);
                            }else if(availablePromotion == 0){ //ไม่สามารถทำโปรโมชั่นนี้ได้แล้ว เนื่องจากจำนวนดอกไม้ไม่เพียงพอ
                                changeStockDTO.setFormulaName(promotionDetail.getFlowerFormula().getName());
                                changeStockDTO.setStatus("inactive");
                                changeStockDTOList.add(changeStockDTO);
                                promotionDetail.setQuantity(availablePromotion);
                                promotionDetail.setStatus("inactive");
                            }
                            this.promotionDetailRepository.saveAndFlush(promotionDetail);
                        }
                        break;
                    }
                }
                if(!recal){
                    getRecalculate();
                }
            }
        }

        return changeStockDTOList;
    }

    @PostMapping("/addStock")
    public void addStockQuantity(@RequestBody List<AddStockDTO> addStock) throws ParseException {
        for (AddStockDTO s: addStock) {
            FlowerPrice flowerPrice = this.flowerPriceRepository.findByFlowerId(s.getFlower().getFlowerId());
            flowerPrice.setPrice(s.getPrice());
            this.flowerPriceRepository.saveAndFlush(flowerPrice);

            Stock stock = new Stock();
            stock.setFlower(s.getFlower());
            stock.setQuantity(s.getQuantity());
            stock.setLot(dateFormat.parse(s.getLot()));
            stock.setUnit(s.getFlower().getUnit());
            stock.setFlorist(s.getFlorist());
            stock.setFlowerPrice(flowerPrice);
            this.stockRepository.saveAndFlush(stock);
        }
    }


    @PostMapping("/recalculate")
    public List<PromotionDetail> getRecalculate() throws ParseException {
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
        Date lot = null;
        String typeFlower = null;

        LocalDate currentDate = LocalDate.now();
        LocalDate dateTime = currentDate.minus(7, ChronoUnit.DAYS);
        Date dateReverse = dateFormat.parse(String.valueOf(dateTime));

        //For Check Duplicate
        LocalDate expireDate = currentDate.plus(2, ChronoUnit.DAYS);

        ZoneId zoneId = ZoneId.systemDefault();
        Date expiryDate = Date.from(expireDate.atStartOfDay(zoneId).toInstant());

        //Get number of promotion
        List<Configurations> config = this.configurationsRepository.findAllByName("NUMBER_OF_PROMOTION");
        for (Configurations c: config){
            numPromotion = c.getValue();
        }

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

        outer:
        for(PromotionDetail promotionDetail1: promotionDetailArrayList){
            PromotionDetailLog promotionDetailLogs = this.promotionDetailLogRepository.findPromotionDetailLogsByStatusAndFlowerFormulaIdAndFloristId("active", promotionDetail1.getFlowerFormula().getId(), promotionDetail1.getFlorist().getId());
            if(promotionDetailLogs != null){
                promotionDetailLogs.setQuantity(promotionDetail1.getQuantity());
                promotionDetailLogs.setTotalProfit((int) (promotionDetail1.getQuantity() * promotionDetail1.getProfit()));
                this.promotionDetailLogRepository.saveAndFlush(promotionDetailLogs);
            }else{
                continue outer;
            }

        }
        return promotionDetailArrayList;
    }
}
