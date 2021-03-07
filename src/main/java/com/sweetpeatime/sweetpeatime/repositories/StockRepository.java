package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Integer>{

    Stock findStockByFlowerIdAndFloristId(Integer flowerId, Integer floristId);

    List<Stock> findAllByFlowerIdAndFloristIdOrderByLotAsc(Integer flowerId, Integer floristId);

    Stock findAllByFlowerIdAndFloristIdOrderByLotDesc(Integer flowerId, Integer floristId);
}
