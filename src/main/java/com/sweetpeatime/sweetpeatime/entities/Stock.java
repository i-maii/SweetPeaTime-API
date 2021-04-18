package com.sweetpeatime.sweetpeatime.entities;

import com.sweetpeatime.sweetpeatime.wrapper.StockWrapper;

import javax.persistence.*;
import java.util.Date;

@SqlResultSetMapping(
        name="stockMapping",
        classes={
                @ConstructorResult(
                        targetClass= StockWrapper.class,
                        columns={
                                @ColumnResult(name="flowerId", type = Integer.class),
                                @ColumnResult(name="floristId", type = Integer.class),
                                @ColumnResult(name="quantity", type = Integer.class)
                        }
                )
        }
)
@SqlResultSetMapping(
        name="stockLotMapping",
        classes={
                @ConstructorResult(
                        targetClass= StockLot.class,
                        columns={
                                @ColumnResult(name="id", type = Integer.class),
                                @ColumnResult(name="flowerId", type = Integer.class),
                                @ColumnResult(name="floristId", type = Integer.class),
                                @ColumnResult(name="quantity", type = Integer.class),
                                @ColumnResult(name="unit", type = String.class),
                                @ColumnResult(name="lot", type = Date.class),
                                @ColumnResult(name="deleteQty", type = Integer.class)
                        }
                )
        }
)
@Entity
@Table(name="Stock")
public class Stock {

    private Integer id;
    private Flower flower;
    private Integer quantity;
    private String unit;
    private Date lot;
    private Florist florist;
    private FlowerPrice flowerPrice;
    private Integer deleteQty;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(targetEntity= Flower.class)
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getLot() {
        return lot;
    }

    public void setLot(Date lot) {
        this.lot = lot;
    }

    @ManyToOne(targetEntity= Florist.class)
    @JoinColumn(name = "floristId", referencedColumnName = "id")
    public Florist getFlorist() {
        return florist;
    }

    public void setFlorist(Florist florist) {
        this.florist = florist;
    }

    @OneToOne(targetEntity = FlowerPrice.class)
    @JoinColumn(name = "flowerPriceId", referencedColumnName = "id")
    public FlowerPrice getFlowerPrice() {
        return flowerPrice;
    }

    public void setFlowerPrice(FlowerPrice flowerPrice) {
        this.flowerPrice = flowerPrice;
    }

    public Integer getDeleteQty() {
        return deleteQty;
    }

    public void setDeleteQty(Integer deleteQty) {
        this.deleteQty = deleteQty;
    }
}
