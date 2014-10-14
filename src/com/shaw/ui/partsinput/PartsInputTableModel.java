package com.shaw.ui.partsinput;

import com.shaw.bean.PartsBean;
import com.shaw.Utility;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;

public class PartsInputTableModel extends AbstractTableModel {
    private static final String[] HEADERS = {"Item", "Part Number", "Description", "Quantity", "Unit Price", "Extended Amount"};
    private List productList;

    public PartsInputTableModel() {
        this.productList = new ArrayList();
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
        PartsBean product = (PartsBean) productList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return String.valueOf(rowIndex+1);
            case 1:
                return product.getPartNumber();
            case 2:
                return product.getDescription();
            case 3:
                return Utility.formatDouble(product.getQuantity());
            case 4:
                return Utility.formatDouble(product.getUnitPrice());
            case 5:
                return Utility.formatDouble(product.getExtendedAmount());
        }
        return null;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public int indexOf(String partNumber) {
        int retVal = -1;

        for(int i=0; i<productList.size(); i++) {
            PartsBean parts = (PartsBean) productList.get(i);
            if(parts.getPartNumber().trim().equalsIgnoreCase(partNumber.trim())) {
                return i;
            }
        }

        return retVal;
    }


    public void addParts(PartsBean product) {
        this.productList.add(product);
        int row = productList.size();
        fireTableRowsInserted(row, row);
    }

    public void updateParts(int rowIndex, PartsBean product) {
        this.productList.set(rowIndex, product);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public PartsBean getParts(int rowIndex) {
        return (PartsBean) productList.get(rowIndex);
    }

    public void removeParts(int rowIndex) {
        productList.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public List getParts() {
        return productList;
    }

    public void resetData() {
        productList.clear();
        fireTableDataChanged();
    }
}

