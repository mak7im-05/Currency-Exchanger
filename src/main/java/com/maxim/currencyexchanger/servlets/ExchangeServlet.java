package com.maxim.currencyexchanger.servlets;

import com.maxim.currencyexchanger.DAO.ExchangeDAO;
import com.maxim.currencyexchanger.ResponseGenerator;
import com.maxim.currencyexchanger.model.ExchangeDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet(name = "ExchangeServlet", urlPatterns = "/exchange/*")
public class ExchangeServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
        ExchangeDAO dao = null;
        try {
            dao = new ExchangeDAO();

            String baseCurrencyCode = request.getParameter("from");
            String targetCurrencyCode = request.getParameter("to");
            String amountStr = request.getParameter("amount");

            if (invalidParametrs(baseCurrencyCode, targetCurrencyCode, amountStr)) {
                responseGenerator.misField();
                return;
            }

            BigDecimal amount = new BigDecimal(amountStr);

            if (amount.compareTo(new BigDecimal("0")) != 1) {
                responseGenerator.misField();
                return;
            }

            ExchangeDTO exchangeDTO = dao.getExchange(baseCurrencyCode, targetCurrencyCode, amount);

            responseGenerator.responseGenerator(exchangeDTO);
        } catch (SQLException e) {
            responseGenerator.DBisNotFound();
        } finally {
            if (dao != null) {
                dao.closeConnection();
            }
        }
    }

    private boolean invalidParametrs(String baseCurrencyCode, String targetCurrencyCode, String amountStr) {
        return baseCurrencyCode == null || targetCurrencyCode == null || amountStr == null || baseCurrencyCode.length() != 3 || targetCurrencyCode.length() != 3;
    }
}