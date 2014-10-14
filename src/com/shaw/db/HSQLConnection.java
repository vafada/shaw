package com.shaw.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class HSQLConnection {
    private static HSQLConnection ourInstance;
    private Connection conn;

    public synchronized static HSQLConnection getInstance() {
        if (ourInstance == null) {
            ourInstance = new HSQLConnection();
        }
        return ourInstance;
    }

    private HSQLConnection() {

    }

    public void initialize() throws ShawDbConnectionException {
        if (conn == null) {
            try {
                Class.forName("org.hsqldb.jdbcDriver");
                conn = DriverManager.getConnection("jdbc:hsqldb:data/shaw", "sa", "");
            } catch (Exception e) {
                throw new ShawDbConnectionException(e.getMessage());
            }
        }
    }

    public Connection getConn() throws ShawDbConnectionException {
        return conn;
    }

    public void close() {

        try {
            Statement stmt = conn.createStatement();
            stmt.executeQuery("SHUTDOWN");
            conn.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    private static final String CONNECTION_ERROR = "Error getting connection.";
}

