package com.sweetpeatime.sweetpeatime.entities;

import javax.persistence.*;
import java.util.Date;

public class StockRemainDto {
    private Integer id;
    private String flowerName;
    private Integer remainQuantity;
    private Integer floristId;
    private String floristName;
    private Date lot;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFlowerName() {
        return flowerName;
    }

    public void setFlowerName(String flowerName) {
        this.flowerName = flowerName;
    }

    public Integer getRemainQuantity() {
        return remainQuantity;
    }

    public void setRemainQuantity(Integer remainQuantity) {
        this.remainQuantity = remainQuantity;
    }

    public Integer getFloristId() {
        return floristId;
    }

    public void setFloristId(Integer floristId) {
        this.floristId = floristId;
    }

    public String getFloristName() {
        return floristName;
    }

    public void setFloristName(String floristName) {
        this.floristName = floristName;
    }

    public Date getLot() {
        return lot;
    }

    public void setLot(Date lot) {
        this.lot = lot;
    }

}
