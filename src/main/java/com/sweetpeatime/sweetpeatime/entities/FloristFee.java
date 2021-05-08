package com.sweetpeatime.sweetpeatime.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "FloristFee")
public class FloristFee {
    private Integer id;
    private Integer preparationTime;
    private String size;
    private Integer fee;
    private Florist florist;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
        this.fee = fee;
    }

    @OneToOne(targetEntity = Florist.class)
    @JoinColumn(name = "floristId", referencedColumnName = "id")
    public Florist getFlorist() {
        return florist;
    }

    public void setFlorist(Florist florist) {
        this.florist = florist;
    }
}
