package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.PromotionProfit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionProfitRepository extends JpaRepository<PromotionProfit, Integer> {
    List<PromotionProfit> findAllByAgeAndFlowerType(Integer age, String flowerType);
}
