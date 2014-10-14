package com.shaw.bean;

import java.util.Date;
import java.util.List;

public class PartsInputBean {
    private int id;
    private String customerNumber;
    private String orderNumber;
    private String allocationNumber;
    private String paymentTerm;
    private String discount;
    private String surcharge;
    private Date date;
    private String invoiceNumber;
    private Date dateCreated;
    private List partsList;

    public List getPartsList() {
        return partsList;
    }

    public void setPartsList(List partsList) {
        this.partsList = partsList;
    }

    public String getAllocationNumber() {
        return allocationNumber;
    }

    public void setAllocationNumber(String allocationNumber) {
        this.allocationNumber = allocationNumber;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getPaymentTerm() {
        return paymentTerm;
    }

    public void setPaymentTerm(String paymentTerm) {
        this.paymentTerm = paymentTerm;
    }

    public String getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(String surcharge) {
        this.surcharge = surcharge;
    }
}
