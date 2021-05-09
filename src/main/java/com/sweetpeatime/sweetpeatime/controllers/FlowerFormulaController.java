package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.*;
import com.sweetpeatime.sweetpeatime.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value="/flowerFormula")
@CrossOrigin(origins = "http://localhost:4200")
public class FlowerFormulaController {

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
    PromotionProfitRepository promotionProfitRepository;

    @Autowired
    FloristFeeRepository floristFeeRepository;

    @PersistenceContext
    EntityManager entityManager;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public PromotionDetail findNearlyDate(List<PromotionDetail> promotionDetails){
        PromotionDetail newPromotionDetail = new PromotionDetail();
        int temp = 0;
        //select nearly date
        for (int j = 0; j < promotionDetails.size() - 1; j++) {
            if (promotionDetails.get(temp).getExpiryDate().before(promotionDetails.get(j + 1).getExpiryDate())) {
                newPromotionDetail = promotionDetails.get(temp);
            } else {
                newPromotionDetail = promotionDetails.get(j + 1);
                temp = j + 1;
            }
        }
        return newPromotionDetail;
    }
    @GetMapping(value="/getAll")
    public List<FlowerFormula> getAllFlowerFormula() {
        return this.flowerFormulaRepository.findAll();
    }

    public Integer calculatePrice (Integer price, Integer floristId, String size) {
        Integer flowerPrices = 0;
        FloristFee floristFee;
        floristFee = this.floristFeeRepository.findAllByFloristIdAndSize(floristId, size);
        flowerPrices = price + floristFee.getFee();
        flowerPrices = (flowerPrices - (flowerPrices % 100)) + 90;
        return flowerPrices;
    }
    @PostMapping(value="/priceOfSalesOrder")
    public PriceOfSalesOrder getQuantityAvailable(@RequestBody List<FlowerPriceDto> flowerPriceDtos) throws ParseException {
        PriceOfSalesOrder priceOfSalesOrder = new PriceOfSalesOrder();
        Integer flowerPrices = 0;
        int floristId = flowerPriceDtos.get(0).getFloristId();
        FloristFee floristFee;
        for (FlowerPriceDto flowerPriceDto : flowerPriceDtos) {
            Date date = this.simpleDateFormat.parse(flowerPriceDto.getReceiveDate());
            List<PromotionDetail> promotionDetails = this.promotionDetailRepository.findOneByFlowerFormulaIdAndStatusAndExpiryDateAndFlorist(flowerPriceDto.getFormulaId(), date, floristId);

            FlowerFormula flowerFormula = this.flowerFormulaRepository.findFlowerFormulaById(flowerPriceDto.getFormulaId());

            int promotionSize = promotionDetails.size();
            int orderTotal = flowerPriceDto.getTotalOrder();
            for (int i = 0; i < orderTotal; i++) {
                if (promotionSize != 0) {
                    if (promotionSize > 1) {
                        PromotionDetail newPromotionDetail = new PromotionDetail();

                        newPromotionDetail = findNearlyDate(promotionDetails);

                        if(newPromotionDetail.getQuantity() == 0 && promotionSize == 1) {
                            flowerPrices += calculatePrice(flowerFormula.getPrice(), floristId, flowerFormula.getSize());
                        }else if (newPromotionDetail.getQuantity() - 1 == 0) {
                            flowerPrices += newPromotionDetail.getPrice();
                            promotionDetails.remove(newPromotionDetail);
                            promotionSize--;
                        } else {
                            if (newPromotionDetail.getQuantity() >= flowerPriceDto.getTotalOrder()) {
                                flowerPrices = newPromotionDetail.getPrice() * flowerPriceDto.getTotalOrder();
                                break;
                            }else {
                                orderTotal = orderTotal - newPromotionDetail.getQuantity();
                                flowerPrices += newPromotionDetail.getPrice() * newPromotionDetail.getQuantity();
                                promotionDetails.remove(newPromotionDetail);
                            }
                        }

                    } else if (promotionDetails.size() == 1 && promotionDetails.get(0).getQuantity() != 0) {
                        //find nearly date
                        flowerPrices += promotionDetails.get(0).getPrice();
                        promotionDetails.get(0).setQuantity(promotionDetails.get(0).getQuantity() -1);

                        if (promotionDetails.get(0).getQuantity() == 0) {
                            promotionSize--;
                        }
                    } else {
                        flowerPrices += calculatePrice(flowerFormula.getPrice(), floristId, flowerFormula.getSize());
                    }
                } else {
                    flowerPrices += calculatePrice(flowerFormula.getPrice(), floristId, flowerFormula.getSize());
                }
            }
        }
        priceOfSalesOrder.setFlowerPrice((double) flowerPrices);

        return priceOfSalesOrder;
    }

    @GetMapping(value="/search")
    public List<FlowerFormula> search(
            @RequestParam(value = "flowerCat", required = false) String flowerCat,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "pattern", required = false) String pattern,
            @RequestParam(value = "occasion", required = false) String occasion,
            @RequestParam(value = "priceFrom", required = false) Integer priceFrom,
            @RequestParam(value = "priceTo", required = false) Integer priceTo,
            @RequestParam(value = "quantityAvailable", required = false) String quantityAvailable,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "color", required = false) String color,
            @RequestParam(value = "florist", required = false) String florist
    ){
        StringBuilder selectQueryStr = new StringBuilder("SELECT f FROM FlowerFormula f WHERE 1 = 1 ");

        if (name != null)
            //selectQueryStr.append("AND f.name like :name ");
            selectQueryStr.append("AND f.name like CONCAT('%', :name, '%') ");
        if(flowerCat != null)
            selectQueryStr.append("AND f.formulaType = :flowerCat ");
        if(florist != null)
        {
            if(pattern !=null) {
                if (florist.equals("หนึ่ง") && pattern.equals("เกาหลี")) {
                    return null;
                }
            }
            else
            {
                if (florist.equals("หนึ่ง")) {
                    pattern = "ทั่วไป";
                }
            }
        }
        if(color != null)
            selectQueryStr.append("AND f.color like CONCAT('%', :color, '%') ");
        if(pattern != null)
            selectQueryStr.append("AND f.pattern = :pattern ");

        if(occasion != null)
            selectQueryStr.append("AND f.occasion = :occasion ");
        if(priceFrom != null && priceTo != null)
            selectQueryStr.append("AND f.price BETWEEN :priceFrom AND :priceTo ");
        else
            if(priceFrom != null && priceTo == null)
            selectQueryStr.append("AND f.price >= :priceFrom ");
            else if(priceFrom == null && priceTo != null)
            selectQueryStr.append("AND f.price <= :priceTo ");

        if(quantityAvailable != null)
            selectQueryStr.append("AND f.quantityAvailable >= :quantityAvailable ");

        if(size != null)
            selectQueryStr.append("AND f.size = :size ");

        selectQueryStr.append("ORDER BY f.size ASC, f.price DESC ");
        Query selectQuery = entityManager.createQuery(selectQueryStr.toString(), FlowerFormula.class);


        if (name != null)
            selectQuery.setParameter("name", name);
        if (pattern != null)
            selectQuery.setParameter("pattern", pattern);
        if (flowerCat != null)
            selectQuery.setParameter("flowerCat", flowerCat);
        if (color != null)
            selectQuery.setParameter("color", color);
        if (occasion != null)
            selectQuery.setParameter("occasion", occasion);

        if (priceFrom != null)
            selectQuery.setParameter("priceFrom", priceFrom);
        if (priceTo != null)
            selectQuery.setParameter("priceTo", priceTo);

        if (quantityAvailable != null)
            selectQuery.setParameter("quantityAvailable", quantityAvailable);

        if (size != null)
            selectQuery.setParameter("size", size);

        List<FlowerFormula> flowerFormulas = selectQuery.getResultList();

        return flowerFormulas;
    }

    @PostMapping(value="updateFlowerFormulaPrice")
    public void updateFlowerFormulaPrice() {
        List<FlowerFormula> flowerFormulas = this.flowerFormulaRepository.findAll();
        Integer flowerFormulaPrice = 0;
        Integer percentProfit = this.configurationRepository.findConfigurationsById(3).getValue();

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

            if (flowerFormulaPrice % 100 != 0) {
                flowerFormulaPrice = (flowerFormulaPrice - (flowerFormulaPrice % 100)) + 90;
            }
            flowerFormula.setPrice(flowerFormulaPrice);
            this.flowerFormulaRepository.saveAndFlush(flowerFormula);
            flowerFormulaPrice = 0;
        }
    }

    @GetMapping(value = "/getflowerFormula")
    public List<FlowerFormula> setFlowerFormula(@RequestParam("floristId") Integer floristId) {
        List<FlowerFormula> flowerFormulasPromotion = new ArrayList<>();
        List<FlowerFormula> flowerFormulas = new ArrayList<>();
        flowerFormulasPromotion = this.flowerFormulaRepository.findAllByFlowerFormulaId(floristId);
        flowerFormulas = this.flowerFormulaRepository.findAll();
        for (FlowerFormula flowerFormula: flowerFormulasPromotion){
            flowerFormulas.remove(flowerFormula);
        }
        int cnt = 0;
        for (FlowerFormula flowerFormula: flowerFormulasPromotion) {
            for (FlowerFormula formula : flowerFormulas) {
                if (formula.getId().equals(flowerFormula.getId())) {
                    cnt++;
                }
            }
            if (cnt == 0) {
                flowerFormula.setName(flowerFormula.getName() + " โปรโมชั่น");
                flowerFormulas.add(0, flowerFormula);
            }
        }

        return flowerFormulas;
    }
}
