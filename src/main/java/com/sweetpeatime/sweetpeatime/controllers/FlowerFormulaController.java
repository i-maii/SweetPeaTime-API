package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.FlowerFormula;
import com.sweetpeatime.sweetpeatime.entities.FlowerQuantityAvaliableDto;
import com.sweetpeatime.sweetpeatime.repositories.FlowerFormulaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@RestController
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

    @GetMapping(value="/getQuantityAvailable/{id}")
    public List<FlowerQuantityAvaliableDto> getQuantityAvailable(@PathVariable("id") Integer id) {
        List<FlowerQuantityAvaliableDto> flowerQuantityAvaliableDtos = new ArrayList<>();
        for(int i = 1; i <= this.flowerFormulaRepository.getQuantityAvailable(id); i++){
            FlowerQuantityAvaliableDto flowerQuantityAvaliableDto = new FlowerQuantityAvaliableDto();
            flowerQuantityAvaliableDto.setId(i);
            flowerQuantityAvaliableDto.setFlowerQuantityAvailiable(i);
            flowerQuantityAvaliableDtos.add(flowerQuantityAvaliableDto);
        }
        return flowerQuantityAvaliableDtos;
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
            @RequestParam(value = "price", required = false) Number price,
            @RequestParam(value = "quantityAvailable", required = false) String quantityAvailable,
            @RequestParam(value = "size", required = false) String size
    ){
        StringBuffer selectQueryStr = new StringBuffer("SELECT f FROM FlowerFormula f WHERE 1 = 1 ");

        if (name != null)
            selectQueryStr.append("AND f.name = :name ");

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

        selectQueryStr.append("ORDER BY f.name DESC ");

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
