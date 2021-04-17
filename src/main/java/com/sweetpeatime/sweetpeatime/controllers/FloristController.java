package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.Florist;
import com.sweetpeatime.sweetpeatime.entities.FloristFee;
import com.sweetpeatime.sweetpeatime.repositories.FloristFeeRepository;
import com.sweetpeatime.sweetpeatime.repositories.FloristRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value="/florist")
public class FloristController {

    @Autowired
    FloristRepository floristRepository;

    @Autowired
    FloristFeeRepository floristFeeRepository;

    @PersistenceContext
    EntityManager entityManager;

    @GetMapping(value="/getAll")
    public List<Florist> getAllFlorist() {
        return this.floristRepository.findAll();
    }

    @GetMapping(value="/getById")
    public Florist getFloristById(
            @RequestParam("id") Integer id
    ) {
        return this.floristRepository.findFloristById(id);
    }

    @GetMapping(value="/getAllFloristFee")
    public List<FloristFee> getAllFloristFee() {
        return this.floristFeeRepository.findAll();
    }

    @GetMapping(value="/getFloristFeeBySize")
    public List<FloristFee> getFloristFeeBySize(@RequestParam("floristId") Integer floristId,@RequestParam("size") String size) {

       List<FloristFee> floristFeeResult = new ArrayList<>();

        StringBuilder selectQueryStr = new StringBuilder("SELECT f FROM FloristFee f WHERE 1 = 1 ");
        selectQueryStr.append("AND f.floristId = :floristId ");

        selectQueryStr.append("AND f.size = :size");

        Query selectQuery = entityManager.createQuery(selectQueryStr.toString());

            if (floristId != null)
                selectQuery.setParameter("floristId", floristId);
            if (size != null)
                selectQuery.setParameter("size", size);

        floristFeeResult = selectQuery.getResultList();


        return floristFeeResult;
    }
}
