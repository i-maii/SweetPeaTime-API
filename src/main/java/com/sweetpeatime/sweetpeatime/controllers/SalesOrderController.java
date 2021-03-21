package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.*;
import com.sweetpeatime.sweetpeatime.repositories.*;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
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

    @Autowired
    private PromotionDetailRepository promotionDetailRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotionProfitRepository promotionProfitRepository;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping(value="/getAll")
    public List<SalesOrder> getAllSalesOrder() {
        return this.salesOrderRepository.findAll();
    }

    @GetMapping(value="/getSalesOrderDetailListDto")
    public List<SalesOrderDetailListDto> getSalesOrderListDto() {
        List<SalesOrderDetailListDto> salesOrderDetailListDtos = new ArrayList<>();

        List<SalesOrder> salesOrders = this.salesOrderRepository.findAll();

        for (SalesOrder salesOrder : salesOrders) {
            SalesOrderDetailListDto salesOrderDetailListDto = new SalesOrderDetailListDto();
            salesOrderDetailListDto.setId(salesOrder.getId());
            salesOrderDetailListDto.setCustomerName(salesOrder.getCustomerName());
            salesOrderDetailListDto.setCustomerPhone(salesOrder.getCustomerPhone());
            salesOrderDetailListDto.setCustomerLineFb(salesOrder.getCustomerLineFb());
            salesOrderDetailListDto.setDate(salesOrder.getDate());
            salesOrderDetailListDto.setReceiverName(salesOrder.getReceiverName());
            salesOrderDetailListDto.setReceiverPhone(salesOrder.getReceiverPhone());
            salesOrderDetailListDto.setReceiverAddress(salesOrder.getReceiverAddress());
            salesOrderDetailListDto.setReceiveDateTime(salesOrder.getReceiverDateTime());
            salesOrderDetailListDto.setFlowerPrice(salesOrder.getPrice());
            salesOrderDetailListDto.setDeliveryFee(salesOrder.getDeliveryPrice());
            salesOrderDetailListDto.setTotalPrice(salesOrder.getTotalPrice());
            salesOrderDetailListDto.setNote(salesOrder.getNote());
            salesOrderDetailListDto.setStatus(salesOrder.getStatus());

            List<SalesOrderDetail> salesOrderDetails = this.salesOrderDetailRepository.findAllBySalesOrderId(salesOrder.getId());
            salesOrderDetailListDto.setSalesOrderDetails(salesOrderDetails);
            salesOrderDetailListDtos.add(salesOrderDetailListDto);
        }
        return salesOrderDetailListDtos;
    }

    @PostMapping(value = "/createSalesOrder")
    public void createSalesOrder(@RequestBody SalesOrderListDto createSalesOrder) throws ParseException {

        String dateInStr = this.simpleDateFormat.format(new Date());
        Date date = this.simpleDateFormat.parse(dateInStr);

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setCustomerName(createSalesOrder.getCustomerName());
        salesOrder.setCustomerLineFb(createSalesOrder.getCustomerLineFb());
        salesOrder.setCustomerPhone(createSalesOrder.getCustomerPhone());
        salesOrder.setDate(createSalesOrder.getDate());
        salesOrder.setNote(createSalesOrder.getNote());
        salesOrder.setDeliveryPrice(createSalesOrder.getDeliveryFee());
        salesOrder.setDeliveryDateTime(createSalesOrder.getReceiveDateTime());
        salesOrder.setPrice(createSalesOrder.getFlowerPrice());
        salesOrder.setReceiverName(createSalesOrder.getReceiverName());
        salesOrder.setReceiverAddress(createSalesOrder.getReceiverAddress());
        salesOrder.setReceiverPhone(createSalesOrder.getReceiverPhone());
        salesOrder.setReceiverDateTime(createSalesOrder.getReceiveDateTime());
        salesOrder.setStatus("จ่ายแล้ว");
        salesOrder.setTotalPrice(createSalesOrder.getTotalPrice());

        //create salesorider
        SalesOrder salesOrder1 = this.salesOrderRepository.saveAndFlush(salesOrder);
        int deleteStock = 0;

        //decrease promotion detail
       for (FlowerMultipleDto flowerMultipleDto : createSalesOrder.getFlowerMultipleDtoList()){
           if (flowerMultipleDto.getFlowerAvailable() >= flowerMultipleDto.getOrderTotal()) {
               List<PromotionDetail> promotionDetails = this.promotionDetailRepository.findOneByFlowerFormulaIdAndStatusAndExpiryDate(flowerMultipleDto.getFlowerFormula(), createSalesOrder.getReceiveDateTime());


               for (int i = 1; i <= flowerMultipleDto.getOrderTotal(); i++){
                   if(promotionDetails.size() > 1){
                       PromotionDetail promotionDetail = new PromotionDetail();
                       int temp = 0;
                       for (int j = 0; j < promotionDetails.size()-1; j++) {
                           if (promotionDetails.get(temp).getExpiryDate().before(promotionDetails.get(j+1).getExpiryDate()) && promotionDetail.getQuantity() != 0) {
                               promotionDetail = promotionDetails.get(temp);
                           } else {
                               promotionDetail = promotionDetails.get(j+1);
                               temp = j + 1;
                           }
                       }

                       if (promotionDetail.getQuantity() != 0){
                           if (promotionDetails.get(0).getType() == null){
                               deleteStock++;
                           }
                           promotionDetail.setQuantity(promotionDetail.getQuantity() - i);
                           promotionDetail.setQuantitySold(promotionDetail.getQuantitySold() + i);
                           this.promotionDetailRepository.saveAndFlush(promotionDetail);
                       }
                   } else {
                       if (promotionDetails.get(0).getQuantity() != 0){
                           if (promotionDetails.get(0).getType() == null){
                               deleteStock++;
                           }

                           promotionDetails.get(0).setQuantity(promotionDetails.get(0).getQuantity() - i);
                           promotionDetails.get(0).setQuantitySold(promotionDetails.get(0).getQuantitySold() + i);
                           this.promotionDetailRepository.saveAndFlush(promotionDetails.get(0));
                       }
                   }

               }
           }

           List<FlowerFormulaDetail> flowerFormulaDetail = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(flowerMultipleDto.getFlowerFormula());

           //decrease stock
           for (FlowerFormulaDetail f: flowerFormulaDetail) {
               List<Stock> stocks = this.stockRepository.findAllByFlowerIdAndFloristIdOrderByLotAsc(f.getFlower().getFlowerId(), createSalesOrder.getFlorist());
               int temp = f.getQuantity() * flowerMultipleDto.getOrderTotal();
               for (Stock stock : stocks) {
                   if (temp >= stock.getQuantity()) {
                       temp -= stock.getQuantity();
                       stock.setQuantity(temp);
                       this.stockRepository.saveAndFlush(stock);
                   } else {
                       temp = stock.getQuantity() - temp;
                       stock.setQuantity(temp);
                       this.stockRepository.saveAndFlush(stock);
                       break;
                   }
               }
           }

           //create salesorderDetail
           SalesOrderDetail salesOrderDetail = new SalesOrderDetail();
           salesOrderDetail.setSalesOrder(salesOrder1);
           Florist florist = this.floristRepository.findFloristById(createSalesOrder.getFlorist());
           salesOrderDetail.setFlorist(florist);
           salesOrderDetail.setQuantity(flowerMultipleDto.getOrderTotal());
           FlowerFormula flowerFormula = this.flowerFormulaRepository.findFlowerFormulaById(flowerMultipleDto.getFlowerFormula());
           salesOrderDetail.setFlowerFormula(flowerFormula);
           this.salesOrderDetailRepository.saveAndFlush(salesOrderDetail);
       }

    }

    @PostMapping(value = "/updateSalesOrder")
    public void updateSalesOrder(@RequestBody SalesOrderListDto updateSalesOrder) throws ParseException {

        String dateInStr = this.simpleDateFormat.format(new Date());
        Date date = this.simpleDateFormat.parse(dateInStr);

        SalesOrder oldSalesOrder = this.salesOrderRepository.findAllById(updateSalesOrder.getId());

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(updateSalesOrder.getId());
        salesOrder.setDate(updateSalesOrder.getDate());
        salesOrder.setCustomerName(updateSalesOrder.getCustomerName());
        salesOrder.setCustomerLineFb(updateSalesOrder.getCustomerLineFb());
        salesOrder.setCustomerPhone(updateSalesOrder.getCustomerPhone());
        salesOrder.setNote(updateSalesOrder.getNote());
        salesOrder.setDeliveryPrice(updateSalesOrder.getDeliveryFee());
        salesOrder.setDeliveryDateTime(updateSalesOrder.getReceiveDateTime());
        salesOrder.setPrice(updateSalesOrder.getFlowerPrice());
        salesOrder.setReceiverName(updateSalesOrder.getReceiverName());
        salesOrder.setReceiverAddress(updateSalesOrder.getReceiverAddress());
        salesOrder.setReceiverPhone(updateSalesOrder.getReceiverPhone());
        salesOrder.setReceiverDateTime(updateSalesOrder.getReceiveDateTime());
        salesOrder.setTotalPrice(updateSalesOrder.getTotalPrice());

        if (updateSalesOrder.getStatus().equals("ยกเลิกออเดอร์") && !oldSalesOrder.getStatus().equals("ยกเลิกออเดอร์")) {
            cancelSalesOrder(updateSalesOrder.getId());
            salesOrder.setStatus(updateSalesOrder.getStatus());
        } else {
            salesOrder.setStatus(updateSalesOrder.getStatus());
        }
        this.salesOrderRepository.saveAndFlush(salesOrder);
    }

    @PostMapping(value = "/cancelSalesOrder")
    public void cancelSalesOrder(@RequestBody Integer salesOrderId) throws ParseException {

        PromotionProfit promotionProfit = this.promotionProfitRepository.findOneByAge(1);
        List<SalesOrderDetail> salesOrderDetails = this.salesOrderDetailRepository.findAllBySalesOrderId(salesOrderId);
        for (SalesOrderDetail salesOrderDetail : salesOrderDetails) {
            List<FlowerFormulaDetail> flowerFormulaDetail = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(salesOrderDetail.getFlowerFormula().getId());

            if (salesOrderDetail.getSalesOrder().getStatus().equals("จัดเสร็จแล้ว")) {
                String dateInStr = this.simpleDateFormat.format(new Date());
                Date date = this.simpleDateFormat.parse(dateInStr);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                c.setTime(salesOrderDetail.getSalesOrder().getDeliveryDateTime());
                c.add(Calendar.DATE, 1);  // number of days to add
                Promotion promotion = new Promotion();
                promotion.setDate(date);

                Promotion promotionCreated = this.promotionRepository.saveAndFlush(promotion);
                int oldPrice = salesOrderDetail.getFlowerFormula().getPrice();
                int profitDis = promotionProfit.getProfit();
                double profit = profitDis / 100.0;
                double newProfit = oldPrice * profit;
                int newPromotionPrice = (int) (oldPrice - newProfit);
                PromotionDetail newPromotionDetail = new PromotionDetail();
                newPromotionDetail.setProfit(salesOrderDetail.getSalesOrder().getTotalPrice());
                newPromotionDetail.setPrice(newPromotionPrice);
                newPromotionDetail.setQuantity(salesOrderDetail.getQuantity());
                newPromotionDetail.setQuantitySold(0);
                newPromotionDetail.setStatus("active");
                newPromotionDetail.setPromotion(promotionCreated);
                newPromotionDetail.setFlowerFormula(salesOrderDetail.getFlowerFormula());
                newPromotionDetail.setExpiryDate(c.getTime());
                newPromotionDetail.setType("ยกเลิกช่อ");
                newPromotionDetail.setFlorist(salesOrderDetail.getFlorist());

                this.promotionDetailRepository.saveAndFlush(newPromotionDetail);
            } else {
                for (FlowerFormulaDetail f: flowerFormulaDetail) {
                    Stock stock = this.stockRepository.findAllByFlowerIdAndFloristIdOrderByLotDesc(f.getFlower().getFlowerId(), salesOrderDetail.getFlorist().getId());
                    Integer quantity = stock.getQuantity() + (f.getQuantity() * salesOrderDetail.getQuantity());
                    stock.setQuantity(quantity);
                    this.stockRepository.saveAndFlush(stock);
                }
            }
            SalesOrder salesOrder = salesOrderDetail.getSalesOrder();
            salesOrder.setStatus("ยกเลิกออเดอร์");
            this.salesOrderRepository.saveAndFlush(salesOrder);
        }
    }

}
