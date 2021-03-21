package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.*;
import com.sweetpeatime.sweetpeatime.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.stream.Collectors;

import static java.lang.Math.decrementExact;
import static java.lang.Math.floor;

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

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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

    public PromotionDetailController(PromotionDetailRepository promotionDetailRepository) {
        this.promotionDetailRepository = promotionDetailRepository;
    }

    @GetMapping(value="/getPromotion")
    public List<PromotionDetailDto> getPromotion() throws ParseException {
        String dateInStr = dateFormat.format(new Date());
        Date date = dateFormat.parse(dateInStr);
        List<PromotionDetailDto> promotionDetailDtos = new ArrayList<>();
        List<StockDto> flowerRemains = new ArrayList<>();
        int flowerLifeTime = 0;
        int profitFlower = 0;
        int calProfit = 0;
        int numPromotion = 0;
        String typeFlower = null;

        LocalDate currentDate = LocalDate.now();
        LocalDate dateTime = currentDate.minus(7, ChronoUnit.DAYS);
        Date dateReverse = dateFormat.parse(String.valueOf(dateTime));

        LocalDate dateTime1 = currentDate.minus(14, ChronoUnit.DAYS);
        LocalDate dateTime2 = currentDate.minus(1, ChronoUnit.DAYS);
        Date dateFrom = dateFormat.parse(String.valueOf(dateTime1));
        Date dateTo = dateFormat.parse(String.valueOf(dateTime2));

        System.out.println("dateReverse : " + dateReverse);
        System.out.println("dateFrom : " + dateFrom);
        System.out.println("dateTo : " + dateTo);
        List<Promotion> listPromotion = this.promotionRepository.findAllByDateGreaterThanAndDateLessThanEqual(dateFrom, dateTo);

        //Get number of promotion
        List<Configurations> config = this.configurationsRepository.findAllByName("NUMBER_OF_PROMOTION");
        for (Configurations c: config){
            numPromotion = c.getValue();
        }

        List<Florist> florist = this.floristRepository.findAll();
        for (Florist flo: florist) {
            System.out.println("------------------------------");
            System.out.println("florist : " + flo.getName());
            List<Stock> stocks = this.stockRepository.findAllByFloristIdOrderByQuantityDesc(flo.getId());
            List<Stock> newStocks = new ArrayList<>();
            List<Integer> newFlower = new ArrayList<>();
            outer:
            for (Stock stock : stocks) {
                String rr = stock.getLot().toString();
                Date d2 = dateFormat.parse(rr);
                long chkExp = date.getTime() - d2.getTime();
                int diffDays = (int) (chkExp / (24 * 60 * 60 * 1000));
                //หา Life Time ของดอกไม้ที่ใกล้หมดอายุ และ ชนิดของดอกไม้
                List<Flower> flowers = this.flowerRepository.findAllById(stock.getFlower().getFlowerId());
                for (Flower flower : flowers) {
                    flowerLifeTime = flower.getLifeTime();
                    typeFlower = flower.getFlowerType();
                }

                int expired = flowerLifeTime - diffDays;
                List<PromotionProfit> promotionProfits = this.promotionProfitRepository.findAllByAgeAndFlowerType(expired, typeFlower);
                for (PromotionProfit profit : promotionProfits) {
                    if (profit != null) {
                        profitFlower = profit.getProfit();
                    } else {
                        break;
                    }
                }

                if (expired > 0 && expired <= 3) {
                    newStocks.add(stock);
                    newFlower.add(stock.getFlower().getFlowerId());
                } else {
                    continue outer;
                }
            }

            List<FlowerFormulaDetail> formulaDup = new ArrayList<>();
            for (Stock qStock : newStocks) {
                String chk1 = "Y";
                String chk2 = "Y";
                int unit = 0;
                //int unit2 = 0;
                int remain = 0;
                int loop = 0;
                List<StockDto> stockChk = new ArrayList<>();
                StockDto stockRecalculate = new StockDto();
                //List flower formula detail
                List<FlowerFormulaDetail> flowerList = this.flowerFormulaDetailRepository.findAllByFlowerIdAndQuantityLessThanEqualOrderByQuantityDesc(qStock.getFlower().getFlowerId(), qStock.getQuantity());
                loop = 1;
                recalDup:
                for (FlowerFormulaDetail q : flowerList) {
                    List<FlowerFormulaDetail> formulas = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(q.getFlowerFormula().getId());
                    System.out.println("formula id : " + q.getFlowerFormula().getId() + "formula name : " + q.getFlowerFormula().getName());
                    //Check Duplicate
                    for (Promotion d : listPromotion) {
                        List<PromotionDetail> chkDupPromotion = this.promotionDetailRepository.findAllByPromotionId(d.getId());
                        for (PromotionDetail dd : chkDupPromotion) {
                            if (q.getFlowerFormula().getId().equals(dd.getFlowerFormula().getId())) {
                                //System.out.println("getName : " + dd.getFlowerFormula().getName());
                                continue recalDup;
                            }
                        }
                    }

                    //Check Duplicate in day
                    for (FlowerFormulaDetail formulasChk : formulaDup) {
                            if (q.getFlowerFormula().getId().equals(formulasChk.getFlowerFormula().getId())) {
                                continue recalDup;
                        }
                    }

//                System.out.println("formula id : " + q.getFlowerFormula().getId());
//                System.out.println("formula name : " + q.getFlowerFormula().getName());
//                System.out.println("flower size : " + q.getFlowerFormula().getSize());
                    for (FlowerFormulaDetail f : formulas) {
                        List<Stock> stockCheck = this.stockRepository.findAllByFlowerIdAndFloristIdAndQuantityGreaterThanEqualAndLotGreaterThanEqual(f.getFlower().getFlowerId(), flo.getId(), f.getQuantity(), dateReverse);
                        chk1 = "Y";
                        chk2 = "Y";

                        if (stockCheck.size() == 0) {
                            chk1 = "N";
                            break;
                        } else {
                            for (Stock ss : stockCheck) {
                                chk2 = "Y";
                                if (ss.getFlower().getMainCategory().equals("หลัก")) {
                                    if (loop == 1) {
                                        unit = ss.getQuantity() / f.getQuantity();
                                        remain = ss.getQuantity() - (f.getQuantity() * unit);
                                        stockRecalculate.setId(ss.getFlower().getFlowerId());
                                        stockRecalculate.setFlowerName(ss.getFlower().getFlowerName());
                                        stockRecalculate.setRemainQuantity(remain);
                                        stockChk.add(stockRecalculate);

                                    } else {
                                        for (StockDto aa : stockChk) {
                                            if (ss.getFlower().getFlowerId().equals(aa.getId())) {
                                                unit = aa.getRemainQuantity() / f.getQuantity();
                                                remain = aa.getRemainQuantity() - (f.getQuantity() * unit);
                                            }
                                        }
                                    }
                                } else {
                                    if (loop == 1) {
                                        remain = ss.getQuantity() - (f.getQuantity() * unit);
                                    } else {
                                        for (StockDto aa : stockChk) {
                                            if (ss.getFlower().getFlowerId().equals(aa.getId())) {
                                                remain = aa.getRemainQuantity() - (f.getQuantity() * unit);

                                            }
                                        }
                                    }
                                    System.out.println("FlowerName : " + ss.getFlower().getFlowerName() + ", ดอกไม้คงเหลือ remain : " + remain);
                                }
                            }
                        }
                    }

                    /*StockDto flowerRemain = new StockDto();
                    flowerRemain.setId();
                    flowerRemain.getRemainQuantity();
                    flowerRemain.getFlowerName();*/

                    loop = loop + 1;
                    if (chk1.equals("Y") && chk2.equals("Y")) {
                        System.out.println("Pass formula name : " + q.getFlowerFormula().getName());
                        calProfit = (int) ((q.getFlowerFormula().getPrice()) - (int) ((q.getFlowerFormula().getPrice() * profitFlower) / 100));
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

                        formulaDup.add(q);

                    } else {
                        System.out.println("Not Pass flower name : " + q.getFlowerFormula().getName());
                    }
                    System.out.println("-------------------------");

                }
            }
        }
        List<PromotionDetailDto> sortedList = promotionDetailDtos.stream()
                .sorted(Comparator.comparingInt(PromotionDetailDto::getTotalProfit).reversed())
                .collect(Collectors.toList());
        //getPromotionSuggest(sortedList);
        return sortedList;
    }

    @GetMapping(value="/getPromotionSuggest")
    //public List<PromotionDetailCurrentDto> getPromotionSuggest(List<PromotionDetailDto> sortedList) throws ParseException {
    public List<PromotionDetailCurrentDto> getPromotionSuggest() throws ParseException {
        String dateInStrs = dateFormat.format(new Date());
        Date date = dateFormat.parse(dateInStrs);
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
            Date d2 = dateFormat.parse(rr);
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

                        String quantityDto = f.getFlower().getFlowerName() + (f.getQuantity() * unit) + f.getFlower().getUnit();
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

    @PostMapping("/addPromotion")
    public void addPromotionDetail(
            @RequestBody AddPromotionDto addPromotionDto
    ) {
        Promotion promotion = this.promotionRepository.findFirstByOrderByDateDesc();

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
                this.promotionDetailRepository.saveAndFlush(promotionDetail);

                s.setStatus("inactive");
                this.promotionDetailRepository.saveAndFlush(s);
            }
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
    }
}
