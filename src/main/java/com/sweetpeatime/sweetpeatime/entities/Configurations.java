package com.sweetpeatime.sweetpeatime.entities;

import javax.persistence.*;

@Entity
@Table(name="Configurations")
public class Configurations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer value;
    public Configurations(){}

    public Configurations(Integer id, String name, Integer value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
    public Integer getValue() {
        return value;
    }
}
