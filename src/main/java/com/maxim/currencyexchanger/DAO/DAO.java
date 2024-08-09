package com.maxim.currencyexchanger.DAO;

import com.maxim.currencyexchanger.ConnectDB;
import com.maxim.currencyexchanger.model.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO {
    private Connection connection;

    public DAO() {
        try {
            ConnectDB connectDB = new ConnectDB();
            connection = connectDB.getConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createCurrency(Currency currency) {
        try {
            String CREATE = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(CREATE);
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Currency getCurrency(int id) {
        Currency currency = new Currency();
        String SELECT = "SELECT * FROM Currencies WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(SELECT);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                currency.setCode(rs.getString("Code"));
                currency.setFullName(rs.getString("FullName"));
                currency.setSign(rs.getString("Sign"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currency;
    }

    public List<Currency> getCurrencies() throws SQLException {
        List<Currency> currencies = new ArrayList<>();
        String query = "SELECT * FROM Currencies";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Currency currency = new Currency(
                        rs.getInt("ID"),
                        rs.getString("Code"),
                        rs.getString("FullName"),
                        rs.getString("Sign"));
                currencies.add(currency);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return currencies;
    }
}
