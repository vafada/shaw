package com.shaw.bean;

public class PartsBean {
    private String partNumber;
    private String description;
    private int quantity;
    private double unitPrice;
    private double extendedAmount;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getExtendedAmount() {
        return extendedAmount;
    }

    public void setExtendedAmount(double extendedAmount) {
        this.extendedAmount = extendedAmount;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
