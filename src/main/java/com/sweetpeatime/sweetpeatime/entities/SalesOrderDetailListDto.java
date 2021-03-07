package com.sweetpeatime.sweetpeatime.entities;

import java.util.Date;
import java.util.List;

public class SalesOrderDetailListDto {
    private Integer id;
    private String customerName;
    private String customerLineFb;
    private String customerPhone;
    private Date date;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private Date receiveDateTime;
    private Integer flowerPrice;
    private Double deliveryFee;
    private Double totalPrice;
    private String note;
    private String status;
    List<SalesOrderDetail> salesOrderDetails;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerLineFb() {
        return customerLineFb;
    }

    public void setCustomerLineFb(String customerLineFb) {
        this.customerLineFb = customerLineFb;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
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

    public Date getReceiveDateTime() {
        return receiveDateTime;
    }

    public void setReceiveDateTime(Date receiveDateTime) {
        this.receiveDateTime = receiveDateTime;
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

    public List<SalesOrderDetail> getSalesOrderDetails() {
        return salesOrderDetails;
    }

    public void setSalesOrderDetails(List<SalesOrderDetail> salesOrderDetails) {
        this.salesOrderDetails = salesOrderDetails;
    }
}
