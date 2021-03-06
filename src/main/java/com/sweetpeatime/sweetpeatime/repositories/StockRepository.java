package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Integer>{

    Stock findStockByFlowerIdAndFloristId(Integer flowerId, Integer floristId);

    List<Stock> findByFlowerIdAndFloristIdOrderByLotAsc(Integer flowerId, Integer floristId);
}
