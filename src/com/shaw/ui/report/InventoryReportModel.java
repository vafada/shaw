package com.shaw.ui.report;

import com.shaw.bean.InventoryReportBean;
import com.shaw.bean.DateRangeWrapper;
import com.shaw.manager.ProductManager;
import com.shaw.ShawException;
import com.shaw.Utility;

import javax.swing.table.AbstractTableModel;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class InventoryReportModel extends AbstractTableModel {
    private String[] headers = {"Part no.", "Desc.", "Loc.", "PCP", "Qty", "Unit cost", "Total cost", "?", "?", "?", "?"
                                , "?", "?", "?", "6MT", "MAD", "Freq.", "Comments", "Misc", "Other no."};

    private Date startDate;
    private Date endDate;

    private List allItemsList;
    private List madList;
    private List data;

    public InventoryReportModel() {
        this.madList = new ArrayList();
        data = new ArrayList();

        DateRangeWrapper dateRange = Utility.getLastSevenMonths();
        startDate = dateRange.getStartDate();
        endDate = dateRange.getEndDate();

        String[] names = dateRange.getNames();

        for (int i = 0; i < 7; i++)
            headers[13 - i] = names[i];
    }

    public String getColumnName(int column) {
        return headers[column];
    }


    public int getColumnCount() {
        return headers.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        InventoryReportBean inventoryBean = (InventoryReportBean) data.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return inventoryBean.getProduct().getPartNumber();
            case 1:
                return inventoryBean.getProduct().getDescription();
            case 2:
                return inventoryBean.getProduct().getLocation();
            case 3:
                return String.valueOf(inventoryBean.getProduct().getPcp());
            case 4:
                return String.valueOf(inventoryBean.getProduct().getQuantity());
            case 5:
                return String.valueOf(inventoryBean.getProduct().getUnitPrice());
            case 6:
                return Utility.formatDouble(inventoryBean.getProduct().getUnitPrice() * inventoryBean.getProduct().getQuantity());
            case 7:
                return inventoryBean.getQuantity(0);
            case 8:
                return inventoryBean.getQuantity(1);
            case 9:
                return inventoryBean.getQuantity(2);
            case 10:
                return inventoryBean.getQuantity(3);
            case 11:
                return inventoryBean.getQuantity(4);
            case 12:
                return inventoryBean.getQuantity(5);
            case 13:
                return inventoryBean.getQuantity(6);
            case 14:
                return String.valueOf(inventoryBean.getTotal());
            case 15:
                return String.valueOf(inventoryBean.getTotal() / 6);
            case 16:
                return String.valueOf(inventoryBean.getFrequency());
            case 17:
                return inventoryBean.getProduct().getComments();
            case 18:
                return inventoryBean.getProduct().getMiscelleneous();
            case 19:
                return inventoryBean.getProduct().getOtherNumber();

        }
        return null;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public List getInventory() {
        return data;
    }

    public void setInventory(List inventory) {
        this.allItemsList = inventory;
        this.data = this.allItemsList;


        this.madList.clear();
        Iterator iter = inventory.iterator();
        while (iter.hasNext()) {
            InventoryReportBean reportBean = (InventoryReportBean) iter.next();
            if (reportBean.isMAD()) {
                this.madList.add(reportBean);
            }
        }
        fireTableDataChanged();
    }

    public void showMAD() {
        this.data = this.madList;
        fireTableDataChanged();
    }

    public void setSearchList(List searchList) {
        this.data = searchList;
        fireTableDataChanged();
    }

    public void showAll() {
        this.data = this.allItemsList;
        fireTableDataChanged();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 17)
            return true;
        return false;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 17) {
            InventoryReportBean product = (InventoryReportBean) data.get(rowIndex);
            String comments = (String) aValue;
            ProductManager manager = new ProductManager();
            try {
                manager.updateComments(product.getProduct().getPartNumber(), comments);
                product.getProduct().setComments(comments);
            } catch (ShawException se) {
                //cannot update in transit
                se.printStackTrace();
            }
        }
    }

    public int getRowCount() {
        return data.size();
    }
}
