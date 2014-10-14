package com.shaw.db;

import jxl.Workbook;
import jxl.Sheet;
import jxl.Cell;
import jxl.NumberCell;
import jxl.DateCell;
import jxl.CellType;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Collection;
import java.util.Iterator;

import com.shaw.bean.SalesBean;
import com.shaw.bean.SalesPartBean;
import com.shaw.manager.SalesManager;

public class SalesExcelParser {

    public static void main(String[] args) throws Exception {
        String filename = "";

        if (args.length != 1) {
            System.out.println("Warning: no path to file... trying sales.xls");
            filename = "sales.xls";
        } else {
            filename = args[0];
        }

        File excelFile = new File(filename);
        if (excelFile.exists() == false) {
            System.out.println("Error: File does not exists");
            System.exit(1);
        } else {
            Workbook workbook = Workbook.getWorkbook(excelFile);
            Sheet sheet = workbook.getSheet(0);
            parseExcel(sheet);

            workbook.close();
            System.out.println("Successfully parsed excel");
        }
    }

    private static void parseExcel(Sheet sheet) throws Exception {
        Map cache = new HashMap();

        int rowCount = sheet.getRows();
        System.out.println("Found " + (rowCount - 2) + " rows of data");

        for (int y = 2; y < rowCount; y++) {
            String soldTo = null;
            String invoiceNumber = null;
            Date date = null;

            SalesPartBean parts = new SalesPartBean();
            for (int x = 0; x < 9; x++) {
                Cell cell = sheet.getCell(x, y);
                switch (x) {
                    case 0: //date
                        DateCell dc = (DateCell) cell;
                        date = dc.getDate();
                        break;
                    case 1: //quantity
                        if (cell.getType() == CellType.NUMBER) {
                            NumberCell nc = (NumberCell) cell;
                            parts.setQuantity((int) nc.getValue());
                        } else {
                            parts.setQuantity(0);
                        }
                        break;
                    case 2: //part number
                        parts.setPartNumber(cell.getContents().trim());
                        break;
                    case 3: //sold to
                        soldTo = cell.getContents().trim();
                        break;
                    case 4: //invoice number
                        invoiceNumber = cell.getContents().trim();
                        break;
                    case 5: //unit cost
                        if (cell.getType() == CellType.NUMBER) {
                            NumberCell nc = (NumberCell) cell;
                            parts.setUnitCost(nc.getValue());
                        } else {
                            parts.setUnitCost(0);
                        }
                        break;
                    case 7: //unit sales
                        if (cell.getType() == CellType.NUMBER) {
                            NumberCell nc = (NumberCell) cell;
                            parts.setUnitSales(nc.getValue());
                        } else {
                            parts.setUnitSales(0);
                        }
                        break;
                }
            }

            String cacheKey = null;

            if (invoiceNumber.trim().length() == 0) {
                //no invoice number...
                //generate unique key
                StringBuffer key = new StringBuffer();
                key.append(date.toString()).append(":").append(soldTo);
                cacheKey = key.toString().trim();
            } else {
                cacheKey = invoiceNumber.trim();
            }
            if (cache.containsKey(cacheKey)) {
                SalesBean sales = (SalesBean) cache.get(cacheKey);
                sales.addParts(parts);
            } else {
                SalesBean sales = new SalesBean();
                sales.setInvoiceNumber(invoiceNumber.trim());
                sales.setSoldTo(soldTo.trim());
                sales.setDate(date);
                sales.addParts(parts);

                cache.put(cacheKey, sales);
            }
        }

        Collection list = cache.values();
        saveCollection(list);
    }

    private static void saveCollection(Collection list) throws Exception {
        HSQLConnection.getInstance().initialize();
        SalesManager salesManager = new SalesManager();

        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            SalesBean sales = (SalesBean) iter.next();
            salesManager.addSalesFromExcel(sales);
            System.out.println("Added sales with invoice number = " + sales.getInvoiceNumber());
        }
    }
}
