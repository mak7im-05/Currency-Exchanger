package com.maxim.currencyexchanger.servlets;

import com.maxim.currencyexchanger.DAO.ExchangeRatesDAO;
import com.maxim.currencyexchanger.ResponseGenerator;
import com.maxim.currencyexchanger.model.ExchangeRatesDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet(name = "ExchangeRateServlet", value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (request.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
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
            if (dao != null) {
                dao.closeConnection();
            }
        }
    }

    public void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
        ExchangeRatesDAO dao = null;
        try {
            dao = new ExchangeRatesDAO();

            String reqURI = request.getRequestURI(); // принимаем параметры в реквесте
            String[] urlParts = reqURI.split("/");
            String param = urlParts[urlParts.length - 1].toUpperCase();
            String rateStr = request.getParameter("rate");

            if (rateStr == null) {
                responseGenerator.misField();
                return;
            }

            BigDecimal rate = new BigDecimal(rateStr);

            if (param.length() != 6) {
                responseGenerator.misField();
                return;
            }

            String baseCurrencyCode = param.substring(0, 3);
            String targetCurrencyCode = param.substring(3);

            dao.updateRateFromExRate(baseCurrencyCode, targetCurrencyCode, rate);
            ExchangeRatesDTO updatedExRate = dao.getExchangeRateByCodes(baseCurrencyCode, targetCurrencyCode);

            if (updatedExRate == null) {
                responseGenerator.currencyPairIsNotExist();
                return;
            }

            responseGenerator.responseGenerator(updatedExRate);
        } catch (SQLException e) {
            responseGenerator.DBisNotFound();
        } finally {
            if (dao != null) {
                dao.closeConnection();
            }
        }
    }
}
