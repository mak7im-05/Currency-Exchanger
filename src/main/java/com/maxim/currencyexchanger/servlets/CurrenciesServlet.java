package com.maxim.currencyexchanger.servlets;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        DAO dao = new DAO();
        Gson gson = new Gson();
        try {
            List<Currency>currencies = dao.getCurrencies(); // получаем список валют
            String usersJson = gson.toJson(currencies); // переводим в json формат
            response.getWriter().write(usersJson);
        } catch (SQLException e) {
            e.printStackTrace();

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "database not avaible");
            String errorJson = gson.toJson(errorResponse);

            response.setStatus(500);
            response.getWriter().write(errorJson);
        }
        }
}