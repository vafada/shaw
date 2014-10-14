package com.shaw.bean;

public class SalesPartBean {
    private String partNumber;
    private int quantity;
    private int remaining;
    private String description;
    private double unitCost;
    private double unitSales;

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public double getUnitSales() {
        return unitSales;
    }

    public void setUnitSales(double unitSales) {
        this.unitSales = unitSales;
    }


}
