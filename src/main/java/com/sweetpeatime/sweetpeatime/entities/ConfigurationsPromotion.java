package com.sweetpeatime.sweetpeatime.entities;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ConfigurationsPromotion")
public class ConfigurationsPromotion {
    private Integer id;
    private String flowerType;
    private Integer lifeTime;
    private String unit;
    private Integer percentProfit;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFlowerType() {
        return flowerType;
    }

    public void setFlowerType(String flowerType) {
        this.flowerType = flowerType;
    }

    public Integer getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(Integer lifeTime) {
        this.lifeTime = lifeTime;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getPercentProfit() {
        return percentProfit;
    }

    public void setPercentProfit(Integer percentProfit) {
        this.percentProfit = percentProfit;
    }
}
