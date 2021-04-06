package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.FloristFee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FloristFeeRepository extends JpaRepository<FloristFee, Integer> {

    public FloristFee findAllByFloristIdAndSize(Integer floristId, String size);
}
