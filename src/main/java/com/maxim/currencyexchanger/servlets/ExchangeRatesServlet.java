package com.maxim.currencyexchanger.servlets;

import com.maxim.currencyexchanger.DAO.CurrenciesDAO;
import com.maxim.currencyexchanger.DAO.ExchangeRatesDAO;
import com.maxim.currencyexchanger.Utils.ResponseGenerator;
import com.maxim.currencyexchanger.model.CurrencyDTO;
import com.maxim.currencyexchanger.model.ExchangeRatesDTO;
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

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
        ExchangeRatesDTO exRate = null;
        try {
            ExchangeRatesDAO exchangeDao = new ExchangeRatesDAO();
            CurrenciesDAO currenciesDAO = new CurrenciesDAO();

            String baseCurrencyCode = request.getParameter("baseCurrencyCode");
            String targetCurrencyCode = request.getParameter("targetCurrencyCode");
            String rateStr = request.getParameter("rate");

            if (isInvalidParametrs(baseCurrencyCode, targetCurrencyCode, rateStr)) {
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

            ExchangeRatesDTO createdExRate = exchangeDao.createExchangeRate(baseCurrency.getId(), targetCurrency.getId(), rate);

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
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
        try {
            ExchangeRatesDAO dao = new ExchangeRatesDAO();
            List<ExchangeRatesDTO> exchangeRates = dao.getExchangeRates();

            responseGenerator.successResponseGenerator(exchangeRates, 200);

        } catch (SQLException e) {
            responseGenerator.DBisNotFound();
        }
    }

    private boolean isInvalidParametrs(String baseCurrencyCode, String targetCurrencyCode, String rateStr) {
        return baseCurrencyCode == null || targetCurrencyCode == null || rateStr == null;
    }

}