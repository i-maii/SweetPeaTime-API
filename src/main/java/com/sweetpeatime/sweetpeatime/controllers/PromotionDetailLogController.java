package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.*;
import com.sweetpeatime.sweetpeatime.repositories.ConfigurationsRepository;
import com.sweetpeatime.sweetpeatime.repositories.FlowerFormulaDetailRepository;
import com.sweetpeatime.sweetpeatime.repositories.PromotionDetailLogRepository;
import com.sweetpeatime.sweetpeatime.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value="/promotionDetailLog")
public class PromotionDetailLogController {

    @Autowired
    ConfigurationsRepository configurationsRepository;

    @Autowired
    FlowerFormulaDetailRepository flowerFormulaDetailRepository;

    @Autowired
    StockRepository stockRepository;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    PromotionDetailLogRepository promotionDetailLogRepository;

    @GetMapping(value = "/normal")
    public List<PromotionDetailLog> getNormalPromotion() throws ParseException {
        String dateInStr = dateFormat.format(new Date());
        Date date = dateFormat.parse(dateInStr);

        return this.promotionDetailLogRepository.findPromotionDetailLogsByPromotionDateAndPromotionTypeAndStatusOrderBySequenceAsc(date, "normal", "active");
    }

    @GetMapping(value = "/current")
    public List<PromotionDetailLog> getCurrentPromotion() throws ParseException {
        String dateInStr = dateFormat.format(new Date());
        Date date = dateFormat.parse(dateInStr);

        return this.promotionDetailLogRepository.findPromotionDetailLogsByPromotionDateAndPromotionTypeAndStatusOrderBySequenceAsc(date, "current", "active");
    }

    @GetMapping(value = "/suggest")
    public List<PromotionDetailLog> getSuggestPromotion() {
        return this.promotionDetailLogRepository.findPromotionDetailLogsByStatusOrderBySequenceAsc("active");
    }

    @GetMapping(value = "/promotionDetailLog")
    public List<PromotionDetailLog> getPromotionDetailLog() throws ParseException {

        int listPromotion;
        Configurations configurations = this.configurationsRepository.getValueByName("LIST_OF_PROMOTION");
        listPromotion = configurations.getValue();

        ArrayList<PromotionDetailLog> compareByTotalProfit = (ArrayList<PromotionDetailLog>) this.promotionDetailLogRepository.findPromotionDetailLogsByStatusAndQuantityGreaterThanAndPromotionTypeOrderByLotStock("active", 0, "normal");
        ArrayList<PromotionDetailLog> promotionDetailLogs = new ArrayList<>();
        Comparator<PromotionDetailLog> compareByLot = Comparator
                .comparing(PromotionDetailLog::getLotStock).reversed()
                .thenComparing(PromotionDetailLog::getTotalProfit).reversed();

        List<PromotionDetailLog> sortedPromotion = compareByTotalProfit.stream()
                .sorted(compareByLot)
                .collect(Collectors.toList());

        int i = 0;
        for(PromotionDetailLog promotionDetailLog: sortedPromotion){
            i = i + 1;
            System.out.println();
            if (i > listPromotion){
                break;
            }
            promotionDetailLogs.add(promotionDetailLog);
        }

        return promotionDetailLogs;
    }

    @GetMapping(value = "/promotionDetailLogRemainQuantity")
    public List<PromotionDetailCurrentDto> getPromotionDetailLogRemainQuantity() throws ParseException {

        List<PromotionDetailLog> promotionDetailLog = this.promotionDetailLogRepository.findPromotionDetailLogsByStatusAndQuantityGreaterThanAndPromotionType("active", 0, "remain");
        List<PromotionDetailCurrentDto> promotionDetailCurrentDtos = new ArrayList<>();
        String dateInStr = dateFormat.format(new Date());
        LocalDate currentDate = LocalDate.now();
        LocalDate dateTime1 = currentDate.minus(4, ChronoUnit.DAYS);
        ZoneId zoneId = ZoneId.systemDefault();
        Date dateFrom = Date.from(dateTime1.atStartOfDay(zoneId).toInstant());
        Date dateTo = dateFormat.parse(dateInStr);
        String quantityFlower = null;

        for(PromotionDetailLog promotionDetailLog1: promotionDetailLog){
            int stockAvailable = 0;
            List<FlowerFormulaDetail> formulaDetail = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetailLog1.getFlowerFormula().getId());
            for(FlowerFormulaDetail flowerFormulaDetail: formulaDetail){
                if (!flowerFormulaDetail.getFlower().getMainCategory().equals("หลัก")){

                    List<Stock> stocks = this.stockRepository.findAllByFlowerIdAndLotGreaterThanEqualAndLotLessThanEqualAndFloristId(flowerFormulaDetail.getFlower().getFlowerId(),dateFrom, dateTo, promotionDetailLog1.getFlorist().getId());
                    for(Stock stock: stocks){
                        stockAvailable = stockAvailable + stock.getQuantity();
                    }

                    int quantity = flowerFormulaDetail.getQuantity() * promotionDetailLog1.getQuantity();
                    quantityFlower = flowerFormulaDetail.getFlower().getFlowerName() + " " + quantity + " " + flowerFormulaDetail.getFlower().getUnit();
                }
            }

            if(stockAvailable > 0) {
                PromotionDetailCurrentDto promotionDetailCurrentDto = new PromotionDetailCurrentDto();
                promotionDetailCurrentDto.setId(promotionDetailLog1.getId());
                promotionDetailCurrentDto.setFormulaName(promotionDetailLog1.getFlowerFormula().getName());
                promotionDetailCurrentDto.setSize(promotionDetailLog1.getFlowerFormula().getSize());
                promotionDetailCurrentDto.setQuantity(promotionDetailLog1.getQuantity());
                promotionDetailCurrentDto.setProfit(promotionDetailLog1.getProfit());
                promotionDetailCurrentDto.setTotalProfit(promotionDetailLog1.getTotalProfit());
                promotionDetailCurrentDto.setPrice(promotionDetailLog1.getPrice());
                promotionDetailCurrentDto.setLocationName(promotionDetailLog1.getFlorist().getLocationName());
                promotionDetailCurrentDto.setImage(promotionDetailLog1.getFlowerFormula().getImagePath());
                promotionDetailCurrentDto.setQuantityFlower(quantityFlower);
                promotionDetailCurrentDto.setStock(stockAvailable);
                promotionDetailCurrentDtos.add(promotionDetailCurrentDto);
            }
        }

        List<PromotionDetailCurrentDto> sortPromotion = promotionDetailCurrentDtos.stream()
                .sorted(Comparator.comparing(PromotionDetailCurrentDto::getTotalProfit).reversed())
                .collect(Collectors.toList());

        return sortPromotion;
    }
}
