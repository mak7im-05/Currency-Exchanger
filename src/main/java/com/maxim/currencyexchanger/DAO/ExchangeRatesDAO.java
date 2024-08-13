package com.maxim.currencyexchanger.DAO;

import com.maxim.currencyexchanger.ConnectDB;
import com.maxim.currencyexchanger.model.CurrencyDTO;
import com.maxim.currencyexchanger.model.ExchangeRatesDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesDAO {
    private final Connection connection;
    private final ConnectDB connectDB;

    public ExchangeRatesDAO() throws SQLException {
        try {
            connectDB = new ConnectDB();
            connection = connectDB.getConnection();
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
    }

    public void closeConnection() {
        connectDB.closeConnection();
    }

    public List<ExchangeRatesDTO> getExchangeRates() throws SQLException {
        List<ExchangeRatesDTO> exRates = new ArrayList<>();
        String query = "SELECT ExchangeRates.id, " +
                "C.id, C.code, C.fullname, C.sign, " +
                "C2.id, C2.code, C2.fullname, C2.sign, " +
                "ExchangeRates.rate\n" +
                "FROM ExchangeRates\n" +
                "JOIN Currencies C on C.id = ExchangeRates.basecurrencyid\n" +
                "JOIN Currencies C2 on C2.id = ExchangeRates.targetcurrencyid";
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

        return exRate;
    }

    public ExchangeRatesDTO createExchangeRate(ExchangeRatesDTO exRate) throws SQLException {

        CurrencyDTO baseCurrency = getCurrencyByCode(exRate.getBaseCurrency().getCode());
        CurrencyDTO targetCurrency = getCurrencyByCode(exRate.getTargetCurrency().getCode());

        if (baseCurrency == null || targetCurrency == null) {
            throw new SQLException();
        }

        exRate.setId(-1); // Одна (или обе) валюта из валютной пары не существует в БД - 404

        String queryCreate = "INSERT INTO ExchangeRates (BaseCurrencyID, TargetCurrencyID, Rate) VALUES (?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(queryCreate);

            exRate.setId(-2); // Ошибка (например, база данных недоступна) - 500


            statement.setInt(1, baseCurrency.getId());
            statement.setInt(2, targetCurrency.getId());
            statement.setBigDecimal(3, exRate.getRate());
            statement.executeUpdate();

            ResultSet generateId = statement.getGeneratedKeys();

            exRate.setId(generateId.getInt(1));
            exRate.setBaseCurrency(baseCurrency);
            exRate.setTargetCurrency(targetCurrency);
        } catch (SQLException e) {
            throw new SQLException(e);
        }

        return exRate;
    }

    public CurrencyDTO getCurrencyByCode(String code) throws SQLException {
        CurrencyDTO currency = null;
        String query = "SELECT * FROM Currencies WHERE Code = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, code);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            currency = new CurrencyDTO(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4));
        }
        return currency;
    }

    public void updateRateFromExRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException {
        String query = "UPDATE ExchangeRates " +
                "SET rate = ? " +
                "WHERE (SELECT id FROM currencies WHERE code=?)=BaseCurrencyID " +
                "AND (SELECT id FROM currencies WHERE code=?)=TargetCurrencyID";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setBigDecimal(1, rate);
        statement.setString(2, baseCurrencyCode);
        statement.setString(3, targetCurrencyCode);
        statement.executeUpdate();
    }
}


