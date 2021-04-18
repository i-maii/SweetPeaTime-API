package com.sweetpeatime.sweetpeatime.entities;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name="StockLot")
public class StockLot {
    private Integer id;
    private Integer flowerId;
    private Integer floristId;
    private Integer quantity;
    private String unit;
    private Date lot;
    private Integer deleteQty;


    public StockLot(Integer id, Integer flowerId, Integer floristId, Integer quantity, String unit, Date lot, Integer deleteQty) {
        this.id = id;
        this.flowerId = flowerId;
        this.floristId = floristId;
        this.quantity = quantity;
        this.unit = unit;
        this.lot = lot;
        this.deleteQty = deleteQty;

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() { return id; }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFlowerId() {
        return flowerId;
    }

    public void setFlowerId(Integer flowerId) {
        this.flowerId = flowerId;
    }

    public Integer getFloristId() {
        return floristId;
    }

    public void setFloristId(Integer floristId) {
        this.floristId = floristId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUnit() { return unit; }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getLot() { return lot; }

    public void setLot(Date unit) {
        this.lot = unit;
    }

    public Integer getDeleteQty() { return deleteQty; }

    public void setDeleteQty(Integer deleteQty) { this.deleteQty = deleteQty; }
}
