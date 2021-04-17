package com.sweetpeatime.sweetpeatime.entities;

import java.util.Date;

public class FlowerPriceDto {

    private int formulaId;
    private int floristId;
    private int totalOrder;
    private String receiveDate;

    public int getFormulaId() {
        return formulaId;
    }

    public void setFormulaId(int formulaId) {
        this.formulaId = formulaId;
    }

    public int getFloristId() {
        return floristId;
    }

    public void setFloristId(int floristId) {
        this.floristId = floristId;
    }

    public int getTotalOrder() {
        return totalOrder;
    }

    public void setTotalOrder(int totalOrder) {
        this.totalOrder = totalOrder;
    }

    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }
}
