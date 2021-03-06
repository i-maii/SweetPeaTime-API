package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.FlowerFormula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FlowerFormulaRepository extends JpaRepository<FlowerFormula, Integer> {


    @Query(value = "SELECT ff.price FROM FlowerFormula ff WHERE ff.id = ?1",
            nativeQuery = true)
    Integer getFlowerPrice(Integer id);

    FlowerFormula findFlowerFormulaById(Integer id);

    @Query(value = "SELECT ff.* FROM PromotionDetail pd LEFT JOIN FlowerFormula ff ON pd.flowerFormulaId = ff.id WHERE status = 'active'", nativeQuery = true)
    List<FlowerFormula> findAllByFlowerFormulaId();

    List<FlowerFormula> findAllById(Integer id);
}
