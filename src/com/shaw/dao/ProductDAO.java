package com.shaw.dao;

import com.shaw.db.HSQLConnection;
import com.shaw.db.ShawDbConnectionException;
import com.shaw.bean.ProductBean;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductDAO {
    public void addProduct(ProductBean product) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;

        try {
            stat = conn.prepareStatement(INSERT_PRODUCT);
            stat.setString(1, product.getPartNumber());
            stat.setString(2, product.getDescription());
            stat.setString(3, product.getLocation());
            stat.setInt(4, product.getQuantity());
            stat.setDouble(5, product.getPcp());
            stat.setString(6, product.getMiscelleneous());
            stat.setString(7, product.getOtherNumber());
            stat.setDouble(8, product.getUnitPrice());
            stat.executeUpdate();
        } catch (SQLException sqle) {
            if(sqle.getErrorCode() == -104)
                throw new ShawDbConnectionException("Part number " + product.getPartNumber() + " already exists");
            sqle.printStackTrace();
            throw new ShawDbConnectionException(sqle.getMessage());
        } finally {
            try {
                stat.close();
            } catch (SQLException e) {

            }
        }
    }

    public ProductBean getProduct(String partnumber) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;
        ResultSet rs = null;

        try {
            stat = conn.prepareStatement(GET_PRODUCT);
            stat.setString(1, partnumber.toUpperCase());
            rs = stat.executeQuery();

            if(rs.next()) {
                ProductBean product = getProductFromResultSet(rs);
                return product;
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
        return null;
    }

    public List getProducts() throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;
        ResultSet rs = null;
        List products = new ArrayList();

        try {
            stat = conn.prepareStatement(GET_PRODUCTS);
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

    public void deleteProduct(String partNumber) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;

        try {
            stat = conn.prepareStatement(DELETE_PRODUCT);
            stat.setString(1, partNumber);
            stat.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new ShawDbConnectionException(sqle.getMessage());
        } finally {
            try {
                stat.close();
            } catch (SQLException e) {

            }
        }
    }

    public void updateProduct(String partNumber, ProductBean product) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;

        try {
            stat = conn.prepareStatement(UPDATE_PRODUCT);
            int i = 1;
            stat.setString(i++, product.getPartNumber());
            stat.setString(i++, product.getDescription());
            stat.setString(i++, product.getLocation());
            stat.setInt(i++, product.getQuantity());
            stat.setDouble(i++, product.getPcp());
            stat.setString(i++, product.getMiscelleneous());
            stat.setString(i++, product.getOtherNumber());

            stat.setString(i++, partNumber);

            stat.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new ShawDbConnectionException(sqle.getMessage());
        } finally {
            try {
                stat.close();
            } catch (SQLException e) {

            }
        }
    }

    public void updateComments(String partNumber, String comments) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;

        try {
            stat = conn.prepareStatement(UPDATE_COMMENTS);
            int i = 1;
            stat.setString(i++, comments == null ? "" : comments);
            stat.setString(i++, partNumber);

            stat.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new ShawDbConnectionException(sqle.getMessage());
        } finally {
            try {
                stat.close();
            } catch (SQLException e) {

            }
        }
    }

    private ProductBean getProductFromResultSet(ResultSet rs) throws SQLException {
        ProductBean product = new ProductBean();
        product.setPartNumber(rs.getString("part_number"));
        product.setDescription(rs.getString("description"));
        product.setLocation(rs.getString("location"));
        product.setQuantity(rs.getInt("quantity"));
        product.setPcp(rs.getDouble("pcp"));
        product.setMiscelleneous(rs.getString("misc"));
        product.setOtherNumber(rs.getString("other_number"));
        product.setUnitPrice(rs.getDouble("unit_price"));

        return product;
    }

    public List searchProducts(String criteria) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;
        ResultSet rs = null;
        List products = new ArrayList();

        try {
            stat = conn.prepareStatement(SEARCH_PRODUCTS);
            stat.setString(1, "%" + criteria + "%");
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

    private static final String GET_PRODUCT = "SELECT * FROM products WHERE UCASE(part_number) = UCASE(?)";
    private static final String SEARCH_PRODUCTS = "SELECT * FROM products WHERE UCASE(part_number) LIKE UCASE(?) ORDER BY part_number";
    private static final String GET_PRODUCTS = "SELECT * FROM products ORDER BY part_number";
    private static final String DELETE_PRODUCT = "DELETE FROM products WHERE part_number = UCASE(?)";
    private static final String UPDATE_PRODUCT = "UPDATE products SET part_number = UCASE(?), description = ?, location = ?, " +
            "quantity = ?, pcp = ?, misc = ?, other_number = ? WHERE part_number = ?";
    private static final String INSERT_PRODUCT = "INSERT INTO products (part_number, description, location, quantity, " +
            "pcp, misc, other_number, unit_price) VALUES (UCASE(?), ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_COMMENTS = "UPDATE products SET comments = ? WHERE part_number = ?";
}
