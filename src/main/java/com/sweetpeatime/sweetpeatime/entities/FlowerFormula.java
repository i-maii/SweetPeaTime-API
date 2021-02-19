package com.sweetpeatime.sweetpeatime.entities;

import javax.persistence.*;

@Entity
@Table(name="FlowerFormula")
public class FlowerFormula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String size;
    private String pattern;
    private Double price;

    public FlowerFormula(){}

    public FlowerFormula(Integer id, String name, String size, String pattern, Double price) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.pattern = pattern;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
