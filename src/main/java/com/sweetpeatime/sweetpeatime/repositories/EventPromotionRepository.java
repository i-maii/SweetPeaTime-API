package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.EventPromotion;
import com.sweetpeatime.sweetpeatime.entities.Florist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface EventPromotionRepository extends JpaRepository<EventPromotion, Integer> {
    List<EventPromotion> findAllByStartDateGreaterThanEqualAndEndDateLessThan(Date startDate, Date endDate);
    List<EventPromotion> findAll();
}
