package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.FlowerFormula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FlowerFormulaRepository extends JpaRepository<FlowerFormula, Integer> {

    @Query(value = "SELECT quantityAvailable FROM FlowerFormula ff WHERE ff.id = ?1",
            nativeQuery = true)
    Integer getQuantityAvailable(Integer id);
}
