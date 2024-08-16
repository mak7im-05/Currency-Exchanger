package com.maxim.currencyexchanger.DAO;

import com.maxim.currencyexchanger.model.CurrencyDTO;

import java.sql.SQLException;
import java.util.List;

public interface ICurrenciesDAO {

    List<CurrencyDTO> getCurrencies() throws SQLException;

    CurrencyDTO getCurrencyByCode(String code) throws SQLException;

    CurrencyDTO createCurrency(CurrencyDTO currency) throws SQLException;

}
