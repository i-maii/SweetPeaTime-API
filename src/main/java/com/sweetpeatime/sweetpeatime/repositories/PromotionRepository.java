package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    List<Promotion> findAllByDate(Date date);

    List<Promotion> findAllByDateGreaterThanAndDateLessThanEqual(Date dateFrom, Date dateTo);

    Promotion findFirstByOrderByDateDesc();
}
