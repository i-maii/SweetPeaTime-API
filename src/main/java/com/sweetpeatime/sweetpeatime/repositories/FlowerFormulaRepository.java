package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.FlowerFormula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FlowerFormulaRepository extends JpaRepository<FlowerFormula, Integer> {

    FlowerFormula findFlowerFormulaById(Integer id);

    @Query(value = "SELECT ff.* FROM PromotionDetail pd LEFT JOIN FlowerFormula ff ON pd.flowerFormulaId = ff.id WHERE status = 'active' AND floristId = ?1", nativeQuery = true)
    List<FlowerFormula> findAllByFlowerFormulaId(Integer floristId);

    List<FlowerFormula> findAllById(Integer id);
    FlowerFormula findFlowerFormulaByName(String name);
}
