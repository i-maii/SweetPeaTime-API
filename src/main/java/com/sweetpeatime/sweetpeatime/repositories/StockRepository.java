package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

import java.util.Date;

public interface StockRepository extends JpaRepository<Stock, Integer>{

    List<Stock> findAllByFlowerIdAndFloristIdOrderByLotAsc(Integer flowerId, Integer floristId);

    Stock findTopByFlowerIdAndFloristIdOrderByLotDesc(Integer flowerId, Integer floristId);

    List<Stock> findAllByFlowerId(Integer flowerId);
    List<Stock> findAllByFloristIdOrderByQuantityDesc(Integer floristId);

    List<Stock> findAllByFlowerIdAndLotGreaterThanEqualAndLotLessThanEqualAndFloristId(Integer flowerId,Date dateFrom, Date dateTo, Integer floristId);

    List<Stock> findAllByQuantityLessThanAndFlowerIdOrderByLotAsc(Integer quantity, Integer flowerId);
    Stock findStockByFlowerIdAndLotAndFloristId(Integer flowerId, Date lot, Integer floristId);
}
