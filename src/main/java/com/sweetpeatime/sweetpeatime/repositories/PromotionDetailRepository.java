package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.PromotionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionDetailRepository extends JpaRepository<PromotionDetail, Integer> {
    List<PromotionDetail> findPromotionDetailsByStatus(String status);
}
