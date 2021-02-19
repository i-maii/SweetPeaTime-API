package com.sweetpeatime.sweetpeatime.entities;

import javax.persistence.*;

@Entity
@Table(name="Florist")
public class Florist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String address;
    private String locationName;

    public Florist(){}

    public Florist(Integer id, String name, String address, String locationName) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.locationName = locationName;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
