package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.FlowerPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlowerPriceRepository extends JpaRepository<FlowerPrice, Integer> {

    FlowerPrice findAllByFlowerId(Integer flowerId);
}
