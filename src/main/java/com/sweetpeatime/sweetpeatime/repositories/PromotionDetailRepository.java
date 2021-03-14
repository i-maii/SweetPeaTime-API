package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.PromotionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface PromotionDetailRepository extends JpaRepository<PromotionDetail, Integer> {
    List<PromotionDetail> findPromotionDetailsByStatus(String status);

    @Query(value = "SELECT * FROM PromotionDetail pd " +
            "WHERE pd.flowerFormulaId = ?1 " +
            "AND pd.status = 'active' " +
            "AND pd.expiryDate >= ?2", nativeQuery = true)
    List<PromotionDetail> findOneByFlowerFormulaIdAndStatusAndExpiryDate(Integer flowerFormulaId, Date orderDate);

    List<PromotionDetail> findAllByFlowerFormulaIdAndFloristId(Integer flowerFormulaId, Integer floristId);


    List<PromotionDetail> findAllByPromotionId(Integer promotionId);

    PromotionDetail findOneByFlowerFormulaIdAndExpiryDate(Integer flowerFormulaId, LocalDateTime expiryDate);

    PromotionDetail findAllById(Integer id);
}
