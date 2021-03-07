package com.sweetpeatime.sweetpeatime.dto;

import com.sweetpeatime.sweetpeatime.entities.Florist;
import com.sweetpeatime.sweetpeatime.entities.Flower;

public class AddStockDTO {
    private Flower flower;
    private Integer quantity;
    private Integer price;
    private String lot;
    private Florist florist;

    public AddStockDTO(Flower flower, Integer quantity, Integer price, String lot, Florist florist) {
        this.flower = flower;
        this.quantity = quantity;
        this.price = price;
        this.lot = lot;
        this.florist = florist;
    }

    public Flower getFlower() {
        return flower;
    }

    public void setFlower(Flower flower) {
        this.flower = flower;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public Florist getFlorist() {
        return florist;
    }

    public void setFlorist(Florist florist) {
        this.florist = florist;
    }
}
