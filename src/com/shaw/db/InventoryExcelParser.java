package com.shaw.db;

import jxl.Workbook;
import jxl.Sheet;
import jxl.Cell;
import jxl.NumberCell;
import jxl.CellType;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.shaw.bean.ProductBean;

public class InventoryExcelParser {
    private static Connection dbconnection;

    public static void main(String[] args) throws Exception {
        String filename = "";

        if (args.length != 1) {
            System.out.println("Warning: no path to file... trying inventory.xls");
            filename = "inventory.xls";
        } else {
            filename = args[0];
        }

        File excelFile = new File(filename);
        if (excelFile.exists() == false) {
            System.out.println("Error: File does not exists");
            System.exit(1);
        } else {
            initDatabase();
            Workbook workbook = Workbook.getWorkbook(excelFile);
            Sheet sheet = workbook.getSheet(0);
            parseExcel(sheet);

            dbconnection.close();
            workbook.close();
            System.out.println("Successfully parsed excel");
        }
    }

    private static void initDatabase() throws Exception {
        Class.forName("org.hsqldb.jdbcDriver");
        dbconnection = DriverManager.getConnection("jdbc:hsqldb:data/shaw", "sa", "");
    }


    private static void parseExcel(Sheet sheet) throws Exception {
        int rowCount = sheet.getRows();
        System.out.println("Found " + (rowCount - 1) + " rows of data");
        dbconnection.setAutoCommit(false);

        for (int y = 1; y < rowCount; y++) {
            ProductBean product = new ProductBean();

            for (int x = 0; x < 20; x++) {
                Cell cell = sheet.getCell(x, y);
                switch (x) {
                    case 0: //part number
                        product.setPartNumber(cell.getContents().trim());
                        break;
                    case 1: //description
                        product.setDescription(cell.getContents().trim());
                        break;
                    case 2: //location
                        product.setLocation(cell.getContents().trim());
                        break;
                    case 3: //PCP
                        if (cell.getType() == CellType.NUMBER) {
                            NumberCell nc = (NumberCell) cell;
                            product.setPcp(nc.getValue());
                        } else {
                            product.setPcp(0);
                        }
                        break;
                    case 4: //quantity
                        if (cell.getType() == CellType.NUMBER) {
                            NumberCell nc = (NumberCell) cell;
                            product.setQuantity((int) nc.getValue());
                        } else {
                            product.setQuantity(0);
                        }
                        break;
                    case 5: //unit cost
                        if (cell.getType() == CellType.NUMBER) {
                            NumberCell nc = (NumberCell) cell;
                            product.setUnitPrice(nc.getValue());
                        } else {
                            product.setUnitPrice(0);
                        }
                        break;
                    case 17: //misc
                        product.setMiscelleneous(cell.getContents().trim());
                        break;
                    case 18: //comments
                        product.setComments(cell.getContents().trim());
                        break;
                    case 19: //other no
                        product.setOtherNumber(cell.getContents().trim());
                        break;
                }
            }
            if (product.getPartNumber().trim().length() > 0) {
                try {
                    insertParts(product);
                } catch(SQLException sqle) {
                    System.out.println("Error in row number = " + y);
                    throw sqle;
                }
            }
        }
        dbconnection.commit();
    }

    private static void insertParts(ProductBean product) throws SQLException {
        PreparedStatement stat = dbconnection.prepareStatement(INSERT_PRODUCT);
        int i = 1;
        stat.setString(i++, product.getPartNumber().trim());
        stat.setString(i++, product.getDescription().trim());
        stat.setString(i++, product.getLocation().trim());
        stat.setInt(i++, product.getQuantity());
        stat.setDouble(i++, product.getPcp());
        stat.setString(i++, product.getMiscelleneous().trim());
        stat.setString(i++, product.getOtherNumber().trim());
        stat.setDouble(i++, product.getUnitPrice());
        stat.setString(i++, product.getComments());

        stat.executeUpdate();
        System.out.println("Added part number " + product.getPartNumber().trim());

    }

    private static final String INSERT_PRODUCT = "INSERT INTO products (part_number, description, location, quantity, " +
            "pcp, misc, other_number, unit_price, comments) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
}
