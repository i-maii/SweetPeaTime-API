package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Integer>{

    Stock findStockByFlowerIdAndFloristId(Integer flowerId, Integer floristId);
    //Stock findStockByFlowerIdAndFloristId(Integer flowerId, Integer florist);
    List<Stock> findStockByFlowerId(Integer flowerId);
    List<Stock> findAllByFlowerId(Integer flowerId);
    List<Stock> findAllByLotGreaterThanAndFloristId(Date lot, Integer floristId);
    List<Stock> findAllByFloristId(Integer floristId);

}
