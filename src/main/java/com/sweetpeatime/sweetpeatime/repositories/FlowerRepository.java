package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.Flower;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlowerRepository extends JpaRepository<Flower, Integer>{
    Flower findOneById(Integer id);
    Flower findAllById(Integer flowerId);
    List<Flower> findAllByIdAndFlowerType(Integer flowerId,String flowerType);
}
