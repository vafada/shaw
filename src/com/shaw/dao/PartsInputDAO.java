package com.shaw.dao;

import com.shaw.bean.PartsInputBean;
import com.shaw.bean.PartsBean;
import com.shaw.db.ShawDbConnectionException;
import com.shaw.db.HSQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

public class PartsInputDAO {
    public void addPartsInput(PartsInputBean partInput) throws ShawDbConnectionException {
        Connection conn = HSQLConnection.getInstance().getConn();
        PreparedStatement stat = null;

        try {
            conn.setAutoCommit(false);

            stat = conn.prepareStatement(ADD_PARTS_INPUT);
            int i = 1;
            stat.setString(i++, partInput.getCustomerNumber());
            stat.setString(i++, partInput.getOrderNumber());
            stat.setString(i++, partInput.getAllocationNumber());
            stat.setString(i++, partInput.getPaymentTerm());
            stat.setString(i++, partInput.getDiscount());
            stat.setString(i++, partInput.getSurcharge());
            stat.setDate(i++, new java.sql.Date(partInput.getDate().getTime()));
            stat.setString(i++, partInput.getInvoiceNumber());
            stat.executeUpdate();

            Iterator iter = partInput.getPartsList().iterator();
            while(iter.hasNext()) {
                PartsBean parts = (PartsBean) iter.next();
                stat = conn.prepareStatement(ADD_PARTS_INPUT_PRODUCTS);
                i = 1;
                stat.setString(i++, parts.getPartNumber());
                stat.setInt(i++, parts.getQuantity());
                stat.setDouble(i++, parts.getUnitPrice());
                stat.setDouble(i++, parts.getExtendedAmount());
                stat.executeUpdate();

                stat = conn.prepareStatement(UPDATE_INVENTORY_PRODUCTS);
                i = 1;
                stat.setInt(i++, parts.getQuantity());
                stat.setDouble(i++, parts.getUnitPrice());
                stat.setString(i++, parts.getPartNumber().trim());
                stat.executeUpdate();
            }

            conn.commit();

        } catch (SQLException sqle) {
            try {
                conn.rollback();
            } catch(SQLException rollback) {
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

    private static final String ADD_PARTS_INPUT = "INSERT INTO parts_input " +
            "(customer_number, order_number, allocation_number, payment_term, discount, surcharge, date, invoice_number, " +
            "date_created) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
    private static final String ADD_PARTS_INPUT_PRODUCTS = "INSERT INTO parts_input_product " +
            "(parts_input_id, part_number, quantity, unit_price, extended_amount) VALUES (IDENTITY(), ?, ?, ?, ?)";
    private static final String UPDATE_INVENTORY_PRODUCTS = "UPDATE products SET quantity = (quantity + ?), " +
            "unit_price = ? WHERE part_number = ?";
}

