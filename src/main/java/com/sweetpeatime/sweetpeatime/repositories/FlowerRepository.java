package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.ConfigurationsPromotion;
import com.sweetpeatime.sweetpeatime.entities.Flower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FlowerRepository extends JpaRepository<Flower, Integer>{
    List<Flower> findAllById(Integer flowerId);
    List<Flower> findAllByIdAndFlowerType(Integer flowerId,String flowerType);
}
