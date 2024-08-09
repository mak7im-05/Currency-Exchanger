package com.maxim.currencyexchanger.servlets;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

import com.google.gson.Gson;
import com.maxim.currencyexchanger.model.Currency;

import com.maxim.currencyexchanger.DAO.DAO;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "CurrenciesServlet", value = "/currencies")
public class CurrenciesServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        try {
            DAO dao = new DAO();
            List<Currency> currencies = dao.getCurrencies();
            String currenciesJson = gson.toJson(currencies);
            response.setStatus(200);
            response.getWriter().write(currenciesJson);
        } catch (SQLException e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "{Database not avaible.}", gson);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message, Gson gson) throws IOException {
        response.setStatus(statusCode);

        String jsonErrorResponse = gson.toJson(message);
        response.getWriter().write(jsonErrorResponse);
    }
}

