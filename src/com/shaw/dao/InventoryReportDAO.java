package com.shaw.dao;

import com.shaw.db.ShawDbConnectionException;
import com.shaw.db.HSQLConnection;
import com.shaw.bean.ProductBean;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryReportDAO {
    public List getInventoryReport(Date startDate, Date endDate) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;
        ResultSet rs = null;
        List products = new ArrayList();

        try {

            stat = conn.prepareStatement(GET_INVENTORY_REPORT);
            stat.setDate(1, new java.sql.Date(startDate.getTime()));
            stat.setDate(2, new java.sql.Date(endDate.getTime()));
            rs = stat.executeQuery();

            while (rs.next()) {
                ProductBean product = getProductFromResultSet(rs);
                products.add(product);
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new ShawDbConnectionException(sqle.getMessage());
        } finally {
            try {
                rs.close();
                stat.close();
            } catch (SQLException e) {

            }
        }
        return products;
    }

    public List getInventoryReportOfProduct(String partNumber, Date startDate, Date endDate) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;
        ResultSet rs = null;
        List products = new ArrayList();

        try {

            stat = conn.prepareStatement(GET_INVENTORY_REPORT_OF_PRODUCT);
            stat.setDate(1, new java.sql.Date(startDate.getTime()));
            stat.setDate(2, new java.sql.Date(endDate.getTime()));
            stat.setString(3, partNumber);
            rs = stat.executeQuery();

            while (rs.next()) {
                ProductBean product = getProductFromResultSet(rs);
                products.add(product);
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new ShawDbConnectionException(sqle.getMessage());
        } finally {
            try {
                rs.close();
                stat.close();
            } catch (SQLException e) {

            }
        }
        return products;
    }

    public List searchInventoryReport(Date startDate, Date endDate, String search) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;
        ResultSet rs = null;
        List products = new ArrayList();

        try {

            stat = conn.prepareStatement(SEARCH_INVENTORY_REPORT);
            stat.setDate(1, new java.sql.Date(startDate.getTime()));
            stat.setDate(2, new java.sql.Date(endDate.getTime()));
            stat.setString(3, "%" + search.toUpperCase() + "%");
            rs = stat.executeQuery();

            while (rs.next()) {
                ProductBean product = getProductFromResultSet(rs);
                products.add(product);
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new ShawDbConnectionException(sqle.getMessage());
        } finally {
            try {
                rs.close();
                stat.close();
            } catch (SQLException e) {

            }
        }
        return products;
    }

    private ProductBean getProductFromResultSet(ResultSet rs) throws SQLException {
        ProductBean product = new ProductBean();
        product.setPartNumber(rs.getString("part_number"));
        product.setDescription(rs.getString("description"));
        product.setLocation(rs.getString("location"));
        product.setPcp(rs.getDouble("pcp"));
        product.setQuantity(rs.getInt("quantity"));
        product.setUnitPrice(rs.getDouble("unit_price"));
        product.setMiscelleneous(rs.getString("misc"));
        product.setOtherNumber(rs.getString("other_number"));
        product.setTransactionDate(rs.getDate("transaction_date"));
        product.setQuanitySold(rs.getInt("fquantity"));
        product.setComments(rs.getString("comments"));

        return product;

    }

    private static final String GET_INVENTORY_REPORT = "SELECT p.*, filter.quantity as fquantity, filter.transaction_date as transaction_date FROM " +
            "products p LEFT JOIN (SELECT * FROM sales_product WHERE transaction_date BETWEEN ? AND ?) filter ON p.part_number = filter.part_number ORDER BY p.part_number";
    private static final String SEARCH_INVENTORY_REPORT = "SELECT p.*, filter.quantity as fquantity, filter.transaction_date as transaction_date FROM " +
            "products p LEFT JOIN (SELECT * FROM sales_product WHERE transaction_date BETWEEN ? AND ?) filter ON p.part_number = filter.part_number " +
            "WHERE UCASE(p.part_number) LIKE ?";
    private static final String GET_INVENTORY_REPORT_OF_PRODUCT = "SELECT p.*, filter.quantity as fquantity, filter.transaction_date as transaction_date FROM " +
            "products p LEFT JOIN (SELECT * FROM sales_product WHERE transaction_date BETWEEN ? AND ?) filter ON p.part_number = filter.part_number " +
            "WHERE p.part_number = ? ORDER BY p.part_number";
}
