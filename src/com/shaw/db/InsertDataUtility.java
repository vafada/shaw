package com.shaw.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class InsertDataUtility {
    public static void main(String[] args) throws Exception {
        Class.forName("org.hsqldb.jdbcDriver");
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:data/shaw", // filenames
                "sa", // username
                "");                     // password
        Statement st = conn.createStatement();            // statement objects can be reused with

        st.executeUpdate("INSERT INTO products VALUES ('MB129591', 'FILTER,FUEL', 'CABINET A', 10, 123.0, 'misc', 'other number', 120.0, 'comment')");
        st.executeUpdate("INSERT INTO products VALUES ('MD050395', 'RING SET,PISTON', 'CABINET B', 20, 123.0, 'misc', 'other number', 45.0, 'comment')");
        st.executeUpdate("INSERT INTO products VALUES ('MD145813', 'BELT,VALVE TIMING', 'CABINET C', 30, 123.0, 'misc', 'other number', 340.0, 'comment')");
        st.executeUpdate("INSERT INTO products VALUES ('1', 'PART 1', 'CABINET A', 10, 123.0, 'misc', 'other number', 120.0, 'comment')");
        st.executeUpdate("INSERT INTO products VALUES ('2', 'PART 2', 'CABINET B', 20, 123.0, 'misc', 'other number', 45.0, 'comment')");
        st.executeUpdate("INSERT INTO products VALUES ('3', 'PART 3', 'CABINET C', 30, 123.0, 'misc', 'other number', 340.0, '')");
        st.executeUpdate("INSERT INTO products VALUES ('4', 'PART 4', 'CABINET A', 10, 123.0, 'misc', 'other number', 120.0, '')");
        st.executeUpdate("INSERT INTO products VALUES ('5', 'PART 5', 'CABINET B', 20, 123.0, 'misc', 'other number', 45.0, '')");
        st.executeUpdate("INSERT INTO products VALUES ('6', 'PART 6', 'CABINET C', 30, 123.0, 'misc', 'other number', 340.0, '')");

        for (int j = 0; j < 30; j++) {

            st.executeUpdate("INSERT INTO sales (invoice_number, purchase_order_number, rest_cert_number, date_issued, " +
                    "place_issued, tin, sold_to, date, address, terms, remarks, received_by, total_cost, total_sales, date_created) " +
                    "VALUES ('invno " + j + "', 'pono', 'rsno', CURDATE(), 'place_issued', 'tin', 'sold_to', '2004-08-" + j + "', 'address', 'terms', " +
                    "'remarks', 'rec_by', 1000.0, 1200.0, CURDATE())");

            for (int i = 0; i < 30; i++) {
                int part = (int) (Math.random() * 6) + 1;
                int month = (int) (Math.random() * 12) + 1;
                int day = (int) (Math.random() * 30) + 1;

                String date = "'2004-" + month + "-" + day + "'";

                st.executeQuery("INSERT INTO sales_product (sales_id, part_number, quantity, unit_cost, unit_sales, " +
                        "transaction_date) VALUES (IDENTITY(), " + part + ", 20, 100.0, 150.0, " + date + ")");


            }
        }


        st.execute("SHUTDOWN");
        st.close();
        conn.close();
        System.out.println("Inserted data");
    }
    //SELECT * FROM products p LEFT JOIN (SELECT * FROM sales_product WHERE transaction_date BETWEEN '2004-03-01' AND '2004-10-02') filter ON p.part_number = filter.part_number
}
