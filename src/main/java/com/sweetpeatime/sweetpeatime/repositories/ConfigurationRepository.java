package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.Configurations;
import com.sweetpeatime.sweetpeatime.entities.PromotionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigurationRepository extends JpaRepository<Configurations, Integer> {

    Configurations findConfigurationsById(Integer id);
}
