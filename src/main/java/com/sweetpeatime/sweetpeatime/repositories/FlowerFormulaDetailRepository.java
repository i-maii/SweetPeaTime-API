package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.FlowerFormulaDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlowerFormulaDetailRepository extends JpaRepository<FlowerFormulaDetail, Integer> {

    List<FlowerFormulaDetail> findAllByFlowerFormulaId(Integer flowerFormulaId);
}
