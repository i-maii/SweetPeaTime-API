package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.dto.AddStockDTO;
import com.sweetpeatime.sweetpeatime.dto.DeleteStockDTO;
import com.sweetpeatime.sweetpeatime.entities.FlowerPrice;
import com.sweetpeatime.sweetpeatime.entities.Stock;
import com.sweetpeatime.sweetpeatime.repositories.FloristRepository;
import com.sweetpeatime.sweetpeatime.repositories.FlowerPriceRepository;
import com.sweetpeatime.sweetpeatime.repositories.FlowerRepository;
import com.sweetpeatime.sweetpeatime.repositories.StockRepository;
import com.sweetpeatime.sweetpeatime.wrapper.StockWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @PersistenceContext
    EntityManager entityManager;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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

    @PostMapping("/deleteStock")
    public void deleteStockQuantity(@RequestBody List<DeleteStockDTO> deleteStock) {
        for (DeleteStockDTO ds: deleteStock) {
            List<Stock> stock = this.stockRepository.findByFlowerIdAndFloristIdOrderByLotAsc(ds.getFlowerId(), ds.getFloristId());
            Integer deleteQuantity = ds.getDeleteQuantity();
            for (Stock s: stock) {
                if (deleteQuantity > s.getQuantity()) {
                    s.setQuantity(0);
                    deleteQuantity = deleteQuantity - s.getQuantity();
                    this.stockRepository.saveAndFlush(s);
                } else if (deleteQuantity < s.getQuantity()) {
                    s.setQuantity(s.getQuantity() - deleteQuantity);
                    this.stockRepository.saveAndFlush(s);
                    return;
                } else {
                    s.setQuantity(0);
                    this.stockRepository.saveAndFlush(s);
                    return;
                }
            }
        }
    }

    @PostMapping("/addStock")
    public void addStockQuantity(@RequestBody List<AddStockDTO> addStock) throws ParseException {
        for (AddStockDTO s: addStock) {
            FlowerPrice flowerPrice = this.flowerPriceRepository.findByFlowerId(s.getFlower().getFlowerId());
            flowerPrice.setPrice(s.getPrice());
            this.flowerPriceRepository.saveAndFlush(flowerPrice);

            Stock stock = new Stock();
            stock.setFlower(s.getFlower());
            stock.setQuantity(s.getQuantity());
            stock.setLot(this.dateFormat.parse(s.getLot()));
            stock.setUnit(s.getFlower().getUnit());
            stock.setFlorist(s.getFlorist());
            stock.setFlowerPrice(flowerPrice);
            this.stockRepository.saveAndFlush(stock);
        }
    }
}
