package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.dto.AddStockDTO;
import com.sweetpeatime.sweetpeatime.dto.ChangeStockDTO;
import com.sweetpeatime.sweetpeatime.dto.DeleteStockDTO;
import com.sweetpeatime.sweetpeatime.entities.*;
import com.sweetpeatime.sweetpeatime.repositories.*;
import com.sweetpeatime.sweetpeatime.wrapper.StockWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value="/stock")
public class StockController {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private FlowerRepository flowerRepository;

    @Autowired
    private FloristRepository floristRepository;

    @Autowired
    private FlowerPriceRepository flowerPriceRepository;

    @Autowired
    PromotionDetailLogRepository promotionDetailLogRepository;

    @Autowired
    FlowerFormulaRepository flowerFormulaRepository;

    @Autowired
    PromotionRepository promotionRepository;

    @Autowired
    FlowerFormulaDetailRepository flowerFormulaDetailRepository;

    @Autowired
    ConfigurationsRepository configurationsRepository;

    @Autowired
    PromotionDetailRepository promotionDetailRepository;

    @Autowired
    PromotionProfitRepository promotionProfitRepository;

    @Autowired
    FloristFeeRepository floristFeeRepository;

    @PersistenceContext
    EntityManager entityManager;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping("")
    public List<Stock> getAllStock() {
        StringBuilder selectQueryStr = new StringBuilder("SELECT " +
                "s.flowerId, " +
                "s.floristId, " +
                "SUM(s.quantity) as quantity " +
                "FROM Stock s " +
                "GROUP BY s.flowerId, s.floristId");
        Query selectQuery = entityManager.createNativeQuery(selectQueryStr.toString(), "stockMapping");

        List<StockWrapper> stockList = selectQuery.getResultList();

        List<Stock> stock = new ArrayList<>();
        for (StockWrapper stockWrapper: stockList) {
            Stock s = new Stock();
            s.setFlower(this.flowerRepository.findOneById(stockWrapper.getFlowerId()));
            s.setFlorist(this.floristRepository.findFloristById(stockWrapper.getFloristId()));
            s.setQuantity(stockWrapper.getQuantity());

            stock.add(s);
        }

        return stock;
    }

    @GetMapping("/getStockByLot")
    public List<Stock> getStockByLot(@RequestParam("startDate") String startD,@RequestParam("endDate") String endD) throws ParseException {
        Date startDate = null;
        Date endDate = null;
        List<Stock> stock = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");

        if (startD != "") {
            startDate = format.parse(startD);
        }
        if(endD != "") {
            endDate = format.parse(endD);
        }
        StringBuilder selectQueryStr = new StringBuilder("SELECT * FROM Stock s WHERE 1 = 1 ");
        if (startDate == null && endDate == null)
        {
            stock = this.stockRepository.findAll();
        }
        else {
            if (startDate == null && endDate != null) {
                selectQueryStr.append("AND s.lot <= :endDate ");

            } else if (startDate != null && endDate == null) {
                selectQueryStr.append("AND s.lot >= :startDate ");

            } else if (startDate != null && endDate != null) {
                selectQueryStr.append("AND s.lot BETWEEN :startDate AND :endDate  ");

            }
        }

        Query selectQuery = entityManager.createNativeQuery(selectQueryStr.toString(), "stockMapping");
        if (startDate != null)
            selectQuery.setParameter("startDate", startDate);
        if (endDate != null)
            selectQuery.setParameter("endDate", endDate);

        List<StockWrapper> stockList = selectQuery.getResultList();

         stock = new ArrayList<>();
        for (StockWrapper stockWrapper: stockList) {
            Stock s = new Stock();
            s.setFlower(this.flowerRepository.findOneById(stockWrapper.getFlowerId()));
            s.setFlorist(this.floristRepository.findFloristById(stockWrapper.getFloristId()));
            s.setQuantity(stockWrapper.getQuantity());
            FlowerPrice flowerPrice = this.flowerPriceRepository.findByFlowerId(stockWrapper.getFlowerId());
            s.setFlowerPrice(flowerPrice);
            stock.add(s);
        }
        return stock;
    }

    @GetMapping("/getStockByDate")
    public List<Stock> getStockByDate(@RequestParam("startDate") String startD,@RequestParam("endDate") String endD) throws ParseException {
        Date startDate = null;
        Date endDate = null;
        List<Stock> stock = new ArrayList<>();
       // List<StockLot> stockList = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");

        if (!startD.isEmpty()) {
            startDate = format.parse(startD);
        }
        if(!endD.isEmpty()) {
            endDate = format.parse(endD);
        }
        StringBuilder selectQueryStr = new StringBuilder("SELECT * FROM Stock s WHERE 1 = 1 ");
        if (startDate == null && endDate == null)
        {
            stock = this.stockRepository.findAll();
        }
        else {
            if (startDate == null && endDate != null) {
                selectQueryStr.append("AND s.lot <= :endDate ");

            } else if (startDate != null && endDate == null) {
                selectQueryStr.append("AND s.lot >= :startDate ");

            } else if (startDate != null && endDate != null) {
                selectQueryStr.append("AND s.lot BETWEEN :startDate AND :endDate  ");

            }
        }

        Query selectQuery = entityManager.createNativeQuery(selectQueryStr.toString(), "stockLotMapping");
        if (startDate != null)
            selectQuery.setParameter("startDate", startDate);
        if (endDate != null)
            selectQuery.setParameter("endDate", endDate);

        List<StockLot> stockList  = selectQuery.getResultList();

        stock = new ArrayList<>();
        for (StockLot stockItem: stockList) {
            Stock s = new Stock();
            s.setFlower(this.flowerRepository.findOneById(stockItem.getFlowerId()));
            s.setFlorist(this.floristRepository.findFloristById(stockItem.getFloristId()));
            s.setQuantity(stockItem.getQuantity());
            s.setUnit(stockItem.getUnit());
            s.setLot(stockItem.getLot());
            s.setDeleteQty(stockItem.getDeleteQty());
            stock.add(s);
        }


        return stock;
    }

    @PostMapping("/deleteStock")
    public List<ChangeStockDTO> deleteStockQuantity(@RequestBody List<DeleteStockDTO> deleteStock) throws ParseException {
        List<ChangeStockDTO> changeStockDTOList = new ArrayList<>();

        int dlQuantity = 0;
        for (DeleteStockDTO ds: deleteStock) {
            List<Stock> stock = this.stockRepository.findAllByFlowerIdAndFloristIdOrderByLotAsc(ds.getFlowerId(), ds.getFloristId());
            Integer deleteQuantity = ds.getDeleteQuantity();

            for (Stock s: stock) {
                if (s.getQuantity() != 0) {
                    if (deleteQuantity > s.getQuantity()) {
                        deleteQuantity = deleteQuantity - s.getQuantity();
                        if (s.getDeleteQty() != null) {
                            s.setDeleteQty(s.getDeleteQty() + s.getQuantity());
                        } else {
                            s.setDeleteQty(s.getQuantity());
                        }
                        s.setQuantity(0);
                        this.stockRepository.saveAndFlush(s);
                    } else if (deleteQuantity < s.getQuantity()) {
                        dlQuantity = s.getQuantity() - deleteQuantity;
                        if (s.getDeleteQty() != null) {
                            s.setDeleteQty(deleteQuantity + s.getDeleteQty());
                        } else {
                            s.setDeleteQty(deleteQuantity);
                        }
                        s.setQuantity(s.getQuantity() - deleteQuantity);
                        this.stockRepository.saveAndFlush(s);
                        break;
                    } else {
                        if (s.getDeleteQty() != null) {
                            s.setDeleteQty(s.getQuantity() + s.getDeleteQty());
                        } else {
                            s.setDeleteQty(s.getQuantity());
                        }
                        s.setQuantity(0);
                        this.stockRepository.saveAndFlush(s);
                        break;
                    }
                }
            }

            List<PromotionDetail> promotionDetails = this.promotionDetailRepository.findAllByStatus("active");
            for (PromotionDetail promotionDetail: promotionDetails){
                boolean recal = false;
                List<FlowerFormulaDetail> formulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetail.getFlowerFormula().getId());
                for(FlowerFormulaDetail formulaDetail: formulaDetails){
                    if(formulaDetail.getFlower().getFlowerId().equals(ds.getFlowerId())){
                        recal = true;
                        int available = formulaDetail.getQuantity() * promotionDetail.getQuantity();
                        if (dlQuantity >= available){
                            promotionDetail.setQuantity(promotionDetail.getQuantity());
                        }else{
                            int availablePromotion = dlQuantity / formulaDetail.getQuantity();
                            ChangeStockDTO changeStockDTO = new ChangeStockDTO();
                            if(availablePromotion > 0){ //มีการเปลี่ยนแปลงจำนวนช่อโปรโมชั่น ชื่อ จำนวนที่เปลี่ยน
                                changeStockDTO.setFormulaName(promotionDetail.getFlowerFormula().getName());
                                changeStockDTO.setBeforeQuantity(promotionDetail.getQuantity());
                                changeStockDTO.setRemainQuantity(availablePromotion);
                                changeStockDTO.setStatus("change");
                                changeStockDTOList.add(changeStockDTO);
                                promotionDetail.setQuantity(availablePromotion);
                            }else if(availablePromotion == 0){ //ไม่สามารถทำโปรโมชั่นนี้ได้แล้ว เนื่องจากจำนวนดอกไม้ไม่เพียงพอ
                                changeStockDTO.setFormulaName(promotionDetail.getFlowerFormula().getName());
                                changeStockDTO.setStatus("inactive");
                                changeStockDTOList.add(changeStockDTO);
                                promotionDetail.setQuantity(availablePromotion);
                                promotionDetail.setStatus("inactive");
                            }
                            this.promotionDetailRepository.saveAndFlush(promotionDetail);
                            getRecalculate();
                        }
                        break;
                    }
                }
                if(!recal){
                    getRecalculate();
                }
            }
        }

        return changeStockDTOList;
    }

    @PostMapping("/addStock")
    public void addStockQuantity(@RequestBody List<AddStockDTO> addStock) throws ParseException {
        for (AddStockDTO s: addStock) {
            FlowerPrice flowerPrice = this.flowerPriceRepository.findByFlowerId(s.getFlower().getFlowerId());
            if (flowerPrice == null) {
                flowerPrice = new FlowerPrice();
                flowerPrice.setFlower(s.getFlower());
            }
            flowerPrice.setPrice(s.getPrice());
            this.flowerPriceRepository.saveAndFlush(flowerPrice);

            List<Stock> oldStock = this.stockRepository.findAllByQuantityLessThanAndFlowerIdOrderByLotAsc(0, s.getFlower().getFlowerId());
            Integer quantity = s.getQuantity();
            if (oldStock != null) {
                for (Stock o : oldStock) {
                    if (quantity >= Math.abs(o.getQuantity())) {
                        quantity = quantity + o.getQuantity();
                        o.setQuantity(0);
                    } else {
                        o.setQuantity(quantity + o.getQuantity());
                        quantity = 0;
                    }
                    this.stockRepository.saveAndFlush(o);

                    if (quantity <= 0)
                        break;
                }
            }

            Stock stock = this.stockRepository.findStockByFlowerIdAndLotAndFloristId(s.getFlower().getFlowerId(), dateFormat.parse(s.getLot()), s.getFlorist().getId());
            if (stock == null) {
                stock = new Stock();
                stock.setFlower(s.getFlower());
                stock.setQuantity(quantity);
                stock.setLot(dateFormat.parse(s.getLot()));
                stock.setUnit(s.getFlower().getUnit());
                stock.setFlorist(s.getFlorist());
                stock.setFlowerPrice(flowerPrice);
            } else {
                stock.setQuantity(quantity + stock.getQuantity());
            }

            this.stockRepository.saveAndFlush(stock);
        }
    }

    @GetMapping(value="/getPromotionSuggest")
    public void getPromotionSuggest(List<StockRemainDto>  stockRemainDtos, String type) throws ParseException {

        String dateInStr = dateFormat.format(new Date());
        Date date = new Date();
        LocalDate currentDate = LocalDate.now();
        LocalDate dateTime1 = currentDate.minus(4, ChronoUnit.DAYS);
        LocalDate createDate = currentDate.plus(0, ChronoUnit.DAYS);
        LocalDate expireDate = currentDate.plus(2, ChronoUnit.DAYS);

        //For Check Duplicate
        LocalDate dateTimeDupFrom = currentDate.minus(14, ChronoUnit.DAYS);
        LocalDate dateTimeDupTo = currentDate.minus(7, ChronoUnit.DAYS);

        ZoneId zoneId = ZoneId.systemDefault();
        Date dateFrom = Date.from(dateTime1.atStartOfDay(zoneId).toInstant());
        Date createtDate = Date.from(createDate.atStartOfDay(zoneId).toInstant());
        Date expiryDate = Date.from(expireDate.atStartOfDay(zoneId).toInstant());
        Date dateTo = dateFormat.parse(dateInStr);
        Date dateTimeFrom = Date.from(dateTimeDupFrom.atStartOfDay(zoneId).toInstant());
        Date dateTimeTo = Date.from(dateTimeDupTo.atStartOfDay(zoneId).toInstant());

        int chkSize = 0;
        int availableQuantity = 0;
        int flowerLifeTime = 0;
        int profitFlower = 0;
        int availableQuantitySum;
        int calProfitCurrent = 0;
        int floristId = 0;
        Date lot = null;
        String floristName = null;
        String typeFlower = null;

        List<FlowerFormulaDetail> formulaDetails = new ArrayList<>();
        List<PromotionDetailCurrentDto> promotionDetailCurrentDtos = new ArrayList<>();
        List<StockRemainDto> stockRemainDtoList = new ArrayList<>();
        PromotionDetailCurrentDto promotionDetailCurrentDto = new PromotionDetailCurrentDto();

        for (StockRemainDto stockRemainDto: stockRemainDtos) {
            if (stockRemainDto.getRemainQuantity() > 0) {
                stockRemainDtoList.add(stockRemainDto);
            }
        }

        int loop = 0;
        other:
        for (StockRemainDto stock : stockRemainDtoList) {
            Flower flower = this.flowerRepository.findAllById(stock.getId());
            loop = loop + 1;
            if(flower.getMainCategory().equals("หลัก")){
                List<FlowerFormulaDetail> flowerList = this.flowerFormulaDetailRepository.findAllByFlowerIdAndQuantityLessThanEqualOrderByFlowerId(stock.getId(), stock.getRemainQuantity());
                if (flowerList.size() > 0) {
                    recalList:
                    for (FlowerFormulaDetail formulaDetail : flowerList) {
                        for (FlowerFormulaDetail flowerFormulaDetail : formulaDetails) {
                            if (formulaDetail.getFlowerFormula().getId().equals(flowerFormulaDetail.getFlowerFormula().getId())) {
                                continue recalList;
                            }
                        }
                        formulaDetails.add(formulaDetail);
                    }
                }
            }else{
                continue other;
            }
        }

        availableQuantitySum = 9999;
        String quantityFlower = null;
        Integer stockAvailable = 0;
        recalDup:
        for (FlowerFormulaDetail flowerFormulaDetail : formulaDetails) {
            List<FlowerFormulaDetail> formulaDetails1 = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(flowerFormulaDetail.getFlowerFormula().getId());
            chkSize = 0;
            stockAvailable = 0;
            int i = 0;
            for (FlowerFormulaDetail formulaDetail : formulaDetails1) {
                i = i +1;
                chkFlower:
                for (StockRemainDto stockRemainDto : stockRemainDtoList) {
                    if (stockRemainDto.getId().equals(formulaDetail.getFlower().getFlowerId())) {
                        chkSize = chkSize + 1;
                        availableQuantity = stockRemainDto.getRemainQuantity() / formulaDetail.getQuantity();
                        availableQuantitySum = Math.min(availableQuantitySum, availableQuantity);
                        lot = stockRemainDto.getLot();

                        floristId = stockRemainDto.getFloristId();
                        floristName = stockRemainDto.getFloristName();
                        long chkExp = date.getTime() - stockRemainDto.getLot().getTime();
                        int diffDays = (int) (chkExp / (24 * 60 * 60 * 1000));

                        //หา Life Time ของดอกไม้ที่ใกล้หมดอายุ และ ชนิดของดอกไม้
                        Flower chkFlower = this.flowerRepository.findAllById(stockRemainDto.getId());
                        flowerLifeTime = chkFlower.getLifeTime();
                        typeFlower = chkFlower.getFlowerType();

                        int expired = flowerLifeTime - diffDays;
                        List<PromotionProfit> promotionProfits = this.promotionProfitRepository.findAllByAgeAndFlowerType(expired, typeFlower);
                        for (PromotionProfit profit : promotionProfits) {
                            if (profit != null) {
                                profitFlower = profit.getProfit();
                            } else {
                                break;
                            }
                        }
                    } else {
                        continue chkFlower;
                    }
                }

                if (i > 1) {
                    List<Stock> stockList1 = this.stockRepository.findAllByFlowerIdAndLotGreaterThanEqualAndLotLessThanEqualAndFloristId(formulaDetail.getFlower().getFlowerId(),dateFrom, dateTo, floristId);
                    for(Stock stock: stockList1){
                        stockAvailable = stock.getQuantity();
                    }
                }
            }

            //Check Duplicate week
            List<Promotion> promotions = this.promotionRepository.findAllByDateGreaterThanAndDateLessThanEqual(dateTimeFrom, dateTimeTo);
            for(Promotion promotion: promotions){
                List<PromotionDetail> promotionDetailList = this.promotionDetailRepository.findAllByPromotionId(promotion.getId());
                for(PromotionDetail promotionDetail1: promotionDetailList){
                    if(flowerFormulaDetail.getFlowerFormula().getId().equals(promotionDetail1.getFlowerFormula().getId())){
                        continue recalDup;
                    }
                }
            }

            calProfitCurrent = flowerFormulaDetail.getFlowerFormula().getPrice() - ((flowerFormulaDetail.getFlowerFormula().getPrice() * profitFlower) / 100);
            Florist florist = this.floristRepository.findFloristById(floristId);
            if (availableQuantity > 0 && stockAvailable > 0) {
                FloristFee floristFee = this.floristFeeRepository.findFloristFeeByFloristIdAndSize(florist.getId(), flowerFormulaDetail.getFlowerFormula().getSize());
                int promotionPrice = (int) (calProfitCurrent + floristFee.getFee());
                if (promotionPrice % 100 != 0) {
                    promotionPrice = (promotionPrice - (promotionPrice % 100)) + 90;
                }else{
                    promotionPrice = promotionPrice + 90;
                }

                if (type.equals("getPromotion")) {
                    PromotionDetailLog promotionDetailLog = new PromotionDetailLog();
                    promotionDetailLog.setProfit((double) calProfitCurrent);
                    promotionDetailLog.setPrice((double) promotionPrice);
                    promotionDetailLog.setQuantity(availableQuantity);
                    promotionDetailLog.setStatus("active");
                    promotionDetailLog.setFlowerFormula(flowerFormulaDetail.getFlowerFormula());
                    promotionDetailLog.setExpiryDate(expiryDate);
                    promotionDetailLog.setPromotionType("remain");
                    promotionDetailLog.setFlorist(florist);
                    promotionDetailLog.setCreateDate(createtDate);
                    promotionDetailLog.setTotalProfit(availableQuantity * calProfitCurrent);
                    promotionDetailLog.setLotStock(lot);
                    this.promotionDetailLogRepository.saveAndFlush(promotionDetailLog);
                }else{
                    PromotionDetailLog promotionDetailLogs = this.promotionDetailLogRepository.findPromotionDetailLogsByStatusAndFlowerFormulaIdAndFloristId("active", flowerFormulaDetail.getFlowerFormula().getId(), florist.getId());
                    if(promotionDetailLogs != null){
                        promotionDetailLogs.setQuantity(availableQuantity);
                        promotionDetailLogs.setTotalProfit(availableQuantity * calProfitCurrent);
                        this.promotionDetailLogRepository.saveAndFlush(promotionDetailLogs);
                    }else{
                        PromotionDetailLog promotionDetailLog = new PromotionDetailLog();
                        promotionDetailLog.setProfit((double) calProfitCurrent);
                        promotionDetailLog.setPrice((double) promotionPrice);
                        promotionDetailLog.setQuantity(availableQuantity);
                        promotionDetailLog.setStatus("active");
                        promotionDetailLog.setFlowerFormula(flowerFormulaDetail.getFlowerFormula());
                        promotionDetailLog.setExpiryDate(expiryDate);
                        promotionDetailLog.setPromotionType("remain");
                        promotionDetailLog.setFlorist(florist);
                        promotionDetailLog.setCreateDate(createtDate);
                        promotionDetailLog.setTotalProfit(availableQuantity * calProfitCurrent);
                        promotionDetailLog.setLotStock(lot);
                        this.promotionDetailLogRepository.saveAndFlush(promotionDetailLog);
                    }
                }
            }
        }
    }

    @PostMapping("/recalculate")
    public List<PromotionDetail> getRecalculate() throws ParseException {

        Date date = new Date();
        int flowerLifeTime = 0;
        int profitFlower = 0;
        int numPromotion = 0;
        int availableQuantity = 0;
        int availableQuantitySum = 9999;
        int profitSum = 120;
        int profitFormula = 0;
        int available = 0;
        int availableTotal = 9999;
        int totalProfit = 0;
        int calProfitCurrent = 0;
        Date lot = null;
        String typeFlower = null;

        LocalDate currentDate = LocalDate.now();
        LocalDate dateTime = currentDate.minus(7, ChronoUnit.DAYS);
        Date dateReverse = dateFormat.parse(String.valueOf(dateTime));
        LocalDate createDate = currentDate.plus(0, ChronoUnit.DAYS);

        //For Check Duplicate
        LocalDate dateTime1 = currentDate.minus(14, ChronoUnit.DAYS);
        LocalDate dateTime2 = currentDate.minus(1, ChronoUnit.DAYS);
        LocalDate expireDate = currentDate.plus(2, ChronoUnit.DAYS);

        ZoneId zoneId = ZoneId.systemDefault();
        Date dateFrom = Date.from(dateTime1.atStartOfDay(zoneId).toInstant());
        Date dateTo = Date.from(dateTime2.atStartOfDay(zoneId).toInstant());
        Date expiryDate = Date.from(expireDate.atStartOfDay(zoneId).toInstant());
        Date createtDate = Date.from(createDate.atStartOfDay(zoneId).toInstant());

        //Get number of promotion
        List<Configurations> config = this.configurationsRepository.findAllByName("NUMBER_OF_PROMOTION");
        for (Configurations c: config){
            numPromotion = c.getValue();
        }

        List<StockRemainDto> stockRemainDtos = new ArrayList<>();
        List<Florist> florist = this.floristRepository.findAll();
        List<PromotionDetail> promotionDetails = new ArrayList<>();
        List<PromotionDetail> promotionDetailArrayList = new ArrayList<>();
        List<Stock> newStocks = new ArrayList<>();
        for (Florist florist1: florist) {
            List<Stock> stocks = this.stockRepository.findAllByFloristIdOrderByQuantityDesc(florist1.getId());
            List<Integer> newFlower = new ArrayList<>();

            //List ดอกไม้ที่เหลืออยู่ในสต๊อกที่ใกล้หมดอายุ
            outer:
            for (Stock stock : stocks) {
                long chkExp = date.getTime() - stock.getLot().getTime();
                int diffDays = (int) (chkExp / (24 * 60 * 60 * 1000));

                //หา Life Time ของดอกไม้ที่ใกล้หมดอายุ และ ชนิดของดอกไม้
                Flower flower = this.flowerRepository.findAllById(stock.getFlower().getFlowerId());
                flowerLifeTime = flower.getLifeTime();
                typeFlower = flower.getFlowerType();

                int expired = flowerLifeTime - diffDays;
                if (typeFlower.equals("ดอกไม้สด")) {
                    if (expired > 0 && expired <= 3) {
                        newStocks.add(stock);
                        StockRemainDto stockRemainDto = new StockRemainDto();
                        stockRemainDto.setId(stock.getFlower().getFlowerId());
                        stockRemainDto.setFlowerName(stock.getFlower().getFlowerName());
                        stockRemainDto.setRemainQuantity(stock.getQuantity());
                        stockRemainDto.setFloristId(stock.getFlorist().getId());
                        stockRemainDto.setFloristName(stock.getFlorist().getName());
                        stockRemainDto.setLot(stock.getLot());
                        stockRemainDtos.add(stockRemainDto);
                    } else {
                        continue outer;
                    }
                }else{
                    PromotionProfit profit = this.promotionProfitRepository.findAllByFlowerType("ดอกไม้แห้ง");
                    int ageFlower = profit.getAge();
                    int expiredFlower = (flowerLifeTime * ageFlower / 100);
                    if (expired > 0 && expired <= expiredFlower) {
                        newStocks.add(stock);
                        StockRemainDto stockRemainDto = new StockRemainDto();
                        stockRemainDto.setId(stock.getFlower().getFlowerId());
                        stockRemainDto.setFlowerName(stock.getFlower().getFlowerName());
                        stockRemainDto.setRemainQuantity(stock.getQuantity());
                        stockRemainDto.setFloristId(stock.getFlorist().getId());
                        stockRemainDto.setFloristName(stock.getFlorist().getName());
                        stockRemainDto.setLot(stock.getLot());
                        stockRemainDtos.add(stockRemainDto);
                    }
                }
            }
        }

        //Check ดอกไม้จาก promotion ที่ถูกเลือกเพื่อไม่นำมา calculate ใหม่
        List<PromotionDetail> promotionDetailList = this.promotionDetailRepository.findPromotionDetailsByStatus("active");
        recalPromotion:
        for (PromotionDetail promotionDetail: promotionDetailList){
            List<FlowerFormulaDetail> flowerList = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetail.getFlowerFormula().getId());
            for (StockRemainDto stockRemainDto1 : stockRemainDtos){
                for (FlowerFormulaDetail formulaDetail: flowerList){
                    if (stockRemainDto1.getId().equals(formulaDetail.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stockRemainDto1.getFloristId())){
                        int remains = stockRemainDto1.getRemainQuantity() - (formulaDetail.getQuantity() * promotionDetail.getQuantity());
                        stockRemainDto1.setRemainQuantity(remains);
                    }
                }
            }
        }

        for (Florist florist1: florist) {
            List<FlowerFormulaDetail> listFormula = new ArrayList<>();
            for (StockRemainDto stockRemainDto1 : stockRemainDtos) {
                //List flower formula detail
                List<FlowerFormulaDetail> flowerList = this.flowerFormulaDetailRepository.findAllByFlowerIdAndQuantityLessThanEqualOrderByFlowerId(stockRemainDto1.getId(), stockRemainDto1.getRemainQuantity());

                //Check Duplicate in List formula
                recalDup:
                for (FlowerFormulaDetail q : flowerList) {
                    for (FlowerFormulaDetail lf: listFormula) {
                        if (q.getFlowerFormula().getId().equals(lf.getFlowerFormula().getId())) {
                            continue recalDup;
                        }
                    }

                    //Check Duplicate week
                    List<Promotion> promotions = this.promotionRepository.findAllByDateGreaterThanAndDateLessThanEqual(dateFrom, dateTo);
                    for(Promotion promotion: promotions){
                        List<PromotionDetail> promotionDetails1 = this.promotionDetailRepository.findAllByPromotionId(promotion.getId());
                        for(PromotionDetail promotionDetail: promotionDetails1){
                            if(q.getFlowerFormula().getId().equals(promotionDetail.getFlowerFormula().getId())){
                                continue recalDup;
                            }
                        }
                    }

                    //ไม่นำ formula ที่ถูกเลือกเป็นโปรโมชั่นปัจจุบันมาแสดงอีก
                    for (PromotionDetail promotionDetail: promotionDetailList){
                        if(promotionDetail.getFlowerFormula().getId().equals(q.getFlowerFormula().getId())){
                            continue recalDup;
                        }
                    }

                    listFormula.add(q);
                }
            }

            int chkSize = 0;
            recalculateFormula:
            for (FlowerFormulaDetail list: listFormula){
                List<FlowerFormulaDetail> formulas = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(list.getFlowerFormula().getId());
                availableQuantitySum = 9999;
                profitSum = 120;
                chkSize = 0;
                for (FlowerFormulaDetail ff : formulas) {
                    chkFlower:
                    for (StockRemainDto stockRemainDto1 : stockRemainDtos) {
                        if (stockRemainDto1.getFloristId().equals(florist1.getId())){
                            if (ff.getFlower().getFlowerId().equals(stockRemainDto1.getId())) {
                                chkSize = chkSize + 1;
                                availableQuantity = stockRemainDto1.getRemainQuantity() / ff.getQuantity();
                                lot = stockRemainDto1.getLot();
                                long chkExp = date.getTime() - stockRemainDto1.getLot().getTime();
                                int diffDays = (int) (chkExp / (24 * 60 * 60 * 1000));

                                //หา Life Time ของดอกไม้ที่ใกล้หมดอายุ และ ชนิดของดอกไม้
                                Flower chkFlower = this.flowerRepository.findAllById(stockRemainDto1.getId());
                                flowerLifeTime = chkFlower.getLifeTime();
                                typeFlower = chkFlower.getFlowerType();

                                int expired = flowerLifeTime - diffDays;
                                List<PromotionProfit> promotionProfits = this.promotionProfitRepository.findAllByAgeAndFlowerType(expired, typeFlower);
                                for (PromotionProfit profit : promotionProfits) {
                                    if (profit != null) {
                                        profitFlower = profit.getProfit();
                                    } else {
                                        break;
                                    }
                                }
                            } else {

                                continue chkFlower;
                            }
                        }
                    }
                    availableQuantitySum = Math.min(availableQuantitySum, availableQuantity);
                    profitSum = Math.min(profitSum, profitFlower);
                    profitFormula = ff.getFlowerFormula().getPrice() - ((ff.getFlowerFormula().getPrice() * profitSum) / 100);
                    totalProfit = profitFormula * availableQuantitySum;

                }
                if (formulas.size() == chkSize && availableQuantitySum > 0){

                    Configurations configurations = this.configurationsRepository.getValueByName("MAX_QUANTITY_PROMOTION");
                    int maxQuantity = configurations.getValue();
                    if (availableQuantitySum > maxQuantity){
                        availableQuantitySum = maxQuantity;
                    }

                    PromotionDetail promotionDetail = new PromotionDetail();
                    promotionDetail.setStatus("active");
                    promotionDetail.setProfit((double) profitFormula);
                    promotionDetail.setQuantity(availableQuantitySum);
                    promotionDetail.setExpiryDate(expiryDate);
                    promotionDetail.setFlorist(florist1);
                    promotionDetail.setFlowerFormula(list.getFlowerFormula());
                    promotionDetail.setPrice(profitFormula);
                    promotionDetail.setLotStock(lot);
                    promotionDetails.add(promotionDetail);
                }else {
                    continue recalculateFormula;
                }
            }
        }

        int loop = 0;
        int maxTotal = 0;
        int remain = 0;
        int sizeFormula = 0;
        String flag1 = "Y";
        String flag2 = "Y";
        PromotionDetail promotionDetail = new PromotionDetail();
        sizeFormula = promotionDetails.size();
        for (int i = 0; i < sizeFormula; i++) {
            maxTotal = 0;
            for (PromotionDetail promotionDetail1 : promotionDetails) {
                int total = promotionDetail1.getQuantity() * promotionDetail1.getPrice();
                if (maxTotal < total) {
                    maxTotal = total;
                    promotionDetail = promotionDetail1;
                }
            }

            if (i == 0) {
                promotionDetailArrayList.add(promotionDetail);
                for (PromotionDetail promotionDetail2: promotionDetailArrayList){
                    List<FlowerFormulaDetail> formulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetail.getFlowerFormula().getId());
                    for(FlowerFormulaDetail formulaDetail: formulaDetails){
                        for(StockRemainDto stockRemainDto1 : stockRemainDtos){
                            if (stockRemainDto1.getId().equals(formulaDetail.getFlower().getFlowerId()) && promotionDetail2.getFlorist().getId().equals(stockRemainDto1.getFloristId())){
                                remain = stockRemainDto1.getRemainQuantity() - (formulaDetail.getQuantity() * promotionDetail2.getQuantity());
                                stockRemainDto1.setRemainQuantity(remain);
                            }
                        }
                    }
                }
                promotionDetails.remove(promotionDetail);
            }else{
                List<FlowerFormulaDetail> formulaDetails = this.flowerFormulaDetailRepository.findAllByFlowerFormulaId(promotionDetail.getFlowerFormula().getId());
                flag1 = "Y";
                flag2 = "Y";
                loop = 0;
                for(FlowerFormulaDetail formulaDetail: formulaDetails){
                    for(StockRemainDto stockRemainDto1 : stockRemainDtos){
                        if (stockRemainDto1.getId().equals(formulaDetail.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stockRemainDto1.getFloristId())){
                            loop = loop + 1;
                            remain = stockRemainDto1.getRemainQuantity() - (formulaDetail.getQuantity() * promotionDetail.getQuantity());
                            available = stockRemainDto1.getRemainQuantity() / formulaDetail.getQuantity();
                            if (remain < 0) {
                                flag1 = "N";
                            }else{
                                flag2 = "Y";
                            }
                        }
                    }

                    if ((flag1.equals("Y") && flag2.equals("Y")) && (formulaDetails.size() == loop)){
                        for(FlowerFormulaDetail formulaDetail1: formulaDetails) {
                            for (StockRemainDto stockRemainDto1 : stockRemainDtos) {
                                if (stockRemainDto1.getId().equals(formulaDetail1.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stockRemainDto1.getFloristId())) {
                                    remain = stockRemainDto1.getRemainQuantity() - (formulaDetail1.getQuantity() * promotionDetail.getQuantity());
                                    stockRemainDto1.setRemainQuantity(remain);
                                }
                            }
                        }
                    }

                    availableTotal = Math.min(availableTotal, available);
                    if (flag1.equals("N") || flag2.equals("N")) {
                        promotionDetail.setQuantity(availableTotal);
                        for(FlowerFormulaDetail formulaDetail2: formulaDetails) {
                            for (StockRemainDto stockRemainDto1 : stockRemainDtos) {
                                if (stockRemainDto1.getId().equals(formulaDetail2.getFlower().getFlowerId()) && promotionDetail.getFlorist().getId().equals(stockRemainDto1.getFloristId())) {
                                    remain = stockRemainDto1.getRemainQuantity() - (formulaDetail2.getQuantity() * promotionDetail.getQuantity());
                                    stockRemainDto1.setRemainQuantity(remain);
                                }
                            }
                        }
                    }
                }

                promotionDetailArrayList.add(promotionDetail);
                if (promotionDetail.getQuantity() < 1){
                    promotionDetailArrayList.remove(promotionDetail);
                }
                promotionDetails.remove(promotionDetail);

            }
        }

        List<PromotionDetailLog> promotionDetailLogs1 = this.promotionDetailLogRepository.findPromotionDetailLogsByStatus("active");
        for(PromotionDetailLog promotionDetailLog: promotionDetailLogs1){
            promotionDetailLog.setQuantity(0);
            promotionDetailLog.setTotalProfit(0);
            this.promotionDetailLogRepository.saveAndFlush(promotionDetailLog);
        }

        //outer:
        for(PromotionDetail promotionDetail1: promotionDetailArrayList){
            PromotionDetailLog promotionDetailLogs = this.promotionDetailLogRepository.findPromotionDetailLogsByStatusAndFlowerFormulaIdAndFloristId("active", promotionDetail1.getFlowerFormula().getId(), promotionDetail1.getFlorist().getId());
            if(promotionDetailLogs != null){
                promotionDetailLogs.setQuantity(promotionDetail1.getQuantity());
                promotionDetailLogs.setTotalProfit((int) (promotionDetail1.getQuantity() * promotionDetail1.getProfit()));
                promotionDetailLogs.setPromotionType("normal");
                this.promotionDetailLogRepository.saveAndFlush(promotionDetailLogs);
            }else{
                calProfitCurrent = promotionDetail1.getFlowerFormula().getPrice() - ((promotionDetail1.getFlowerFormula().getPrice() * profitFlower) / 100);
                FloristFee floristFee = this.floristFeeRepository.findFloristFeeByFloristIdAndSize(promotionDetail1.getFlorist().getId(), promotionDetail1.getFlowerFormula().getSize());
                int promotionPrice = (int) (calProfitCurrent + floristFee.getFee());
                if (promotionPrice % 100 != 0) {
                    promotionPrice = (promotionPrice - (promotionPrice % 100)) + 90;
                }else{
                    promotionPrice = promotionPrice + 90;
                }
                PromotionDetailLog promotionDetailLog = new PromotionDetailLog();
                promotionDetailLog.setProfit(promotionDetail1.getProfit());
                promotionDetailLog.setPrice((double)promotionPrice);
                promotionDetailLog.setQuantity(promotionDetail1.getQuantity());
                promotionDetailLog.setStatus("active");
                promotionDetailLog.setFlowerFormula(promotionDetail1.getFlowerFormula());
                promotionDetailLog.setExpiryDate(promotionDetail1.getExpiryDate());
                promotionDetailLog.setPromotionType("normal");
                promotionDetailLog.setFlorist(promotionDetail1.getFlorist());
                promotionDetailLog.setCreateDate(createtDate);
                promotionDetailLog.setTotalProfit((int) (promotionDetail1.getProfit() * promotionDetail1.getQuantity()));
                promotionDetailLog.setLotStock(promotionDetail1.getLotStock());
                this.promotionDetailLogRepository.saveAndFlush(promotionDetailLog);
                //continue outer;
            }

        }
        getPromotionSuggest(stockRemainDtos, "recalculate");
        return promotionDetailArrayList;
    }
}
