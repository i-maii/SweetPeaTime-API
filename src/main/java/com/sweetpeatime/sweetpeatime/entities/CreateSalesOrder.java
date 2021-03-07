package com.sweetpeatime.sweetpeatime.entities;

import java.util.Date;

public class CreateSalesOrder {
    private Integer id;
    private String orderFirstName;
    private String orderLastName;
    private String orderPhone;
    private Date orderDate;
    private String receiverFirstName;
    private String receiverLastName;
    private String receiverPhone;
    private String receiverAddress;
    private Date receiveDate;
    private Integer flowerFormula;
    private Integer flowerAvailable;
    private Integer flowerPrice;
    private Double deliveryFee;
    private Double totalPrice;
    private Integer florist;
    private String note;
    private String status;

    public CreateSalesOrder(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderFirstName() {
        return orderFirstName;
    }

    public void setOrderFirstName(String orderFirstName) {
        this.orderFirstName = orderFirstName;
    }

    public String getOrderLastName() {
        return orderLastName;
    }

    public void setOrderLastName(String orderLastName) {
        this.orderLastName = orderLastName;
    }

    public String getOrderPhone() {
        return orderPhone;
    }

    public void setOrderPhone(String orderPhone) {
        this.orderPhone = orderPhone;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getReceiverFirstName() {
        return receiverFirstName;
    }

    public void setReceiverFirstName(String receiverFirstName) {
        this.receiverFirstName = receiverFirstName;
    }

    public String getReceiverLastName() {
        return receiverLastName;
    }

    public void setReceiverLastName(String receiverLastName) {
        this.receiverLastName = receiverLastName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public Integer getFlowerFormula() {
        return flowerFormula;
    }

    public void setFlowerFormula(Integer flowerFormula) {
        this.flowerFormula = flowerFormula;
    }

    public Integer getFlowerAvailable() {
        return flowerAvailable;
    }

    public void setFlowerAvailable(Integer flowerAvailable) {
        this.flowerAvailable = flowerAvailable;
    }

    public Integer getFlowerPrice() {
        return flowerPrice;
    }

    public void setFlowerPrice(Integer flowerPrice) {
        this.flowerPrice = flowerPrice;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getFlorist() {
        return florist;
    }

    public void setFlorist(Integer florist) {
        this.florist = florist;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
