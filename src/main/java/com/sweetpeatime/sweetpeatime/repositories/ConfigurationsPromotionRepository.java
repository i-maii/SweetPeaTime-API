package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.ConfigurationsPromotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ConfigurationsPromotionRepository extends JpaRepository<ConfigurationsPromotion, Integer> {

    List<ConfigurationsPromotion> findAllByLifeTimeAndFlowerType(Integer lifeTime,String flowerType);

}
