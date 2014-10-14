package com.shaw.ui.admin;

import com.shaw.bean.ProductBean;
import com.shaw.Utility;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;

public class ProductTableModel extends AbstractTableModel {
    private static final String[] HEADERS = {"Part Number", "Description", "Location", "Quantity", "PCP", "Misc", "Other no.", "Unit Price"};
    private List data;

    public ProductTableModel() {
        this.data = new ArrayList();
    }

    public String getColumnName(int column) {
        return HEADERS[column];
    }

    public int getColumnCount() {
        return HEADERS.length;
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        ProductBean product = (ProductBean) data.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return product.getPartNumber();
            case 1:
                return product.getDescription();
            case 2:
                return product.getLocation();
            case 3:
                return String.valueOf(product.getQuantity());
            case 4:
                return String.valueOf(product.getPcp());
            case 5:
                return product.getMiscelleneous();
            case 6:
                return product.getOtherNumber();
            case 7:
                return Utility.formatDouble(product.getUnitPrice());
        }
        return null;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void setProductList(List productList) {
        this.data = productList;
        fireTableDataChanged();
    }

    public void addProduct(ProductBean product) {
        this.data.add(product);
        fireTableDataChanged();
    }

    public void updateProduct(int rowIndex, ProductBean product) {
        this.data.set(rowIndex, product);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public ProductBean getProduct(int rowIndex) {
        return (ProductBean) data.get(rowIndex);
    }

    public void removeProduct(int rowIndex) {
        data.remove(rowIndex);
        fireTableDataChanged();
    }

    public int getRowCount() {
        return data.size();
    }
}
