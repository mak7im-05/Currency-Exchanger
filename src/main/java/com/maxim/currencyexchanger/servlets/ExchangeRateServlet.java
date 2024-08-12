package com.maxim.currencyexchanger.servlets;

import com.maxim.currencyexchanger.DAO.ExchangeRatesDAO;
import com.maxim.currencyexchanger.ResponseGenerator;
import com.maxim.currencyexchanger.model.ExchangeRatesDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "ExchangeRateServlet", value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response, request);
        ExchangeRatesDAO dao = null;
        try {
            dao = new ExchangeRatesDAO();

            String reqURI = request.getRequestURI(); // принимаем параметры в реквесте
            String[] urlParts = reqURI.split("/");
            String param = urlParts[urlParts.length - 1].toUpperCase();

            if (param.length() != 6) {
                responseGenerator.misField();
                return;
            }

            String baseCurrencyCode = param.substring(0, 3);
            String targetCurrencyCode = param.substring(3);

            ExchangeRatesDTO exRate = dao.getExchangeRateByCodes(baseCurrencyCode, targetCurrencyCode);

            if (exRate == null) {
                responseGenerator.exRatesNotFound();
                return;
            }

            responseGenerator.responseGenerator(exRate);
        } catch (SQLException e) {
            responseGenerator.DBisNotFound();
        } finally {
            dao.closeConnection();
        }
    }
}