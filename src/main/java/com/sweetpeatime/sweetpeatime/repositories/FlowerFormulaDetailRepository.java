package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.FlowerFormulaDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlowerFormulaDetailRepository extends JpaRepository<FlowerFormulaDetail, Integer> {

    List<FlowerFormulaDetail> findAllByFlowerFormulaId(Integer flowerFormulaId);
    List<FlowerFormulaDetail> findAllByFlowerFormulaIdAndFlowerId(Integer flowerFormulaId, Integer flowerId);

    //List<FlowerFormulaDetail> findAllByFlowerId(Integer flowerId);
    //FlowerFormulaDetail findAllByFlowerIdAndQuantityLessThanEqual(Integer flowerId, Integer quantity);
    List<FlowerFormulaDetail> findAllByFlowerIdAndQuantityLessThanEqualOrderByQuantityDesc(Integer flowerId, Integer quantity);
    List<FlowerFormulaDetail> findAllByFlowerIdAndQuantityLessThanEqualOrderByFlowerId(Integer flowerId, Integer quantity);
    FlowerFormulaDetail findAllByFlowerId(Integer flowerId);
    //List<FlowerFormulaDetail> findAllByFlowerId(Integer flowerId);
}