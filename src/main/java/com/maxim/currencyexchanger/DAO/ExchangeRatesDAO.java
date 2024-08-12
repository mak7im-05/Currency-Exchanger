package com.maxim.currencyexchanger.DAO;

import com.maxim.currencyexchanger.ConnectDB;
import com.maxim.currencyexchanger.model.CurrencyDTO;
import com.maxim.currencyexchanger.model.ExchangeRatesDTO;

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
}


