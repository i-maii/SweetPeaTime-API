package com.sweetpeatime.sweetpeatime.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "PromotionDetailLog")
public class PromotionDetailLog {
    private Integer id;
    private Double profit;
    private Double price;
    private Integer quantity;
    private Integer quantitySold;
    private String status;
    private Promotion promotion;
    private FlowerFormula flowerFormula;
    private Integer sequence;
    private Date expiryDate;
    private String promotionType;
    private Florist florist;
    private Date createDate;
    private Date lotStock;
    private Integer totalProfit;


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

    @OneToOne(targetEntity = Promotion.class)
    @JoinColumn(name = "promotionId", referencedColumnName = "id")
    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    @OneToOne(targetEntity = FlowerFormula.class)
    @JoinColumn(name = "flowerFormulaId", referencedColumnName = "id")
    public FlowerFormula getFlowerFormula() {
        return flowerFormula;
    }

    public void setFlowerFormula(FlowerFormula flowerFormula) {
        this.flowerFormula = flowerFormula;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(String promotionType) {
        this.promotionType = promotionType;
    }

    @ManyToOne(targetEntity= Florist.class)
    @JoinColumn(name = "floristId", referencedColumnName = "id")
    public Florist getFlorist() {
        return florist;
    }

    public void setFlorist(Florist florist) {
        this.florist = florist;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLotStock() {
        return lotStock;
    }

    public void setLotStock(Date lotStock) {
        this.lotStock = lotStock;
    }

    public Integer getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(Integer totalProfit) {
        this.totalProfit = totalProfit;
    }

}
