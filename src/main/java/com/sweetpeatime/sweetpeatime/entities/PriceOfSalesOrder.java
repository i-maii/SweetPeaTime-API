package com.sweetpeatime.sweetpeatime.entities;

public class PriceOfSalesOrder {
    private Double flowerPrice;
    private Double feePrice;
    private Double totalPrice;

    public PriceOfSalesOrder(){}

    public Double getFlowerPrice() {
        return flowerPrice;
    }

    public void setFlowerPrice(Double flowerPrice) {
        this.flowerPrice = flowerPrice;
    }

    public Double getFeePrice() {
        return feePrice;
    }

    public void setFeePrice(Double feePrice) {
        this.feePrice = feePrice;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
