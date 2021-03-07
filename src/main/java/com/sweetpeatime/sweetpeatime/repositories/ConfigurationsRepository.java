package com.sweetpeatime.sweetpeatime.repositories;
import com.sweetpeatime.sweetpeatime.entities.Configurations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConfigurationsRepository extends JpaRepository< Configurations, Integer> {
    Configurations getValueByName(String name);
}
