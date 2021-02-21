package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.SalesOrder;
import com.sweetpeatime.sweetpeatime.entities.SalesOrderDetail;
import com.sweetpeatime.sweetpeatime.repositories.SalesOrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/salesOrderDetail")
@CrossOrigin(origins = "http://localhost:4200")
public class SalesOrderDetailController {

    @Autowired
    private SalesOrderDetailRepository salesOrderDetailRepository;

    @GetMapping(value="/getAllBySaleOrder")
    public SalesOrderDetail getAllSalesOrder(@RequestParam("salesOrderId") Integer salesOrderId) {
        return this.salesOrderDetailRepository.findAllBySalesOrderId(salesOrderId);
    }

}
