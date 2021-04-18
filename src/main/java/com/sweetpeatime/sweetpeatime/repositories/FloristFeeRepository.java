package com.sweetpeatime.sweetpeatime.repositories;

import com.sweetpeatime.sweetpeatime.entities.FloristFee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FloristFeeRepository extends JpaRepository<FloristFee, Integer> {

    FloristFee findAllByFloristIdAndSize(Integer floristId, String size);

    FloristFee findFloristFeeByFloristIdAndSize(Integer floristId,String size);


    FloristFee findFloristFeeByIdAndSize(Integer id, String size);
}
