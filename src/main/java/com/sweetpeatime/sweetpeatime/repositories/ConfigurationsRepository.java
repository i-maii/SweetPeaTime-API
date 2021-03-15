package com.sweetpeatime.sweetpeatime.repositories;
import com.sweetpeatime.sweetpeatime.entities.Configurations;
import com.sweetpeatime.sweetpeatime.entities.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface ConfigurationsRepository extends JpaRepository< Configurations, Integer> {
    Configurations getValueByName(String name);
    List<Configurations> findAllByName(String name);
}
