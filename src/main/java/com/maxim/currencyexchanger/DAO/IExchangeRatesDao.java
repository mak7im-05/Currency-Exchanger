package com.maxim.currencyexchanger.DAO;

import com.maxim.currencyexchanger.model.CurrencyDTO;
import com.maxim.currencyexchanger.model.ExchangeRatesDTO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface IExchangeRatesDao {

    List<ExchangeRatesDTO> getExchangeRates() throws SQLException;

    ExchangeRatesDTO getExchangeRateByCodes(String baseCurrencyCode, String targetCurrencyCode) throws SQLException;

    ExchangeRatesDTO createExchangeRate(CurrencyDTO baseCurrencyId, CurrencyDTO targetCurrencyId, BigDecimal rate) throws SQLException;

    void updateRateFromExRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException;
}
