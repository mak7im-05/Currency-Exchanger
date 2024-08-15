package com.maxim.currencyexchanger.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {

    private Connection connection;

    public ConnectDB() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        String url = "jdbc:sqlite:C:/D/study/CurrencyExchanger/src/main/resources/mydb.db";
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
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