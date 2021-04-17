package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.Florist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FloristRepository extends JpaRepository<Florist, Integer> {

    Florist findFloristById(Integer id);

    List<Florist> findAll();

    Florist findFloristByName(String name);
}
