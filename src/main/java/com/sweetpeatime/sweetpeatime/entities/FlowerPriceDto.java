package com.sweetpeatime.sweetpeatime.entities;

public class FlowerPriceDto {

    private Integer formulaId;
    private Integer floristId;
    private Integer totalOrder;
    private String receiveDate;

    public Integer getFormulaId() {
        return formulaId;
    }

    public void setFormulaId(Integer formulaId) {
        this.formulaId = formulaId;
    }

    public Integer getFloristId() {
        return floristId;
    }

    public void setFloristId(Integer floristId) {
        this.floristId = floristId;
    }

    public Integer getTotalOrder() {
        return totalOrder;
    }

    public void setTotalOrder(Integer totalOrder) {
        this.totalOrder = totalOrder;
    }

    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }
}
