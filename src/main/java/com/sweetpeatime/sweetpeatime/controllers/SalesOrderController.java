package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.SalesOrder;
import com.sweetpeatime.sweetpeatime.repositories.SalesOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="/salesOrder")
public class SalesOrderController {
    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(value="/getAll")
    public List<SalesOrder> getAllSalesOrder() {
        return this.salesOrderRepository.findAll();
    }
}
