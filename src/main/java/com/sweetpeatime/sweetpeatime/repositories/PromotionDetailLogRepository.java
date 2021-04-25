package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.PromotionDetailLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PromotionDetailLogRepository extends JpaRepository<PromotionDetailLog, Integer> {
    List<PromotionDetailLog> findPromotionDetailLogsByPromotionDateAndPromotionTypeAndStatusOrderBySequenceAsc(Date promotionDate, String promotionType, String status);
    PromotionDetailLog findPromotionDetailLogsByStatusAndFlowerFormulaIdAndFloristId(String status, Integer flowerFormulaId,Integer florist);
    PromotionDetailLog findPromotionDetailLogsByStatusAndFlowerFormulaId(String status, Integer flowerFormulaId);
    List<PromotionDetailLog> findPromotionDetailLogsByStatusAndCreateDateLessThanEqual(String status, Date createDate);
    List<PromotionDetailLog> findPromotionDetailLogsByStatusAndCreateDateLessThanEqualOrderByTotalProfitDesc(String status, Date createDate);

    List<PromotionDetailLog> findPromotionDetailLogsByStatusAndQuantityGreaterThanAndPromotionType(String status, Integer quantity, String promotionType);
    List<PromotionDetailLog> findPromotionDetailLogsByStatus(String status);
    List<PromotionDetailLog> findPromotionDetailLogsByStatusAndQuantityGreaterThanAndPromotionTypeOrderByLotStock(String status, Integer quantity, String promotionType);

    //List<PromotionDetailLog> findPromotionDetailLogsByStatusAndQuantityGreaterThanAndPromotionTypeOrderByLotStockTotalProfit(String status, Integer quantity, String promotionType);

    List<PromotionDetailLog> findPromotionDetailLogsByStatusOrderBySequenceAsc(String status);
}
