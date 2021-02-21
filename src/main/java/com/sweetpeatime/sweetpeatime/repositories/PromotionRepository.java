package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    List<Promotion> findAllByDate(Date date);
}
