package com.maxim.currencyexchanger.servlets;

import com.maxim.currencyexchanger.DAO.CurrenciesDAO;
import com.maxim.currencyexchanger.DAO.ExchangeRatesDAO;
import com.maxim.currencyexchanger.ResponseGenerator;
import com.maxim.currencyexchanger.model.ExchangeRatesDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ExchangeRatesServlet", value = "/exchangeRates")

public class ExchangeRatesServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response, request);
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