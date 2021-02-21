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
    private Integer price;
    private String customerFirstName;
    private String customerLastName;
    private String customerPhone;
    private String receiverFirstName;
    private String receiverLastName;
    private String receiverAddress;
    private String receiverPhone;
    private String note;
    private Date deliveryDateTime;
    private Date receiverDateTime;
    private String status;
    private Double deliveryPrice;
    private Double totalPrice;

    public SalesOrder(){}

    public SalesOrder(Integer id, Date date, Integer price, String customerFirstName, String customerLastName, String customerPhone, String receiverFirstName, String receiverLastName, String receiverAddress, String receiverPhone, String note, Date deliveryDateTime, Date receiverDateTime, String status, Double deliveryPrice, Double totalPrice) {
        this.id = id;
        this.date = date;
        this.price = price;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.customerPhone = customerPhone;
        this.receiverFirstName = receiverFirstName;
        this.receiverLastName = receiverLastName;
        this.receiverAddress = receiverAddress;
        this.receiverPhone = receiverPhone;
        this.note = note;
        this.deliveryDateTime = deliveryDateTime;
        this.receiverDateTime = receiverDateTime;
        this.status = status;
        this.deliveryPrice = deliveryPrice;
        this.totalPrice = totalPrice;
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
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

    public Double getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(Double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
