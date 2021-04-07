package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.*;
import com.sweetpeatime.sweetpeatime.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import java.util.function.Predicate;
import java.util.stream.Collectors;



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

    @PersistenceContext
    EntityManager entityManager;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping(value="/getAll")
    public List<SalesOrder> getAllSalesOrder() {
        return this.salesOrderRepository.findAll();
    }

    @GetMapping(value="/getSalesOrderDetailListDto")
    public List<SalesOrderDetailListDto> getSalesOrderListDto()
    {

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

    @GetMapping(value="/searchSalesOrderDetailListDto")
    public List<SalesOrderDetailListDto> searchSalesOrderListDto(@RequestParam("startDate") String startD,@RequestParam("endDate") String endD, @RequestParam("floristId") String floristId) throws ParseException {
       List<SalesOrderDetailListDto> salesOrderDetailListDtos = new ArrayList<>();
       List<SalesOrder>salesOrdersResult = new ArrayList<>();
        Date startDate = null;
        Date endDate = null;

        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");

       if (startD != "") {
           startDate = format.parse(startD);
       }
       if(endD != "") {
           endDate = format.parse(endD);
       }

        StringBuilder selectQueryStr = new StringBuilder("SELECT s FROM SalesOrder s WHERE 1 = 1 ");
        if (startDate == null && endDate == null)
        {
            salesOrdersResult = this.salesOrderRepository.findAll();

        }
        else {

            if ( startDate == null && endDate != null)
            {
                selectQueryStr.append("AND s.date <= :endDate ");

            }
            else if ( startDate != null && endDate == null)
            {
                selectQueryStr.append("AND s.date >= :startDate ");

            }
            else if( startDate != null && endDate != null)
            {
                selectQueryStr.append("AND s.date BETWEEN :startDate AND :endDate  ");

            }

            selectQueryStr.append("ORDER BY s.date ");
            Query selectQuery = entityManager.createQuery(selectQueryStr.toString(), SalesOrder.class);

            if (startDate != null)
                selectQuery.setParameter("startDate", startDate);
            if (endDate != null)
                selectQuery.setParameter("endDate", endDate);

            salesOrdersResult = selectQuery.getResultList();
        }
        for (SalesOrder salesOrder : salesOrdersResult) {
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

            List<SalesOrderDetail> salesOrderDetails = new ArrayList<>();

            salesOrderDetails = this.salesOrderDetailRepository.findAllBySalesOrderId(salesOrder.getId());
          //  salesOrderDetails = (List<SalesOrderDetail>) CollectionUtils.filter(salesOrderDetails, p -> (((SalesOrderDetail) p).getFlorist()).getId().intValue() == 1);

//            if(floristId != null)
//            {
//                //Predicate<SalesOrderDetail> byFlorist = detail -> detail.getFlorist().getId().intValue() = floristId;
//              //   salesOrderDetails = salesOrderDetails.stream().filter(byFlorist).collect(Collectors.toList());
//                //selectQueryStr.append("AND s.date BETWEEN :startDate AND :endDate  ");


//            }
            if(floristId != "") {
                salesOrderDetails = salesOrderDetails.stream().filter(s -> s.getFlorist().getId() == Integer.parseInt(floristId)).collect(Collectors.toList());
                ;
                if (salesOrderDetails.size() > 0) {
                    salesOrderDetailListDto.setSalesOrderDetails(salesOrderDetails);
                    salesOrderDetailListDtos.add(salesOrderDetailListDto);
                }
            }
            else
            {
                salesOrderDetailListDto.setSalesOrderDetails(salesOrderDetails);
                salesOrderDetailListDtos.add(salesOrderDetailListDto);
            }
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
                           if (!promotionDetail.getType().equals("ช่อ")){
                               deleteStock++;
                           }
                           promotionDetail.setQuantity(promotionDetail.getQuantity() - i);
                           promotionDetail.setQuantitySold(promotionDetail.getQuantitySold() + i);
                           this.promotionDetailRepository.saveAndFlush(promotionDetail);
                       }
                   } else {
                       if (promotionDetails.get(0).getQuantity() != 0){
                           if (!promotionDetails.get(0).getType().equals("ช่อ")){
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

//        SalesOrder oldSalesOrder = this.salesOrderRepository.findAllById(updateSalesOrder.getId());

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
        salesOrder.setStatus(updateSalesOrder.getStatus());
        salesOrder.setTotalPrice(updateSalesOrder.getTotalPrice());

        this.salesOrderRepository.saveAndFlush(salesOrder);
        List<SalesOrderDetail> salesOrderDetail = this.salesOrderDetailRepository.findAllBySalesOrderId(updateSalesOrder.getId());
//        Florist florist = this.floristRepository.findFloristById(updateSalesOrder.getFlorist());

//        if(updateSalesOrder.getStatus().equals("ยกเลิกออเดอร์")){
//            cancelSalesOrder(salesOrderDetail);
//        }
//        else{
//            List<FlowerFormulaDetail> flowerFormulaDetail = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(updateSalesOrder.getFlowerFormula());
//
//            for (FlowerFormulaDetail f: flowerFormulaDetail) {
//                Stock stock = this.stockRepository.findStockByFlowerIdAndFloristId(f.getFlower().getFlowerId(), updateSalesOrder.getFlorist());
//                Integer quantity = stock.getQuantity() - (f.getQuantity() * updateSalesOrder.getFlowerAvailable());
//                stock.setQuantity(quantity);
//                this.stockRepository.saveAndFlush(stock);
//            }
//        }

//        salesOrderDetail.setFlorist(florist);
//        salesOrderDetail.setQuantity(updateSalesOrder.getFlowerAvailable());
//        FlowerFormula flowerFormula = this.flowerFormulaRepository.findFlowerFormulaById(updateSalesOrder.getFlowerFormula());
//        salesOrderDetail.setFlowerFormula(flowerFormula);
//        this.salesOrderDetailRepository.saveAndFlush(salesOrderDetail);
    }

    @PostMapping(value = "/cancelSalesOrder")
    public void cancelSalesOrder(@RequestBody Integer salesOrderId) throws ParseException {

        List<SalesOrderDetail> salesOrderDetails = this.salesOrderDetailRepository.findAllBySalesOrderId(salesOrderId);
        for (SalesOrderDetail salesOrderDetail : salesOrderDetails) {
            List<FlowerFormulaDetail> flowerFormulaDetail = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(salesOrderDetail.getFlowerFormula().getId());

            if (salesOrderDetail.getSalesOrder().getStatus().equals("จัดเสร็จแล้ว")) {
//            PromotionDetail promotionDetail = this.promotionDetailRepository.findOneByFlowerFormulaIdAndExpiryDate(salesOrderDetail.getFlowerFormula().getId(), LocalDateTime.from(salesOrderDetail.getSalesOrder().getReceiverDateTime().toInstant()).plusDays(1));
//            if (promotionDetail.getFlowerFormula().getId() == salesOrderDetail.getFlowerFormula().getId() && promotionDetail.getExpiryDate() == salesOrderDetail.getSalesOrder().getDeliveryDateTime()) {
//                promotionDetail.setQuantity(promotionDetail.getQuantity() + salesOrderDetail.getQuantity());
//                this.promotionDetailRepository.saveAndFlush(promotionDetail);
//            } else {
                String dateInStr = this.simpleDateFormat.format(new Date());
                Date date = this.simpleDateFormat.parse(dateInStr);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                c.setTime(salesOrderDetail.getSalesOrder().getDeliveryDateTime());
                c.add(Calendar.DATE, 1);  // number of days to add
                Promotion promotion = new Promotion();
                promotion.setDate(date);

                Promotion promotionCreated = this.promotionRepository.saveAndFlush(promotion);

                PromotionDetail newPromotionDetail = new PromotionDetail();
                newPromotionDetail.setProfit(salesOrderDetail.getSalesOrder().getTotalPrice());
                newPromotionDetail.setPrice(salesOrderDetail.getFlowerFormula().getPrice());
                newPromotionDetail.setQuantity(salesOrderDetail.getQuantity());
                newPromotionDetail.setQuantitySold(0);
                newPromotionDetail.setStatus("active");
                newPromotionDetail.setPromotion(promotionCreated);
                newPromotionDetail.setFlowerFormula(salesOrderDetail.getFlowerFormula());
                newPromotionDetail.setExpiryDate(c.getTime());
                newPromotionDetail.setType("ช่อ");

                this.promotionDetailRepository.saveAndFlush(newPromotionDetail);
//            }
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
