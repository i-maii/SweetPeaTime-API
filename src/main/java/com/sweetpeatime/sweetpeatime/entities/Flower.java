package com.sweetpeatime.sweetpeatime.entities;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;

@Entity
@Table(name="Flower")
public class Flower {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String flowerName;
    private String mainCategory;
    private Boolean isStock;
    private Integer lifeTime;
    private String unit;
    private Boolean isFreeze;
    private String flowerCategory;
    private String flowerType;
    private Integer capacity;

    public Flower(){}

    public Flower(Integer id, String flowerName, String mainCategory, Boolean isStock, Integer lifeTime, String unit, Boolean isFreeze, String flowerCategory, String flowerType, Integer capacity) {
        this.id = id;
        this.flowerName = flowerName;
        this.mainCategory = mainCategory;
        this.isStock = isStock;
        this.lifeTime = lifeTime;
        this.unit = unit;
        this.isFreeze = isFreeze;
        this.flowerCategory = flowerCategory;
        this.flowerType = flowerType;
        this.capacity = capacity;
    }

    public void setFlowerId(Integer id) {
        this.id = id;
    }

    public Integer getFlowerId() {
        return id;
    }

    public String getFlowerName() {
        return flowerName;
    }

    public void setFlowerName(String flowerName) {
        this.flowerName = flowerName;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public Boolean getIsStock() {
        return isStock;
    }

    public void setIsStock(Boolean isStock) {
        this.isStock = isStock;
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

    public Boolean getIsFreeze() {
        return isFreeze;
    }

    public void setIsFreeze(Boolean isFreeze) {
        this.isFreeze = isFreeze;
    }

    public String getFlowerCategory() {
        return flowerCategory;
    }

    public void setFlowerCategory(String flowerCategory) {
        this.flowerCategory = flowerCategory;
    }

    public String getFlowerType() {
        return flowerType;
    }

    public void setFlowerType(String flowerType) {
        this.flowerType = flowerType;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
