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
            "AND pd.expiryDate >= ?2 " +
            "AND pd.floristId = ?3 " +
            "AND pd.quantity != 0", nativeQuery = true)
    List<PromotionDetail> findOneByFlowerFormulaIdAndStatusAndExpiryDateAndFlorist(Integer flowerFormulaId, Date orderDate, Integer floristId);

    List<PromotionDetail> findAllByFlowerFormulaIdAndFloristId(Integer flowerFormulaId, Integer floristId);
    List<PromotionDetail> findPromotionDetailsByStatusAndQuantityGreaterThanOrderByTotalProfitDesc(String status, Integer quantity);

    List<PromotionDetail> findAllByPromotionId(Integer promotionId);
    PromotionDetail findOneByFlowerFormulaIdAndExpiryDate(Integer flowerFormulaId, LocalDateTime expiryDate);
    PromotionDetail findAllById(Integer id);
    List<PromotionDetail> findAllByStatus(String status);
    //PromotionDetail findAllByPromotionId(Integer promotionId);
}
