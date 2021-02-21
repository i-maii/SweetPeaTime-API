package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.SalesOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesOrderDetailRepository extends JpaRepository<SalesOrderDetail, Integer> {

    SalesOrderDetail findAllBySalesOrderId(Integer salesOderId);
}
