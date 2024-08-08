package com.maxim.currencyexchanger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils {

    private Connection connection;

    public DBUtils() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        String url = "jdbc:sqlite:C:/D/study/CurrencyExchanger/src/main/java/com/maxim/currencyexchanger/mydb.db";
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Success");
        } catch (SQLException e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                System.out.println("CLOSE");
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}