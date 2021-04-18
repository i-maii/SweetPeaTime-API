package com.sweetpeatime.sweetpeatime.entities;

public class PromotionDetailCurrentDto {
    private Integer id;
    private String formulaName;
    private String size;
    private Integer quantity;
    private Double profit;
    private Integer totalProfit;
    private Double price;
    private String locationName;
    private String image;
    private String quantityFlower;
    private Integer stock;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFormulaName() {
        return formulaName;
    }

    public void setFormulaName(String formulaName) {
        this.formulaName = formulaName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getProfit() { return profit; }

    public void setProfit(Double profit) {
        this.profit = profit;
    }

    public Integer getTotalProfit() { return totalProfit; }

    public void setTotalProfit(Integer totalProfit) {
        this.totalProfit = totalProfit;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getQuantityFlower() {
        return quantityFlower;
    }

    public void setQuantityFlower(String quantityFlower) {
        this.quantityFlower = quantityFlower;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

}
