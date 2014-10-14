package com.shaw;

import jxl.Workbook;
import jxl.Sheet;
import jxl.SheetSettings;
import jxl.format.Alignment;
import jxl.write.WritableWorkbook;
import jxl.write.WritableSheet;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.NumberFormat;
import jxl.write.NumberFormats;

import java.io.File;
import java.util.List;
import java.util.Iterator;

import com.shaw.bean.DateRangeWrapper;
import com.shaw.bean.InventoryReportBean;
import com.shaw.manager.InventoryReportManager;

public class ExcelBuilder {
    private static String[] headers = {"Part no.", "Desc.", "Loc.", "PCP", "Qty", "Unit cost", "Total cost", "?", "?", "?", "?"
                                       , "?", "?", "?", "6MT", "MAD", "Freq.", "Comments", "Misc", "Other no."};

    private static WritableCellFormat cellformat = new WritableCellFormat();
    private static WritableCellFormat numberCellFormat;// = new WritableCellFormat(fivedps);

    public static void exportInventory(String filename) throws ShawException {


        DateRangeWrapper dateRange = Utility.getLastSevenMonths();
        InventoryReportManager manager = new InventoryReportManager();

        String[] names = dateRange.getNames();

        for (int i = 0; i < 7; i++)
            headers[13 - i] = names[i];

        try {
            WritableWorkbook workbook = Workbook.createWorkbook(new File(filename));
            WritableSheet sheet = workbook.createSheet("inventory", 0);
            sheet.getSettings().setDefaultColumnWidth(15);

            numberCellFormat = new WritableCellFormat(NumberFormats.FORMAT7);
            cellformat.setAlignment(Alignment.CENTRE);
            //generate header
            generateHeaders(sheet);
            //generate data
            List list = manager.getInventoryReport(dateRange.getStartDate(), dateRange.getEndDate());
            generateData(sheet, list);
            //now write it to the disk
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ShawException("Error exporting to excel");
        }
    }

    private static void generateData(WritableSheet sheet, List data) throws Exception {
        int i = 1;
        Iterator iter = data.iterator();
        while (iter.hasNext()) {
            InventoryReportBean inventoryBean = (InventoryReportBean) iter.next();
            Label label = new Label(0, i, inventoryBean.getProduct().getPartNumber());
            sheet.addCell(label);
            label = new Label(1, i, inventoryBean.getProduct().getDescription());
            label.setCellFormat(cellformat);
            sheet.addCell(label);
            label = new Label(2, i, inventoryBean.getProduct().getLocation());
            label.setCellFormat(cellformat);
            sheet.addCell(label);
            jxl.write.Number number = new jxl.write.Number(3, i, inventoryBean.getProduct().getPcp());
            number.setCellFormat(numberCellFormat);
            sheet.addCell(number);
            number = new jxl.write.Number(4, i, inventoryBean.getProduct().getQuantity());
            sheet.addCell(number);
            number = new jxl.write.Number(5, i, inventoryBean.getProduct().getUnitPrice());
            number.setCellFormat(numberCellFormat);
            sheet.addCell(number);
            number = new jxl.write.Number(6, i, inventoryBean.getProduct().getUnitPrice() * inventoryBean.getProduct().getQuantity());
            number.setCellFormat(numberCellFormat);
            sheet.addCell(number);

            for (int y = 0; y <= 6; y++) {
                String quantity = inventoryBean.getQuantity(y);
                if ("-".equalsIgnoreCase(quantity)) {
                    label = new Label(y+7, i, inventoryBean.getQuantity(y));
                    label.setCellFormat(cellformat);
                    sheet.addCell(label);
                } else {
                    number = new jxl.write.Number(y+7, i, Integer.parseInt(quantity));
                    sheet.addCell(number);
                }
            }

            /*

            label = new Label(8, i, inventoryBean.getQuantity(1));
            label.setCellFormat(cellformat);
            sheet.addCell(label);
            label = new Label(9, i, inventoryBean.getQuantity(2));
            label.setCellFormat(cellformat);
            sheet.addCell(label);
            label = new Label(10, i, inventoryBean.getQuantity(3));
            label.setCellFormat(cellformat);
            sheet.addCell(label);
            label = new Label(11, i, inventoryBean.getQuantity(4));
            label.setCellFormat(cellformat);
            sheet.addCell(label);
            label = new Label(12, i, inventoryBean.getQuantity(5));
            label.setCellFormat(cellformat);
            sheet.addCell(label);
            label = new Label(13, i, inventoryBean.getQuantity(6));
            label.setCellFormat(cellformat);
            sheet.addCell(label);
            */

            number = new jxl.write.Number(14, i, inventoryBean.getTotal());
            sheet.addCell(number);
            number = new jxl.write.Number(15, i, inventoryBean.getTotal() / 6);
            sheet.addCell(number);
            number = new jxl.write.Number(16, i, inventoryBean.getFrequency());
            sheet.addCell(number);
            label = new Label(17, i, inventoryBean.getProduct().getComments());
            label.setCellFormat(cellformat);
            sheet.addCell(label);
            label = new Label(18, i, inventoryBean.getProduct().getMiscelleneous());
            label.setCellFormat(cellformat);
            sheet.addCell(label);
            label = new Label(19, i, inventoryBean.getProduct().getOtherNumber());
            label.setCellFormat(cellformat);
            sheet.addCell(label);
            i++;
        }
    }

    private static void generateHeaders(WritableSheet sheet) throws Exception {
        for (int i = 0; i < headers.length; i++) {
            Label label = new Label(i, 0, headers[i]);
            label.setCellFormat(cellformat);
            sheet.addCell(label);
        }
    }
}
