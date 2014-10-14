package com.shaw.bean;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;

public class SalesBean {
    private String invoiceNumber;
    private String purchaseOrderNumber;
    private String restCertNumber;

    private Date dateIssued;
    private String placeIssued;
    private String tin;

    private String soldTo;
    private Date date;
    private String address;

    private String terms;
    private String remarks;
    private String receivedBy;

    private double totalCost = 0;
    private double totalSales = 0;

    private List partsList;

    public List getPartsList() {
        return partsList;
    }

    public void setPartsList(List partsList) {
        this.partsList = partsList;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(Date dateIssued) {
        this.dateIssued = dateIssued;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getPlaceIssued() {
        return placeIssued;
    }

    public void setPlaceIssued(String placeIssued) {
        this.placeIssued = placeIssued;
    }

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRestCertNumber() {
        return restCertNumber;
    }

    public void setRestCertNumber(String restCertNumber) {
        this.restCertNumber = restCertNumber;
    }

    public String getSoldTo() {
        return soldTo;
    }

    public void setSoldTo(String soldTo) {
        this.soldTo = soldTo;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }

    public void addParts(SalesPartBean parts) {
        if (this.partsList == null) {
            this.partsList = new ArrayList();
        }
        this.partsList.add(parts);

        totalCost += (parts.getUnitCost() * parts.getQuantity());
        totalSales += (parts.getUnitSales() * parts.getQuantity());
    }
}

