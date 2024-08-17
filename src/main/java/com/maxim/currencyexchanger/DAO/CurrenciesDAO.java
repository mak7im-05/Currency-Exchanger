package com.maxim.currencyexchanger.DAO;

import com.maxim.currencyexchanger.Utils.DatabaseConnectionPool;
import com.maxim.currencyexchanger.model.CurrencyDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrenciesDAO implements ICurrenciesDAO {

    public List<CurrencyDTO> getCurrencies() throws SQLException {
        List<CurrencyDTO> currencies = new ArrayList<>();
        String query = "SELECT * FROM Currencies";
        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                CurrencyDTO currency = new CurrencyDTO(
                        rs.getInt("id"),
                        rs.getString("fullName"),
                        rs.getString("code"),
                        rs.getString("sign"));
                currencies.add(currency);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return currencies;
    }

    public CurrencyDTO getCurrencyByCode(String code) throws SQLException {
        CurrencyDTO currency = null;
        String query = "SELECT * FROM Currencies WHERE Code = ?";
        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, code);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    currency = new CurrencyDTO(
                            rs.getInt("id"),
                            rs.getString("fullName"),
                            rs.getString("code"),
                            rs.getString("sign"));
                }
            }

        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return currency;
    }

    public CurrencyDTO createCurrency(CurrencyDTO currency) throws SQLException {
        String query = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getName());
            statement.setString(3, currency.getSign());

            try {
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new SQLException("Currency is exists");
            }

            try (ResultSet generateId = statement.getGeneratedKeys();) {
                currency.setId(generateId.getInt(1));
            }
        } catch (SQLException e) {
            throw new SQLException("DB failed");
        }
        return currency;
    }
}