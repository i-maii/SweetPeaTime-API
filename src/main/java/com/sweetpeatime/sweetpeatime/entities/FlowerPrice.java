package com.sweetpeatime.sweetpeatime.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "FlowerPrice")
public class FlowerPrice {
    private Integer id;
    private Integer quantitySaleUnit;
    private String saleUnit;
    private Double price;
    private Date lot;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getLot() {
        return lot;
    }

    public void setLot(Date lot) {
        this.lot = lot;
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
