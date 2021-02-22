package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.FlowerFormula;
import com.sweetpeatime.sweetpeatime.entities.PriceOfSalesOrder;
import com.sweetpeatime.sweetpeatime.repositories.FlowerFormulaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@RestController
@Controller
@RequestMapping(value="/flowerFormula")
@CrossOrigin(origins = "http://localhost:4200")
public class FlowerFormulaController {

    @Autowired
    FlowerFormulaRepository flowerFormulaRepository;

    @PersistenceContext
    EntityManager entityManager;

    @GetMapping(value="/getAll")
    public List<FlowerFormula> getAllFlowerFormular() {
        return this.flowerFormulaRepository.findAll();
    }

    @GetMapping(value="/priceOfSalesOrder")
    public PriceOfSalesOrder getQuantityAvailable(
            @RequestParam("formulaId") Integer formulaId,
            @RequestParam("floristId") Integer floristId,
            @RequestParam("totalOrder") Integer totalOrder) {
        PriceOfSalesOrder priceOfSalesOrder = new PriceOfSalesOrder();
        if(totalOrder == 0){
            priceOfSalesOrder.setFlowerPrice(0.0);
            priceOfSalesOrder.setFeePrice(0.0);
            priceOfSalesOrder.setTotalPrice(0.0);
        }else{
            int flowerPrice = this.flowerFormulaRepository.getFlowerPrice(formulaId);
            double shippingFee = 0;

            if(floristId == 1){
                shippingFee = 100;
            }else{
                shippingFee = 200;
            }

            double totalPrice = (flowerPrice * totalOrder) + shippingFee;

            priceOfSalesOrder.setFlowerPrice((double) flowerPrice);
            priceOfSalesOrder.setFeePrice(shippingFee);
            priceOfSalesOrder.setTotalPrice(totalPrice);
        }

        return priceOfSalesOrder;
    }

    @GetMapping(value="/searchFlowerFormula")
    public List<FlowerFormula> searchFlowerFormula() {
        return this.flowerFormulaRepository.findAll();
    }

    @PostMapping(value="/search")
    public List<FlowerFormula> search(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "pattern", required = false) String pattern,
            @RequestParam(value = "occasion", required = false) String occasion,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "quantityAvailable", required = false) String quantityAvailable,
            @RequestParam(value = "size", required = false) String size
    ){
        StringBuilder selectQueryStr = new StringBuilder("SELECT f FROM FlowerFormula f WHERE 1 = 1 ");

        if (name != null)
            //selectQueryStr.append("AND f.name like :name ");
            selectQueryStr.append("AND f.name like CONCAT('%', :name, '%') ");

        if(pattern != null)
            selectQueryStr.append("AND f.pattern = :pattern ");

        if(occasion != null)
            selectQueryStr.append("AND f.occasion = :occasion ");

        if(price != null)
            selectQueryStr.append("AND f.price <= :price ");

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

        if (occasion != null)
            selectQuery.setParameter("occasion", occasion);

        if (price != null)
            selectQuery.setParameter("price", price);

        if (quantityAvailable != null)
            selectQuery.setParameter("quantityAvailable", quantityAvailable);

        if (size != null)
            selectQuery.setParameter("size", size);

        List<FlowerFormula> flowerFormulas = selectQuery.getResultList();

        return flowerFormulas;
    }
}
