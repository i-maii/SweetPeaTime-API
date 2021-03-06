package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.SalesOrder;
import com.sweetpeatime.sweetpeatime.entities.SalesOrderDetail;
import com.sweetpeatime.sweetpeatime.entities.SalesOrderListDto;
import com.sweetpeatime.sweetpeatime.repositories.SalesOrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping(value="/salesOrderDetail")
@CrossOrigin(origins = "http://localhost:4200")
public class SalesOrderDetailController {

    @Autowired
    private SalesOrderDetailRepository salesOrderDetailRepository;

    @GetMapping(value="/getAllSalesOrderDetail")
    public List<SalesOrderDetail> getBySalesOrder() {
        return this.salesOrderDetailRepository.findAll();
    }

    @GetMapping(value="/getBySalesOrder")
    public List<SalesOrderDetail> getBySalesOrder(@RequestParam("salesOrderId") Integer salesOrderId) {
        return this.salesOrderDetailRepository.findAllBySalesOrderId(salesOrderId);
    }
}
