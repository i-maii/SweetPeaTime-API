package com.sweetpeatime.sweetpeatime.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="SalesOrder")
public class SalesOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Date date;
    private Double price;
    private String customerName;
    private String customerPhone;
    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;
    private String note;
    private Date deliveryDateTime;
    private Date receiverDateTime;
    private String status;
    private String deliveryPrice;

    public SalesOrder(){}

    public SalesOrder(Integer id, Date date, Double price, String customerName, String customerPhone, String receiverName, String receiverAddress, String receiverPhone, String note, Date deliveryDateTime, Date receiverDateTime, String status, String deliveryPrice) {
        this.id = id;
        this.date = date;
        this.price = price;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.receiverName = receiverName;
        this.receiverAddress = receiverAddress;
        this.receiverPhone = receiverPhone;
        this.note = note;
        this.deliveryDateTime = deliveryDateTime;
        this.receiverDateTime = receiverDateTime;
        this.status = status;
        this.deliveryPrice = deliveryPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getDeliveryDateTime() {
        return deliveryDateTime;
    }

    public void setDeliveryDateTime(Date deliveryDateTime) {
        this.deliveryDateTime = deliveryDateTime;
    }

    public Date getReceiverDateTime() {
        return receiverDateTime;
    }

    public void setReceiverDateTime(Date receiverDateTime) {
        this.receiverDateTime = receiverDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(String deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }
}
