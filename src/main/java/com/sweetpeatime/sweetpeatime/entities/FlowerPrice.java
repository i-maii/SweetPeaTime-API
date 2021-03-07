package com.sweetpeatime.sweetpeatime.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "FlowerPrice")
public class FlowerPrice {
    private Integer id;
    private Integer quantitySaleUnit;
    private String saleUnit;
    private Integer price;
    private Flower flower;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuantitySaleUnit() {
        return quantitySaleUnit;
    }

    public void setQuantitySaleUnit(Integer quantitySaleUnit) {
        this.quantitySaleUnit = quantitySaleUnit;
    }

    public String getSaleUnit() {
        return saleUnit;
    }

    public void setSaleUnit(String saleUnit) {
        this.saleUnit = saleUnit;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @ManyToOne(targetEntity = Flower.class)
    @JoinColumn(name = "flowerId", referencedColumnName = "id")
    public Flower getFlower() {
        return flower;
    }

    public void setFlower(Flower flower) {
        this.flower = flower;
    }
}
