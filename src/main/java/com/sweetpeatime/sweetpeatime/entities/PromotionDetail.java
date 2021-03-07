package com.sweetpeatime.sweetpeatime.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="PromotionDetail")
public class PromotionDetail {
    private Integer id;
    private Double profit;
    private Double price;
    private Integer quantity;
    private Integer quantitySold;
    private String status;
    private Promotion promotion;
    private FlowerFormula flowerFormula;
    private Florist florist;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double profit) {
        this.profit = profit;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(Integer quantitySold) {
        this.quantitySold = quantitySold;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotionId", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flowerFormulaId", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public FlowerFormula getFlowerFormula() {
        return flowerFormula;
    }

    public void setFlowerFormula(FlowerFormula flowerFormula) {
        this.flowerFormula = flowerFormula;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floristId", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public Florist getFlorist() {
        return florist;
    }

    public void setFlorist(Florist florist) {
        this.florist = florist;
    }
}
