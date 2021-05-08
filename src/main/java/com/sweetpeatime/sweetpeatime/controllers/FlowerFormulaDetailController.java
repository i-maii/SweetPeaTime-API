package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.FlowerFormulaDetail;
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
import java.util.Calendar;
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

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping(value="/getAll")
    public List<FlowerFormulaDetail> getAll() {
        return this.flowerFormulaDetailRepository.findAll();
    }

    @GetMapping(value="/getQuantityPromotion")
    public List<FlowerFormulaDetail> getQuantityPromotion(@RequestParam("formulaId") Integer formulaId,@RequestParam("flowerId") Integer flowerId) {
        //return this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(formulaId);
        //System.out.println("test" + formulaId);
        return this.flowerFormulaDetailRepository.findAllByFlowerFormulaIdAndFlowerId(formulaId, flowerId);
    }

    @GetMapping(value="/getCheckStock")
    public List<Stock> getCheckStock(@RequestParam("flowerId") Integer flowerId) {
        return this.stockRepository.findAllByFlowerId(flowerId);
    }

    @GetMapping(value="/getFormulary")
    public List<FlowerFormulaDetail> getFormulary(){
        return this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(9);
    }

    @GetMapping(value="/getFormulaDetailsFromFormulaId")
    public List<FlowerFormulaDetail> getFormulaDetailsFromFormulaId(@RequestParam("formulaId") Integer formulaId){
        return this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(formulaId);
    }

    @GetMapping(value="/getFormulaDetail")
    public Integer getFlowerFormularDetail(@RequestParam("formulaId") Integer formulaId, @RequestParam("floristId") Integer floristId, @RequestParam("orderDate") String orderDate) throws ParseException {
        List<PromotionDetail> promotionDetails = this.promotionDetailRepository.findAllByFlowerFormulaIdAndFloristId(formulaId, floristId);

        Date date = simpleDateFormat.parse(orderDate);
        int available = 0;

        for( PromotionDetail promotionDetail : promotionDetails) {
            if((date.before(promotionDetail.getExpiryDate())) && (promotionDetail.getFlorist().getId() == floristId) && (promotionDetail.getStatus().equals("active"))) {
                available += promotionDetail.getQuantity();
            } else {
                available = 0;
            }
        }

        return available;
    }

    @GetMapping(value="/getFormulaDetailFromStock")
    public Integer getFlowerFormularDetailFromStock(@RequestParam("formulaId") Integer formulaId, @RequestParam("floristId") Integer floristId, @RequestParam("orderDate") String orderDate) throws ParseException {
        List<FlowerFormulaDetail> flowerFormulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(formulaId);
        List<Stock> stocks = new ArrayList<>();
        int quantityAvailablePerFlower = 0;
        int quantityAvailablePerFormula = 0;

        int stockQuantity = 0;
        Date date = simpleDateFormat.parse(orderDate);
        Calendar receiveDate = Calendar.getInstance();
        receiveDate.setTime(date);

        Calendar lotDate = Calendar.getInstance();

        for (FlowerFormulaDetail f: flowerFormulaDetails) {
            stocks = this.stockRepository.findAllByFlowerIdAndFloristIdOrderByLotAsc(f.getFlower().getFlowerId(), floristId);
            for (Stock s: stocks)
            {
                lotDate.setTime(s.getLot());
                lotDate.add(Calendar.DATE,f.getFlower().getLifeTime());
                lotDate.add(Calendar.DATE,-1);
                Date expireDate = lotDate.getTime();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String expireDateStr = format.format(expireDate);
                expireDate = simpleDateFormat.parse(expireDateStr);

                Date receiveDateForcompare = receiveDate.getTime();
                //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String receiveDateStr = format.format(receiveDateForcompare);
                receiveDateForcompare = simpleDateFormat.parse(receiveDateStr);
                //date.e = null;
                if (receiveDateForcompare.before(expireDate))
                {
                    stockQuantity = stockQuantity + s.getQuantity();
                }
            }
            quantityAvailablePerFlower = stockQuantity / f.getQuantity();
            if (quantityAvailablePerFormula == 0)
            {
                quantityAvailablePerFormula = quantityAvailablePerFlower;
            }
            else {
                if (quantityAvailablePerFormula > quantityAvailablePerFlower)
                {
                    quantityAvailablePerFormula = quantityAvailablePerFlower;
                }
            }
        }

        return quantityAvailablePerFormula;
    }

}
