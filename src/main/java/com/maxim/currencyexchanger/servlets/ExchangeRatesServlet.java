package com.maxim.currencyexchanger.servlets;

import com.maxim.currencyexchanger.DAO.ExchangeRatesDAO;
import com.maxim.currencyexchanger.ResponseGenerator;
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
        ExchangeRatesDAO dao = null;
        ExchangeRatesDTO exRate = null;
        try {
            dao = new ExchangeRatesDAO();

            String baseCurrencyCode = request.getParameter("baseCurrencyCode");
            String targetCurrencyCode = request.getParameter("targetCurrencyCode");
            BigDecimal rate = new BigDecimal(request.getParameter("rate"));

            if (baseCurrencyCode == null || targetCurrencyCode == null || rate == null) {
                responseGenerator.misField();
                return;
            }

            CurrencyDTO baseCurrency = new CurrencyDTO();
            CurrencyDTO targetCurrency = new CurrencyDTO();

            baseCurrency.setCode(baseCurrencyCode.toUpperCase());
            targetCurrency.setCode(targetCurrencyCode.toUpperCase());

            exRate = new ExchangeRatesDTO(0, baseCurrency, targetCurrency, rate);

            ExchangeRatesDTO createdExRate = dao.createExchangeRate(exRate);

            responseGenerator.responseGenerator(createdExRate);
        } catch (SQLException e) {
            if (exRate.getId() == 0) {
                responseGenerator.currencyPairIsNotExist();
            } else if (exRate.getId() == -2) {
                responseGenerator.currencyIsAlreadyExists();
            } else {
                responseGenerator.DBisNotFound();
            }
        }
        if (dao != null) {
            dao.closeConnection();
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
        ExchangeRatesDAO dao = null;
        try {
            dao = new ExchangeRatesDAO();
            List<ExchangeRatesDTO> exchangeRates = dao.getExchangeRates();

            responseGenerator.responseGenerator(exchangeRates);

        } catch (SQLException e) {
            responseGenerator.DBisNotFound();
        }
        if (dao != null) {
            dao.closeConnection();
        }
    }

}