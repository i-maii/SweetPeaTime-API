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
    private String customerName;
    private String customerLineFb;
    private String customerPhone;
    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;
    private String note;
    private Date deliveryDateTime;
    private Date receiverDateTime;
    private String status;
    private Double deliveryPrice;
    private Double totalPrice;

    public SalesOrder(){}

    public SalesOrder(Integer id, Date date, Integer price, String customerName, String customerLineFb, String customerPhone, String receiverName, String receiverAddress, String receiverPhone, String note, Date deliveryDateTime, Date receiverDateTime, String status, Double deliveryPrice, Double totalPrice) {
        this.id = id;
        this.date = date;
        this.price = price;
        this.customerName = customerName;
        this.customerLineFb = customerLineFb;
        this.customerPhone = customerPhone;
        this.receiverName = receiverName;
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
