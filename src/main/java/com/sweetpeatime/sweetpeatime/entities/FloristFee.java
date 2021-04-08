package com.sweetpeatime.sweetpeatime.entities;

import javax.persistence.*;

@Entity
@Table(name="FloristFee")
public class FloristFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer preparationTime;
    private String size;
    private Integer fee;
    private Integer floristId;

    public FloristFee(){}

    public FloristFee(Integer id, Integer preparationTime, String size, Integer fee, Integer floristId) {
        this.id = id;
        this.preparationTime = preparationTime;
        this.size = size;
        this.fee = fee;
        this.floristId = floristId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.id = fee;
    }

    public Integer getFloristId() {
        return floristId;
    }

    public void setFloristId(Integer floristId) {
        this.floristId = floristId;
    }
}
