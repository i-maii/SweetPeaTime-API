package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.PromotionDetailLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PromotionDetailLogRepository extends JpaRepository<PromotionDetailLog, Integer> {

    List<PromotionDetailLog> findPromotionDetailLogsByPromotionDateAndPromotionTypeAndStatusOrderBySequenceAsc(Date promotionDate, String promotionType, String status);
}
