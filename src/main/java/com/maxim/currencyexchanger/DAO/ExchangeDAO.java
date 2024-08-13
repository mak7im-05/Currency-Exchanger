package com.maxim.currencyexchanger.DAO;

import com.maxim.currencyexchanger.ConnectDB;
import com.maxim.currencyexchanger.model.CurrencyDTO;
import com.maxim.currencyexchanger.model.ExchangeDTO;
import com.maxim.currencyexchanger.model.ExchangeRatesDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;

public class ExchangeDAO {
    private final Connection connection;
    private final ConnectDB connectDB;

    public ExchangeDAO() throws SQLException {
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


    public ExchangeDTO getExchange(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) throws SQLException {
        ExchangeDTO exchange = null;
        ExchangeRatesDAO exRatesDAO = new ExchangeRatesDAO();
        CurrenciesDAO currenciesDAO = new CurrenciesDAO();

        CurrencyDTO baseCurrency = currenciesDAO.getCurrencyByCode(baseCurrencyCode);
        CurrencyDTO targetCurrency = currenciesDAO.getCurrencyByCode(targetCurrencyCode);

        if (baseCurrency == null || targetCurrency == null) { // одна из валют не существует в бд
            throw new SQLException();
        }

        ExchangeRatesDTO exRate = exRatesDAO.getExchangeRateByCodes(baseCurrencyCode, targetCurrencyCode); // первый случай АB
        if (exRate != null) {
            BigDecimal rate = exRate.getRate();
            BigDecimal convertedAmount = rate.multiply(amount);
            exchange = new ExchangeDTO(baseCurrency, targetCurrency, rate, amount, convertedAmount);

            currenciesDAO.closeConnection();
            exRatesDAO.closeConnection();
            return exchange;
        }

        exRate = exRatesDAO.getExchangeRateByCodes(targetCurrencyCode, baseCurrencyCode); //второй случай BA
        if (exRate != null) {
            BigDecimal rate = new BigDecimal("1").divide(exRate.getRate(), 2, RoundingMode.HALF_UP);
            BigDecimal convertedAmount = rate.multiply(amount);
            exchange = new ExchangeDTO(baseCurrency, targetCurrency, rate, amount, convertedAmount);

            currenciesDAO.closeConnection();
            exRatesDAO.closeConnection();
            return exchange;
        }
        ExchangeRatesDTO usd_A = exRatesDAO.getExchangeRateByCodes("USD", baseCurrencyCode); // 3 случай USD - A & USD - B
        ExchangeRatesDTO usd_B = exRatesDAO.getExchangeRateByCodes("USD", targetCurrencyCode); //все остальные случаи отпускаютсю

        if (usd_A != null && usd_B != null) {
            BigDecimal rate = new BigDecimal("1").divide(usd_A.getRate(), 2, RoundingMode.HALF_UP); // A - USD
            BigDecimal convertedAmount = rate.multiply(amount);

            rate = usd_B.getRate();
            convertedAmount = rate.multiply(convertedAmount); // USD - B

            rate = convertedAmount.divide(amount, 2, RoundingMode.HALF_UP);
            exchange = new ExchangeDTO(baseCurrency, targetCurrency, rate, amount, convertedAmount);
            currenciesDAO.closeConnection();
            exRatesDAO.closeConnection();
            return exchange;
        }
        currenciesDAO.closeConnection();
        exRatesDAO.closeConnection();
        return exchange;
    }
}


