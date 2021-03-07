package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.FlowerPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlowerPriceRepository extends JpaRepository<FlowerPrice, Integer> {
    FlowerPrice findByFlowerId(Integer flowerId);
}
