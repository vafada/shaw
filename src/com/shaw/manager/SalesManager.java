package com.shaw.manager;

import com.shaw.dao.SalesDAO;
import com.shaw.bean.SalesBean;
import com.shaw.ShawException;
import com.shaw.db.ShawDbConnectionException;

import java.util.List;
import java.util.Date;

public class SalesManager {
    private SalesDAO salesDAO;

    public SalesManager() {
        salesDAO = new SalesDAO();
    }

    public boolean isUniqueInvoiceNumber(String invoiceNumber) throws ShawException {
        try {
            return salesDAO.isUniqueInvoiceNumber(invoiceNumber);
        } catch (ShawDbConnectionException sdbe) {
            throw new ShawException(sdbe.getMessage());
        }
    }

    public void addSalesFromExcel(SalesBean sales) throws ShawException {
        try {
            salesDAO.addSalesFromExcel(sales);
        } catch (ShawDbConnectionException sdbe) {
            throw new ShawException(sdbe.getMessage());
        }
    }

    public void addSales(SalesBean sales) throws ShawException {
        try {
            salesDAO.addSales(sales);
        } catch (ShawDbConnectionException sdbe) {
            throw new ShawException(sdbe.getMessage());
        }
    }

    public void cancelSales(int salesId) throws ShawException {
        try {
            salesDAO.cancelSales(salesId);
        } catch (ShawDbConnectionException sdbe) {
            throw new ShawException(sdbe.getMessage());
        }
    }

    public List getSalesByInvoice(String invoice) throws ShawException {
        try {
            return salesDAO.getSalesByInvoice(invoice);
        } catch (ShawDbConnectionException sdbe) {
            throw new ShawException(sdbe.getMessage());
        }
    }

    public List getSalesByCustomer(String customer) throws ShawException {
        try {
            return salesDAO.getSalesByCustomer(customer);
        } catch (ShawDbConnectionException sdbe) {
            throw new ShawException(sdbe.getMessage());
        }
    }

    public List getSales(Date date) throws ShawException {
        try {
            return salesDAO.getSales(date);
        } catch (ShawDbConnectionException sdbe) {
            throw new ShawException(sdbe.getMessage());
        }
    }

    public List getSales(int month, int year) throws ShawException {
        try {
            return salesDAO.getSales(month, year);
        } catch (ShawDbConnectionException sdbe) {
            throw new ShawException(sdbe.getMessage());
        }
    }

    public SalesBean getSalesReport(int salesId) throws ShawException {
        try {
            return salesDAO.getSalesReport(salesId);
        } catch (ShawDbConnectionException sdbe) {
            throw new ShawException(sdbe.getMessage());
        }
    }
}
