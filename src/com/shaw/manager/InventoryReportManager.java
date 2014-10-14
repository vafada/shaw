package com.shaw.manager;

import com.shaw.dao.InventoryReportDAO;
import com.shaw.ShawException;
import com.shaw.bean.ProductBean;
import com.shaw.bean.InventoryReportBean;
import com.shaw.db.ShawDbConnectionException;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Collections;
import java.util.TreeMap;

public class InventoryReportManager {
    private InventoryReportDAO inventoryReportDAO;

    public InventoryReportManager() {
        inventoryReportDAO = new InventoryReportDAO();
    }

    public InventoryReportBean getInventoryReportOfProduct(String partNumber, Date startDate, Date endDate) throws ShawException {
        return (InventoryReportBean) searchInventoryReport(startDate, endDate, partNumber, true).get(0);
    }

    public List getInventoryReport(Date startDate, Date endDate) throws ShawException {
        return searchInventoryReport(startDate, endDate, null, false);
    }

    public List searchInventoryReport(Date startDate, Date endDate, String search, boolean strictSearch) throws ShawException {
        Map monthIndex = new HashMap();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        long time = startDate.getTime();
        long endTime = endDate.getTime();
        int index = 0;

        do {
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);

            monthIndex.put(String.valueOf(month + "-" + year), new Integer(index));
            index++;

            calendar.add(Calendar.MONTH, 1);
            time = calendar.getTime().getTime();

        } while (time <= endTime);

        List retVal = new ArrayList();
        //Map cache = new HashMap();
        Map cache = new TreeMap();
        try {
            List list = null;
            //get all invetory report
            if(search == null)
                list = inventoryReportDAO.getInventoryReport(startDate, endDate);
            else {
                //get inventory report of procudt
                if(strictSearch) {
                    list = inventoryReportDAO.getInventoryReportOfProduct(search, startDate, endDate);
                } else {
                    //search for a given string
                    list = inventoryReportDAO.searchInventoryReport(startDate, endDate, search);
                }
            }

            Iterator iter = list.iterator();
            while (iter.hasNext()) {
                InventoryReportBean reportBean = null;
                ProductBean product = (ProductBean) iter.next();
                //check cache
                if (cache.containsKey(product.getPartNumber())) {
                    //product already in cache
                    reportBean = (InventoryReportBean) cache.get(product.getPartNumber());
                    if (product.getTransactionDate() != null) {
                        calendar.setTime(product.getTransactionDate());

                        int month = calendar.get(Calendar.MONTH) + 1;
                        int year = calendar.get(Calendar.YEAR);

                        Integer arrayIndex = (Integer) monthIndex.get(String.valueOf(month + "-" + year));
                        reportBean.addQuanity(arrayIndex.intValue(), product.getQuanitySold());
                    }
                } else {
                    //new product
                    reportBean = new InventoryReportBean(product);
                    if (product.getTransactionDate() != null) {
                        calendar.setTime(product.getTransactionDate());

                        int month = calendar.get(Calendar.MONTH) + 1;
                        int year = calendar.get(Calendar.YEAR);

                        Integer arrayIndex = (Integer) monthIndex.get(String.valueOf(month + "-" + year));
                        reportBean.addQuanity(arrayIndex.intValue(), product.getQuanitySold());
                    }
                }
                cache.put(product.getPartNumber(), reportBean);
            }
        } catch (ShawDbConnectionException sdbe) {
            throw new ShawException(sdbe.getMessage());
        }

        retVal.addAll(cache.values());

        return retVal;
    }

}
