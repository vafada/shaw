package com.shaw.dao;

import com.shaw.bean.SalesBean;
import com.shaw.bean.SalesPartBean;
import com.shaw.bean.BrowseSalesBean;
import com.shaw.db.ShawDbConnectionException;
import com.shaw.db.HSQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class SalesDAO {
    public boolean isUniqueInvoiceNumber(String invoiceNumber) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;
        ResultSet rs = null;

        try {
            stat = conn.prepareStatement(CHECK_EXISTING);
            stat.setString(1, invoiceNumber);
            rs = stat.executeQuery();

            if(rs.next()) {
                int count = rs.getInt(1);

                if(count > 0)
                    return false;
                else
                    return true;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new ShawDbConnectionException(sqle.getMessage());
        } finally {
            try {
                stat.close();
                rs.close();
            } catch (SQLException e) {

            }
        }
        return true;
    }

    public void addSalesFromExcel(SalesBean sales) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;

        try {
            conn.setAutoCommit(false);
            stat = conn.prepareStatement(ADD_SALES_FROM_EXCEL);
            int i = 1;
            stat.setString(i++, sales.getInvoiceNumber());
            stat.setString(i++, sales.getSoldTo());
            stat.setDate(i++, new java.sql.Date(sales.getDate().getTime()));
            stat.setDouble(i++, sales.getTotalCost());
            stat.setDouble(i++, sales.getTotalSales());
            stat.executeUpdate();

            Iterator iter = sales.getPartsList().iterator();
            while (iter.hasNext()) {
                SalesPartBean parts = (SalesPartBean) iter.next();
                stat = conn.prepareStatement(ADD_SALES_PRODUCTS);
                i = 1;
                stat.setString(i++, parts.getPartNumber());
                stat.setInt(i++, parts.getQuantity());
                stat.setDouble(i++, parts.getUnitCost());
                stat.setDouble(i++, parts.getUnitSales());
                stat.setDate(i++, new java.sql.Date(sales.getDate().getTime()));
                stat.executeUpdate();
                /*
                stat = conn.prepareStatement(UPDATE_INVENTORY_PRODUCTS);
                i = 1;
                stat.setInt(i++, parts.getQuantity());
                stat.setString(i++, parts.getPartNumber().trim());
                stat.executeUpdate();
                */
            }

            conn.commit();

        } catch (SQLException sqle) {
            try {
                conn.rollback();
            } catch (SQLException rollback) {
                rollback.printStackTrace();
            }
            sqle.printStackTrace();
            throw new ShawDbConnectionException(sqle.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
                stat.close();
            } catch (SQLException e) {

            }
        }
    }

    public void cancelSales(int salesId) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;
        ResultSet rs = null;

        try {
            conn.setAutoCommit(false);

            stat = conn.prepareStatement(GET_SALES_PRODUCT);
            stat.setInt(1, salesId);
            rs = stat.executeQuery();

            while(rs.next()) {
                String partNumber = rs.getString("part_number");
                int quantity = rs.getInt("quantity");

                stat = conn.prepareStatement(ADD_INVENTORY_PRODUCTS);
                stat.setInt(1, quantity);
                stat.setString(2, partNumber);
                stat.executeUpdate();
            }

            stat = conn.prepareStatement(CANCEL_SALES_PRODUCT);
            stat.setInt(1, salesId);
            stat.executeUpdate();

            stat = conn.prepareStatement(CANCEL_SALES);
            stat.setInt(1, salesId);
            stat.executeUpdate();

            conn.commit();
        } catch (SQLException sqle) {
            try {
                conn.rollback();
            } catch (SQLException rollback) {
                rollback.printStackTrace();
            }
            sqle.printStackTrace();
            throw new ShawDbConnectionException(sqle.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
                stat.close();
                rs.close();
            } catch (SQLException e) {

            }
        }
    }

    public void addSales(SalesBean sales) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;

        try {
            conn.setAutoCommit(false);

            stat = conn.prepareStatement(ADD_SALES);
            int i = 1;
            stat.setString(i++, sales.getInvoiceNumber());
            stat.setString(i++, sales.getPurchaseOrderNumber());
            stat.setString(i++, sales.getRestCertNumber());
            stat.setDate(i++, new java.sql.Date(sales.getDateIssued().getTime()));
            stat.setString(i++, sales.getPlaceIssued());
            stat.setString(i++, sales.getTin());
            stat.setString(i++, sales.getSoldTo());
            stat.setDate(i++, new java.sql.Date(sales.getDate().getTime()));
            stat.setString(i++, sales.getAddress());
            stat.setString(i++, sales.getTerms());
            stat.setString(i++, sales.getRemarks());
            stat.setString(i++, sales.getReceivedBy());
            stat.setDouble(i++, sales.getTotalCost());
            stat.setDouble(i++, sales.getTotalSales());
            stat.executeUpdate();

            Iterator iter = sales.getPartsList().iterator();
            while (iter.hasNext()) {
                SalesPartBean parts = (SalesPartBean) iter.next();
                stat = conn.prepareStatement(ADD_SALES_PRODUCTS);
                i = 1;
                stat.setString(i++, parts.getPartNumber());
                stat.setInt(i++, parts.getQuantity());
                stat.setDouble(i++, parts.getUnitCost());
                stat.setDouble(i++, parts.getUnitSales());
                stat.setDate(i++, new java.sql.Date(sales.getDate().getTime()));
                stat.executeUpdate();

                stat = conn.prepareStatement(UPDATE_INVENTORY_PRODUCTS);
                i = 1;
                stat.setInt(i++, parts.getQuantity());
                stat.setString(i++, parts.getPartNumber().trim());
                stat.executeUpdate();
            }

            conn.commit();

        } catch (SQLException sqle) {
            try {
                conn.rollback();
            } catch (SQLException rollback) {
                rollback.printStackTrace();
            }
            sqle.printStackTrace();
            throw new ShawDbConnectionException(sqle.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
                stat.close();
            } catch (SQLException e) {

            }
        }
    }

    public List getSales(int month, int year) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;
        ResultSet rs = null;
        List salesList = new ArrayList();

        try {

            stat = conn.prepareStatement(GET_SALES);
            stat.setInt(1, month);
            stat.setInt(2, year);
            rs = stat.executeQuery();
            while (rs.next()) {
                salesList.add(getBrowseSalesBean(rs));
            }
        } catch (SQLException sqle) {
            try {
                conn.rollback();
            } catch (SQLException rollback) {
                rollback.printStackTrace();
            }
            sqle.printStackTrace();
            throw new ShawDbConnectionException(sqle.getMessage());
        } finally {
            try {
                rs.close();
                stat.close();
            } catch (SQLException e) {

            }
        }

        return salesList;
    }

    public List getSales(Date date) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;
        ResultSet rs = null;
        List salesList = new ArrayList();

        try {

            stat = conn.prepareStatement(GET_SALES_BY_DATE);
            stat.setDate(1, new java.sql.Date(date.getTime()));
            rs = stat.executeQuery();
            while (rs.next()) {
                salesList.add(getBrowseSalesBean(rs));
            }
        } catch (SQLException sqle) {
            try {
                conn.rollback();
            } catch (SQLException rollback) {
                rollback.printStackTrace();
            }
            sqle.printStackTrace();
            throw new ShawDbConnectionException(sqle.getMessage());
        } finally {
            try {
                rs.close();
                stat.close();
            } catch (SQLException e) {

            }
        }

        return salesList;
    }

    public List getSalesByInvoice(String invoice) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;
        ResultSet rs = null;
        List salesList = new ArrayList();

        try {

            stat = conn.prepareStatement(GET_SALES_BY_INVOICE);
            stat.setString(1, invoice);
            rs = stat.executeQuery();
            while (rs.next()) {
                salesList.add(getBrowseSalesBean(rs));
            }
        } catch (SQLException sqle) {
            try {
                conn.rollback();
            } catch (SQLException rollback) {
                rollback.printStackTrace();
            }
            sqle.printStackTrace();
            throw new ShawDbConnectionException(sqle.getMessage());
        } finally {
            try {
                rs.close();
                stat.close();
            } catch (SQLException e) {

            }
        }

        return salesList;
    }

    public List getSalesByCustomer(String customer) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;
        ResultSet rs = null;
        List salesList = new ArrayList();

        try {

            stat = conn.prepareStatement(GET_SALES_BY_CUSTOMER);
            stat.setString(1, "%" + customer + "%");
            rs = stat.executeQuery();
            while (rs.next()) {
                salesList.add(getBrowseSalesBean(rs));
            }
        } catch (SQLException sqle) {
            try {
                conn.rollback();
            } catch (SQLException rollback) {
                rollback.printStackTrace();
            }
            sqle.printStackTrace();
            throw new ShawDbConnectionException(sqle.getMessage());
        } finally {
            try {
                rs.close();
                stat.close();
            } catch (SQLException e) {

            }
        }

        return salesList;
    }

    public SalesBean getSalesReport(int salesId) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;
        ResultSet rs = null;
        boolean firstPass = true;
        List partsList = new ArrayList();
        SalesBean salesBean = new SalesBean();

        try {

            stat = conn.prepareStatement(GET_SALES_REPORT);
            stat.setInt(1, salesId);
            stat.setInt(2, salesId);
            rs = stat.executeQuery();

            while (rs.next()) {
                if (firstPass) {
                    salesBean.setInvoiceNumber(rs.getString("invoice_number"));
                    salesBean.setPurchaseOrderNumber(rs.getString("purchase_order_number"));
                    salesBean.setRestCertNumber(rs.getString("rest_cert_number"));
                    salesBean.setDateIssued(rs.getDate("date_issued"));
                    salesBean.setPlaceIssued(rs.getString("place_issued"));
                    salesBean.setTin(rs.getString("tin"));
                    salesBean.setSoldTo(rs.getString("sold_to"));
                    salesBean.setDate(rs.getDate("date"));
                    salesBean.setAddress(rs.getString("address"));
                    salesBean.setTerms(rs.getString("terms"));
                    salesBean.setRemarks(rs.getString("remarks"));
                    salesBean.setReceivedBy(rs.getString("received_by"));
                    salesBean.setTotalCost(rs.getDouble("total_cost"));
                    salesBean.setTotalSales(rs.getDouble("total_sales"));
                    firstPass = false;
                }
                SalesPartBean part = new SalesPartBean();
                part.setPartNumber(rs.getString("part_number"));
                part.setQuantity(rs.getInt("quantity"));
                part.setUnitCost(rs.getDouble("unit_cost"));
                part.setUnitSales(rs.getDouble("unit_sales"));
                part.setDescription(rs.getString("description"));
                partsList.add(part);
            }
            salesBean.setPartsList(partsList);


        } catch (SQLException sqle) {
            try {
                conn.rollback();
            } catch (SQLException rollback) {
                rollback.printStackTrace();
            }
            sqle.printStackTrace();
            throw new ShawDbConnectionException(sqle.getMessage());
        } finally {
            try {
                rs.close();
                stat.close();
            } catch (SQLException e) {

            }
        }
        return salesBean;
    }

    private BrowseSalesBean getBrowseSalesBean(ResultSet rs) throws SQLException {
        BrowseSalesBean browseBean = new BrowseSalesBean();
        browseBean.setId(rs.getInt("id"));
        browseBean.setDate(rs.getDate("date"));
        browseBean.setInvoiceNumber(rs.getString("invoice_number"));
        browseBean.setSoldTo(rs.getString("sold_to"));
        browseBean.setTotalSales(rs.getDouble("total_sales"));
        browseBean.setTotalCost(rs.getDouble("total_cost"));

        return browseBean;
    }

    private static final String ADD_SALES_FROM_EXCEL = "INSERT INTO sales (invoice_number, sold_to, date, total_cost, total_sales, date_created) " +
            "VALUES (?, ?, ?, ?, ?, NOW())";
    private static final String ADD_SALES = "INSERT INTO sales (invoice_number, purchase_order_number, rest_cert_number, date_issued, " +
            "place_issued, tin, sold_to, date, address, terms, remarks, received_by, total_cost, total_sales, date_created) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
    private static final String ADD_SALES_PRODUCTS = "INSERT INTO sales_product " +
            "(sales_id, part_number, quantity, unit_cost, unit_sales, transaction_date) VALUES (IDENTITY(), ?, ?, ?, ?, ?)";
    private static final String UPDATE_INVENTORY_PRODUCTS = "UPDATE products SET quantity = (quantity - ?) WHERE part_number = ?";
    private static final String GET_SALES = "SELECT id, date, invoice_number, sold_to, total_sales, total_cost FROM sales WHERE MONTH(sales.date) = ? " +
            " AND YEAR(date) = ? ORDER BY date DESC";
    private static final String GET_SALES_BY_DATE = "SELECT id, date, invoice_number, sold_to, total_sales, total_cost FROM sales WHERE date = ? " +
            " ORDER BY date DESC";
    private static final String GET_SALES_BY_CUSTOMER = "SELECT id, date, invoice_number, sold_to, total_sales, total_cost FROM sales WHERE UCASE(sold_to) LIKE UCASE(?) " +
            " ORDER BY date DESC";
    private static final String GET_SALES_BY_INVOICE = "SELECT id, date, invoice_number, sold_to, total_sales, total_cost FROM sales WHERE UCASE(invoice_number) LIKE UCASE(?) " +
            " ORDER BY date DESC";
    private static final String GET_SALES_REPORT = "SELECT sales.*, salesprod.* FROM sales, " +
            "(SELECT sp.*, prod.description FROM sales_product sp LEFT JOIN products prod ON prod.part_number = sp.part_number WHERE sales_id = ?) " +
            "salesprod WHERE sales.id = ?";
    private static final String CANCEL_SALES = "DELETE FROM sales WHERE id = ?";
    private static final String CANCEL_SALES_PRODUCT = "DELETE FROM sales_product WHERE sales_id = ?";
    private static final String GET_SALES_PRODUCT = "SELECT * FROM sales_product WHERE sales_id = ?";
    private static final String ADD_INVENTORY_PRODUCTS = "UPDATE products SET quantity = (quantity + ?) WHERE part_number = ?";
    private static final String CHECK_EXISTING = "SELECT count(*) FROM sales WHERE invoice_number LIKE ?";
}
