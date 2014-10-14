package com.shaw.bean;

import java.util.Date;

public class ProductBean {
    private String partNumber;
    private String description;
    private String location;
    private int quantity;
    private double pcp;
    private String miscelleneous;
    private String otherNumber;
    private String comments;

    private double unitPrice;

    //for inventory reports;
    private Date transactionDate;
    private int quanitySold;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getQuanitySold() {
        return quanitySold;
    }

    public void setQuanitySold(int quanitySold) {
        this.quanitySold = quanitySold;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getMiscelleneous() {
        return miscelleneous;
    }

    public void setMiscelleneous(String miscelleneous) {
        this.miscelleneous = miscelleneous;
    }

    public String getOtherNumber() {
        return otherNumber;
    }

    public void setOtherNumber(String otherNumber) {
        this.otherNumber = otherNumber;
    }

    public double getPcp() {
        return pcp;
    }

    public void setPcp(double pcp) {
        this.pcp = pcp;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
