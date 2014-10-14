package com.shaw.ui.sales;

import com.shaw.bean.SalesPartBean;
import com.shaw.Utility;

import javax.swing.table.AbstractTableModel;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class SalesTableModel extends AbstractTableModel {
    private static final String[] HEADERS = {"Item", "Part Number", "Description", "Quantity", "Remaining", "Unit Cost", "Ext. Cost", "Unit Sales", "Total"};
    private List productList;

    private JTextField totalCost;
    private double cost = 0;

    private JTextField totalSales;
    private double sales = 0;

    public SalesTableModel(JTextField cost, JTextField sales) {
        this.productList = new ArrayList();
        this.totalCost = cost;
        this.totalSales = sales;
    }

    public String getColumnName(int column) {
        return HEADERS[column];
    }

    public int getColumnCount() {
        return HEADERS.length;
    }

    public int getRowCount() {
        return productList.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        SalesPartBean product = (SalesPartBean) productList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return String.valueOf(rowIndex + 1);
            case 1:
                return product.getPartNumber();
            case 2:
                return product.getDescription();
            case 3:
                return String.valueOf(product.getQuantity());
            case 4:
                return String.valueOf(product.getRemaining());
            case 5:
                return Utility.formatDouble(product.getUnitCost());
            case 6:
                return Utility.formatDouble(product.getUnitCost() * product.getQuantity());
            case 7:
                return Utility.formatDouble(product.getUnitSales());
            case 8:
                return Utility.formatDouble(product.getUnitSales() * product.getQuantity());
        }
        return null;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public int indexOf(String partNumber) {
        int retVal = -1;

        for (int i = 0; i < productList.size(); i++) {
            SalesPartBean parts = (SalesPartBean) productList.get(i);
            if (parts.getPartNumber().trim().equalsIgnoreCase(partNumber.trim())) {
                return i;
            }
        }

        return retVal;
    }


    public void addParts(SalesPartBean product) {
        this.productList.add(product);
        int row = productList.size();
        fireTableRowsInserted(row, row);

        cost += (product.getUnitCost() * product.getQuantity());
        sales += (product.getUnitSales() * product.getQuantity());

        totalCost.setText(Utility.formatDouble(cost));
        totalSales.setText(Utility.formatDouble(sales));
    }

    public void updateParts(int rowIndex, SalesPartBean product) {
        SalesPartBean oldProduct = (SalesPartBean) productList.get(rowIndex);
        this.productList.set(rowIndex, product);
        fireTableRowsUpdated(rowIndex, rowIndex);

        cost = cost - (oldProduct.getUnitCost() * oldProduct.getQuantity()) + (product.getUnitCost() * product.getQuantity());
        sales = sales - (oldProduct.getUnitSales() * oldProduct.getQuantity()) + (product.getUnitSales() * product.getQuantity());


        totalCost.setText(Utility.formatDouble(cost));
        totalSales.setText(Utility.formatDouble(sales));

    }

    public SalesPartBean getParts(int rowIndex) {
        return (SalesPartBean) productList.get(rowIndex);
    }

    public void removeParts(int rowIndex) {
        SalesPartBean oldProduct = (SalesPartBean) productList.get(rowIndex);

        productList.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);

        cost -= (oldProduct.getUnitCost() * oldProduct.getQuantity());
        sales -= (oldProduct.getUnitSales() * oldProduct.getQuantity());

        totalCost.setText(Utility.formatDouble(cost));
        totalSales.setText(Utility.formatDouble(sales));
    }

    public List getParts() {
        return productList;
    }

    public void setParts(List parts) {
        this.productList = parts;
        fireTableDataChanged();
    }

    public void resetData() {
        cost = 0;
        sales = 0;
        productList.clear();
        fireTableDataChanged();
    }

    public double getCost() {
        return cost;
    }

    public double getSales() {
        return sales;
    }
}

