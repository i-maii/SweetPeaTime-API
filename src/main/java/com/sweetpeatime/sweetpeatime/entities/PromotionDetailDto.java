package com.sweetpeatime.sweetpeatime.entities;

public class PromotionDetailDto {
    private Integer id;
    private String formulaName;
    private String size;
    private Integer quantity;
    private Integer profit;
    private Integer totalProfit;
    private Integer price;
    private String locationName;
    private String image;

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

    public Integer getProfit() { return profit; }

    public void setProfit(Integer profit) {
        this.profit = profit;
    }

    public Integer getTotalProfit() { return totalProfit; }

    public void setTotalProfit(Integer totalProfit) {
        this.totalProfit = totalProfit;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
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
}
