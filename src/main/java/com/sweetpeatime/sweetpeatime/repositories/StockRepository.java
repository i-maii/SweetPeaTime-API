package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import java.util.Date;

public interface StockRepository extends JpaRepository<Stock, Integer>{

    Stock findStockByFlowerIdAndFloristId(Integer flowerId, Integer floristId);

    List<Stock> findAllByFlowerIdAndFloristIdOrderByLotAsc(Integer flowerId, Integer floristId);

    Stock findAllByFlowerIdAndFloristIdOrderByLotDesc(Integer flowerId, Integer floristId);

    List<Stock> findStockByFlowerId(Integer flowerId);
    List<Stock> findAllByFlowerId(Integer flowerId);
    List<Stock> findAllByLotGreaterThanAndFloristId(Date lot, Integer floristId);
    List<Stock> findAllByFloristId(Integer floristId);

}
