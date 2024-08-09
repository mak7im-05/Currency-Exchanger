package com.maxim.currencyexchanger.servlets;

import java.io.*;

import com.google.gson.Gson;
import com.maxim.currencyexchanger.DAO.DAO;
import com.maxim.currencyexchanger.model.Currency;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "CurrencyServlet", value = "/currency/*")
public class CurrencyServlet extends HttpServlet {


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        try {
            DAO dao = new DAO();
            String[] urlParts = request.getRequestURI().split("/");

            if (urlParts.length < 4 || urlParts[3].length() != 3) {
                sendErrorResponse(response, 400, "message: Код валюты отсутствует в адресе", gson);
                return;
            }

            String lastPart = urlParts[urlParts.length - 1];
            Currency currency = dao.getCurrency(lastPart);

            if (currency == null) {
                sendErrorResponse(response, 404, "message: Валюта не найдена", gson);
                return;
            }

            String currencyJson = gson.toJson(currency);
            response.setStatus(200);
            response.getWriter().write(currencyJson);
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "message: Проблема с ДБ", gson);
        }

    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message, Gson gson) throws IOException {
        response.setStatus(statusCode);

        String jsonErrorResponse = gson.toJson(message);
        response.getWriter().write(jsonErrorResponse);
    }
}