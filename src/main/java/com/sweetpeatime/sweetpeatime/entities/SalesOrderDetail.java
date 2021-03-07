package com.sweetpeatime.sweetpeatime.entities;

import javax.persistence.*;

@Entity
@Table(name="SalesOrderDetail")
public class SalesOrderDetail {

    private Integer id;
    private SalesOrder salesOrder;
    private FlowerFormula flowerFormula;
    private Integer quantity;
    private Florist florist;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(targetEntity= SalesOrder.class)
    @JoinColumn(name = "salesOrderId", referencedColumnName = "id")
    public SalesOrder getSalesOrder() {
        return salesOrder;
    }

    public void setSalesOrder(SalesOrder salesOrder) {
        this.salesOrder = salesOrder;
    }

    @ManyToOne(targetEntity= FlowerFormula.class)
    @JoinColumn(name = "flowerFormulaId", referencedColumnName = "id")
    public FlowerFormula getFlowerFormula() {
        return flowerFormula;
    }

    public void setFlowerFormula(FlowerFormula flowerFormula) {
        this.flowerFormula = flowerFormula;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @OneToOne(targetEntity= Florist.class)
    @JoinColumn(name = "floristId", referencedColumnName = "id")
    public Florist getFlorist() {
        return florist;
    }

    public void setFlorist(Florist florist) {
        this.florist = florist;
    }
}
