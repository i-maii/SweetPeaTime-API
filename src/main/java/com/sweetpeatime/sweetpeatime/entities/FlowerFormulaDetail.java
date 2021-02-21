package com.sweetpeatime.sweetpeatime.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="FlowerFormulaDetail")
public class FlowerFormulaDetail implements Serializable{

    private Integer id;
    private FlowerFormula flowerFormula;
    private Flower flower;
    private Integer quantity;

    public FlowerFormulaDetail(){}

    public FlowerFormulaDetail(Integer id, FlowerFormula flowerFormula, Flower flower, Integer quantity) {
        this.id = id;
        this.flowerFormula = flowerFormula;
        this.flower = flower;
        this.quantity = quantity;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(targetEntity= FlowerFormula.class)
    @JoinColumn(name = "flowerFormulaId", referencedColumnName = "id")
    public FlowerFormula getFlowerFormula() {
        return flowerFormula;
    }

    public void setFlowerFormula(FlowerFormula flowerFormula) {
        this.flowerFormula = flowerFormula;
    }

    @OneToOne(targetEntity= Flower.class)
    @JoinColumn(name = "flowerId", referencedColumnName = "id")
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
}
