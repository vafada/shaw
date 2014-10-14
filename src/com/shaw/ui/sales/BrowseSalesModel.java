package com.shaw.ui.sales;

import com.shaw.bean.BrowseSalesBean;
import com.shaw.Utility;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

public class BrowseSalesModel extends AbstractTableModel {
    private static final String[] HEADER = {"Date", "Invoice Number", "Customer", "Total Cost", "Total Sales"};
    private List data;

    public BrowseSalesModel() {
        this.data = new ArrayList();
    }

    public int getRowCount() {
        return data.size();
    }

    public int getColumnCount() {
        return HEADER.length;
    }

    public String getColumnName(int column) {
        return HEADER[column];
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        BrowseSalesBean sales = (BrowseSalesBean) data.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return Utility.formatDate(sales.getDate());
            case 1:
                return sales.getInvoiceNumber();
            case 2:
                return sales.getSoldTo();
            case 3:
                return Utility.formatDouble(sales.getTotalCost());
            case 4:
                return Utility.formatDouble(sales.getTotalSales());
        }
        return "";
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public int salesId(int rowIndex) {
        BrowseSalesBean sales = (BrowseSalesBean) data.get(rowIndex);
        return sales.getId();
    }

    public void setData(List data) {
        this.data = data;
        fireTableDataChanged();
    }

    public double getTotalCost() {
        double total = 0.0;
        Iterator iter = data.iterator();
        while (iter.hasNext()) {
            BrowseSalesBean sales = (BrowseSalesBean) iter.next();
            total += sales.getTotalCost();
        }
        return total;
    }

    public double getTotalSales() {
        double total = 0.0;
        Iterator iter = data.iterator();
        while (iter.hasNext()) {
            BrowseSalesBean sales = (BrowseSalesBean) iter.next();
            total += sales.getTotalSales();
        }
        return total;
    }
}
