package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.PromotionDetail;
import com.sweetpeatime.sweetpeatime.entities.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Integer> {

    SalesOrder findAllById(Integer id);

    List<SalesOrder> findAllByReceiverDateTimeGreaterThanEqualAndReceiverDateTimeLessThanAndStatus(Date receiverDateTimeFrom, Date receiverDateTimeTo, String status);

}
