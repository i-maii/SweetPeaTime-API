package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.EventPromotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventPromotionRepository extends JpaRepository<EventPromotion, Integer> {
}
