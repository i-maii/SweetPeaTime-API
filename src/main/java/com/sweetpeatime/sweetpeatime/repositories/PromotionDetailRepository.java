package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.PromotionDetail;
import com.sweetpeatime.sweetpeatime.entities.SalesOrder;
import com.sweetpeatime.sweetpeatime.entities.SalesOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionDetailRepository extends JpaRepository<PromotionDetail, Integer> {
    List<PromotionDetail> findPromotionDetailsByStatus(String status);
    //PromotionDetail findPromotionDetailsById(Integer promotionId);
    //PromotionDetail findAllById(Integer id);
    PromotionDetail findAllById(Integer id);
}
