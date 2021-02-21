package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.*;
import com.sweetpeatime.sweetpeatime.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value="/salesOrder")
@CrossOrigin(origins = "http://localhost:4200")
public class SalesOrderController {

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private SalesOrderDetailRepository salesOrderDetailRepository;

    @Autowired
    private FloristRepository floristRepository;

    @Autowired
    private FlowerFormulaRepository flowerFormulaRepository;

    @Autowired
    private FlowerFormulaDetailRepository flowerFormulaDetailRepository;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping(value="/getAll")
    public List<SalesOrder> getAllSalesOrder() {
        return this.salesOrderRepository.findAll();
    }

    @PostMapping(value = "/createSalesOrder")
    public void createSalesOrder(@RequestBody CreateSalesOrder createSalesOrder) throws ParseException {

        String dateInStr = this.simpleDateFormat.format(new Date());
        Date date = this.simpleDateFormat.parse(dateInStr);

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setCustomerName(createSalesOrder.getOrderFirstName() + " " + createSalesOrder.getOrderLastName());
        salesOrder.setCustomerPhone(createSalesOrder.getOrderPhone());
        salesOrder.setDate(date);
        salesOrder.setNote(createSalesOrder.getNote());
        salesOrder.setDeliveryPrice(createSalesOrder.getDeliveryFee());
        salesOrder.setDeliveryDateTime(date);
        salesOrder.setPrice(createSalesOrder.getTotalPrice());
        salesOrder.setReceiverName(createSalesOrder.getReceiverFirstName() + " " + createSalesOrder.getReceiverLastName());
        salesOrder.setReceiverAddress(createSalesOrder.getReceiverAddress());
        salesOrder.setReceiverPhone(createSalesOrder.getReceiverPhone());
        salesOrder.setReceiverDateTime(date);
        salesOrder.setStatus("จ่ายแล้ว");

        SalesOrder salesOrder1 = this.salesOrderRepository.saveAndFlush(salesOrder);

        List<FlowerFormulaDetail> flowerFormulaDetail = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(createSalesOrder.getFlowerFormular());

        for (FlowerFormulaDetail f: flowerFormulaDetail) {
            Stock stock = this.stockRepository.findStockByFlowerIdAndFloristId(f.getFlower().getFlowerId(), createSalesOrder.getFlorist());
            Integer quantity = stock.getQuantity() - (stock.getQuantity() * createSalesOrder.getFlowerAvailable());
            stock.setQuantity(quantity);
            this.stockRepository.saveAndFlush(stock);
        }

        SalesOrderDetail salesOrderDetail = new SalesOrderDetail();
        salesOrderDetail.setSalesOrder(salesOrder1);
        Florist florist = this.floristRepository.findFloristById(createSalesOrder.getFlorist());
        salesOrderDetail.setFlorist(florist);
        salesOrderDetail.setQuantity(createSalesOrder.getFlowerAvailable());
        FlowerFormula flowerFormula = this.flowerFormulaRepository.findFlowerFormulaById(createSalesOrder.getFlowerFormular());
        salesOrderDetail.setFlowerFormula(flowerFormula);
        this.salesOrderDetailRepository.saveAndFlush(salesOrderDetail);

    }
}
