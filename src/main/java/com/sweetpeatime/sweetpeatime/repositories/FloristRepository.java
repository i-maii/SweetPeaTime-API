package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.Florist;
import com.sweetpeatime.sweetpeatime.entities.FloristFee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FloristRepository extends JpaRepository<Florist, Integer> {

    Florist findFloristById(Integer id);
}
