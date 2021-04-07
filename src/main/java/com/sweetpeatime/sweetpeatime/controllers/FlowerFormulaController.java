package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.*;
import com.sweetpeatime.sweetpeatime.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@Controller
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

    @PersistenceContext
    EntityManager entityManager;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping(value="/getAll")
    public List<FlowerFormula> getAllFlowerFormula() {
        return this.flowerFormulaRepository.findAll();
    }

    @GetMapping(value="/priceOfSalesOrder")
    public PriceOfSalesOrder getQuantityAvailable(
            @RequestParam("formulaId") Integer formulaId,
            @RequestParam("floristId") Integer floristId,
            @RequestParam("totalOrder") Integer totalOrder,
            @RequestParam("flowerPrice") Integer flowerPrice,
            @RequestParam("receiveDateTime") String receiveDateTime) throws ParseException {
        Date date = this.simpleDateFormat.parse(receiveDateTime);
        PriceOfSalesOrder priceOfSalesOrder = new PriceOfSalesOrder();
        List<PromotionDetail> promotionDetails = this.promotionDetailRepository.findOneByFlowerFormulaIdAndStatusAndExpiryDate(formulaId, date);
        Integer flowerPrices = 0;
        Integer percentProfit = 0;

        FlowerFormula flowerFormula = this.flowerFormulaRepository.findFlowerFormulaById(formulaId);
        List<PromotionProfit> promotionProfits = this.promotionProfitRepository.findAll();

        if (promotionDetails != null) {
            int countTotal = 0;
            for (PromotionDetail promotionDetail : promotionDetails) {
                countTotal += promotionDetail.getQuantity();
            }
            if (countTotal >= totalOrder) {
                for (int i = 0; i < totalOrder; i++) {
                    if (promotionDetails.size() > 1) {
                        PromotionDetail promotionDetail = new PromotionDetail();
                        int temp = 0;


                        //select nearly date
                        for (int j = 0; j < promotionDetails.size() - 1; j++) {
                            if (promotionDetails.get(temp).getExpiryDate().before(promotionDetails.get(j + 1).getExpiryDate()) && promotionDetail.getQuantity() != 0) {
                                promotionDetail = promotionDetails.get(temp);
                            } else {
                                promotionDetail = promotionDetails.get(j + 1);
                                temp = j + 1;
                            }
                        }

                        //set profit for promotion
                        if (promotionDetail.getQuantity() != 0) {
                            long diffInMillies = Math.abs(promotionDetail.getExpiryDate().getTime() - date.getTime());
                            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                            if (flowerFormula.getFormulaType().equals("ดอกไม้แห้ง")) {
                                percentProfit = promotionProfits.get(4).getProfit();
                            } else {
                                for (PromotionProfit promotionProfit : promotionProfits) {
                                    if (diff == promotionProfit.getAge()) {
                                        percentProfit = promotionProfit.getProfit();
                                    }
                                }
                            }

                            //delete from stock
                            List<FlowerFormulaDetail> flowerFormulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(formulaId);
                            Integer flowerFormulaPrice = 0;
                            for (FlowerFormulaDetail flowerFormulaDetail : flowerFormulaDetails) {
                                FlowerPrice fp = this.flowerPriceRepository.findByFlowerId(flowerFormulaDetail.getFlower().getFlowerId());
                                int unitQuantityUse = 1;
                                int quantity = flowerFormulaDetail.getQuantity();
                                while (quantity > fp.getQuantitySaleUnit()) {
                                    unitQuantityUse++;
                                    quantity = quantity - fp.getQuantitySaleUnit();
                                }
                                flowerFormulaPrice += fp.getPrice() * unitQuantityUse;
                            }
                            flowerFormulaPrice += (flowerFormulaPrice * percentProfit) / 100;

                            //calculate flower price
                            if (flowerFormulaPrice % 100 != 0) {
                                flowerFormulaPrice = (flowerFormulaPrice - (flowerFormulaPrice % 100)) + 90;
                            }
                            flowerPrices = flowerFormulaPrice;
                        }
                    } else  if (promotionDetails.size() == 1) {

                        //find nearly date
                        for (PromotionDetail promotionDetail : promotionDetails) {
                            if (promotionDetail.getQuantity() != 0) {
                                long diffInMillies = Math.abs(promotionDetail.getExpiryDate().getTime() - date.getTime());
                                long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                                for (PromotionProfit promotionProfit : promotionProfits) {
                                    if (diff == promotionProfit.getAge()) {
                                        percentProfit = promotionProfit.getProfit();
                                    }
                                }

                                //delete from stock
                                List<FlowerFormulaDetail> flowerFormulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(formulaId);
                                Integer flowerFormulaPrice = 0;
                                for (FlowerFormulaDetail flowerFormulaDetail : flowerFormulaDetails) {
                                    FlowerPrice fp = this.flowerPriceRepository.findByFlowerId(flowerFormulaDetail.getFlower().getFlowerId());
                                    int unitQuantityUse = 1;
                                    int quantity = flowerFormulaDetail.getQuantity();
                                    while (quantity > fp.getQuantitySaleUnit()) {
                                        unitQuantityUse++;
                                        quantity = quantity - fp.getQuantitySaleUnit();
                                    }
                                    flowerFormulaPrice += fp.getPrice() * unitQuantityUse;
                                }
                                flowerFormulaPrice += (flowerFormulaPrice * percentProfit) / 100;

                                //calculat flower price
                                if (flowerFormulaPrice % 100 != 0) {
                                    flowerFormulaPrice = (flowerFormulaPrice - (flowerFormulaPrice % 100)) + 90;
                                }
                                flowerPrices = flowerFormulaPrice;
                            } else {
                                flowerPrices = flowerFormula.getPrice();
                            }
                        }
                    }
                }
            }

        } else {
            flowerFormula = this.flowerFormulaRepository.findFlowerFormulaById(formulaId);
            flowerPrices = flowerFormula.getPrice();
        }

        double shippingFee = 0;

        if (floristId == 1) {
            shippingFee = 100;
        } else {
            shippingFee = 200;
        }
        double totalPrice = (flowerPrices * totalOrder) + shippingFee;
        totalPrice += flowerPrice;
        flowerPrice += flowerPrices;
        priceOfSalesOrder.setFlowerPrice((double) flowerPrice);
        priceOfSalesOrder.setFeePrice(shippingFee);
        priceOfSalesOrder.setTotalPrice(totalPrice);

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
    public List<FlowerFormula> setFlowerFormula() {
        List<FlowerFormula> flowerFormulasPromotion = new ArrayList<>();
        List<FlowerFormula> flowerFormulas = new ArrayList<>();
        flowerFormulasPromotion = this.flowerFormulaRepository.findAllByFlowerFormulaId();
        flowerFormulas = this.flowerFormulaRepository.findAll();
        for (FlowerFormula flowerFormula: flowerFormulasPromotion){
            flowerFormulas.remove(flowerFormula);
        }
        for (FlowerFormula flowerFormula: flowerFormulasPromotion){
            flowerFormula.setName(flowerFormula.getName() + " โปรโมชั่น");
        }
        for (FlowerFormula flowerFormula: flowerFormulasPromotion){
            flowerFormulas.add(0, flowerFormula);
        }
        return flowerFormulas;
    }
}
