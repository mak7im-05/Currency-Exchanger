package com.maxim.currencyexchanger.servlets;

import com.maxim.currencyexchanger.DAO.CurrenciesDAO;
import com.maxim.currencyexchanger.DAO.ExchangeRatesDAO;
import com.maxim.currencyexchanger.Utils.ResponseGenerator;
import com.maxim.currencyexchanger.model.CurrencyDTO;
import com.maxim.currencyexchanger.model.ExchangeRatesDTO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ExchangeRatesServlet", value = "/exchangeRates/*")

public class ExchangeRatesServlet extends HttpServlet {
    private CurrenciesDAO currenciesDAO;
    private ExchangeRatesDAO exchangeRatesDAO;
    private ResponseGenerator responseGenerator;

    @Override
    public void init(ServletConfig config) {
        currenciesDAO = new CurrenciesDAO();
        exchangeRatesDAO = new ExchangeRatesDAO();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            responseGenerator = new ResponseGenerator(response);

            String baseCurrencyCode = request.getParameter("baseCurrencyCode");
            String targetCurrencyCode = request.getParameter("targetCurrencyCode");
            String rateStr = request.getParameter("rate");

            if (isInvalidParameters(baseCurrencyCode, targetCurrencyCode, rateStr)) {
                responseGenerator.misField();
                return;
            }
            BigDecimal rate = new BigDecimal(rateStr);

            CurrencyDTO baseCurrency = currenciesDAO.getCurrencyByCode(baseCurrencyCode);
            CurrencyDTO targetCurrency = currenciesDAO.getCurrencyByCode(targetCurrencyCode);

            if (baseCurrency == null || targetCurrency == null) {
                responseGenerator.currencyPairIsNotExist();
                return;
            }
            int baseCurrencyId = baseCurrency.getId();
            int targetCurrencyId = targetCurrency.getId();

            ExchangeRatesDTO createdExRate = exchangeRatesDAO.createExchangeRate(baseCurrencyId, targetCurrencyId, rate);

            createdExRate.setBaseCurrency(baseCurrency);
            createdExRate.setTargetCurrency(targetCurrency);

            responseGenerator.successResponseGenerator(createdExRate, 201);
        } catch (SQLException e) {
            if (e.getMessage().equals("Currency is exists")) {// уже существуют
                responseGenerator.currencyIsAlreadyExists();
            } else if (e.getMessage().equals("DB failed")) {
                responseGenerator.DBisNotFound();
            }
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            responseGenerator = new ResponseGenerator(response);

            List<ExchangeRatesDTO> exchangeRates = exchangeRatesDAO.getExchangeRates();

            responseGenerator.successResponseGenerator(exchangeRates, 200);
        } catch (SQLException e) {
            responseGenerator.DBisNotFound();
        }
    }

    private boolean isInvalidParameters(String baseCurrencyCode, String targetCurrencyCode, String rateStr) {
        return baseCurrencyCode == null || targetCurrencyCode == null || rateStr == null;
    }

}