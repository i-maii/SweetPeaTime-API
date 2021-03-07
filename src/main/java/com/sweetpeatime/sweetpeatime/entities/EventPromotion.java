package com.sweetpeatime.sweetpeatime.entities;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="EventPromotion")
public class EventPromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String promotionName;
    private Integer percentOfProfit;
    private Date startDate;
    private Date endDate;
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public Integer getPercentOfProfit() {
        return percentOfProfit;
    }

    public void setPercentOfProfit(Integer percentOfProfit) {
        this.percentOfProfit = percentOfProfit;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
