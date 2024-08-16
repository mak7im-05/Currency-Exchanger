package com.maxim.currencyexchanger.DAO;

import com.maxim.currencyexchanger.Utils.DatabaseConnectionPool;
import com.maxim.currencyexchanger.model.CurrencyDTO;
import com.maxim.currencyexchanger.model.ExchangeRatesDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesDAO {

    public List<ExchangeRatesDTO> getExchangeRates() throws SQLException {
        List<ExchangeRatesDTO> exRates = new ArrayList<>();
        String query = "SELECT ExchangeRates.id, " +
                "C.id, C.code, C.fullname, C.sign, " +
                "C2.id, C2.code, C2.fullname, C2.sign, " +
                "ExchangeRates.rate\n" +
                "FROM ExchangeRates\n" +
                "JOIN Currencies C on C.id = ExchangeRates.basecurrencyid\n" +
                "JOIN Currencies C2 on C2.id = ExchangeRates.targetcurrencyid";
        try {
            Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                ExchangeRatesDTO exRate = new ExchangeRatesDTO(
                        rs.getInt(1),
                        new CurrencyDTO(rs.getInt(2),
                                rs.getString(3),
                                rs.getString(4),
                                rs.getString(5)),
                        new CurrencyDTO(rs.getInt(6),
                                rs.getString(7),
                                rs.getString(8),
                                rs.getString(9)),
                        rs.getBigDecimal(10));
                exRates.add(exRate);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }

        return exRates;
    }

    public ExchangeRatesDTO getExchangeRateByCodes(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        ExchangeRatesDTO exRate = null;
        String query = "SELECT ExchangeRates.id, " +
                "C.id, C.code, C.fullname, C.sign, " +
                "C2.id, C2.code, C2.fullname, C2.sign, " +
                "ExchangeRates.rate\n" +
                "FROM ExchangeRates\n" +
                "JOIN Currencies C on C.id = ExchangeRates.basecurrencyid\n" +
                "JOIN Currencies C2 on C2.id = ExchangeRates.targetcurrencyid\n" +
                "WHERE C.Code = ? AND C2.Code = ?";
        try {
            Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, baseCurrencyCode);
            statement.setString(2, targetCurrencyCode);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                exRate = new ExchangeRatesDTO(
                        rs.getInt(1),
                        new CurrencyDTO(rs.getInt(2),
                                rs.getString(3),
                                rs.getString(4),
                                rs.getString(5)),
                        new CurrencyDTO(rs.getInt(6),
                                rs.getString(7),
                                rs.getString(8),
                                rs.getString(9)),
                        rs.getBigDecimal(10));
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }

        return exRate;
    }

    public ExchangeRatesDTO createExchangeRate(int baseCurrencyId, int targetCurrencyId, BigDecimal rate) throws SQLException {
        String query = "INSERT INTO ExchangeRates (BaseCurrencyID, TargetCurrencyID, Rate) VALUES (?, ?, ?)";
        ExchangeRatesDTO exRate = new ExchangeRatesDTO();
        PreparedStatement statement;
        try {
            Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
            statement = connection.prepareStatement(query);

        } catch (SQLException e) {
            throw new SQLException("DB failed");
        }

        statement.setInt(1, baseCurrencyId);
        statement.setInt(2, targetCurrencyId);
        statement.setBigDecimal(3, rate);
        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Currency is exists");
        }
        ResultSet generateId = statement.getGeneratedKeys();

        exRate.setId(generateId.getInt(1));
        return exRate;
    }

    public void updateRateFromExRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException {
        String query = "UPDATE ExchangeRates " +
                "SET rate = ? " +
                "WHERE (SELECT id FROM currencies WHERE code=?)=BaseCurrencyID " +
                "AND (SELECT id FROM currencies WHERE code=?)=TargetCurrencyID";
        Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setBigDecimal(1, rate);
        statement.setString(2, baseCurrencyCode);
        statement.setString(3, targetCurrencyCode);
        statement.executeUpdate();
    }
}


