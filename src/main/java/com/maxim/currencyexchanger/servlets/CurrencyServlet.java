package com.maxim.currencyexchanger.servlets;

import java.io.*;
import java.sql.SQLException;

import com.maxim.currencyexchanger.DAO.CurrenciesDAO;
import com.maxim.currencyexchanger.Utils.ResponseGenerator;
import com.maxim.currencyexchanger.model.CurrencyDTO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "CurrencyServlet", value = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrenciesDAO dao;
    private ResponseGenerator responseGenerator;

    @Override
    public void init(ServletConfig config) {
        dao = new CurrenciesDAO();
    }


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            responseGenerator = new ResponseGenerator(response);

            String reqURI = request.getRequestURI(); // принимаем параметры в request
            String[] urlParts = reqURI.split("/");
            String targetCurrencyCode = urlParts[urlParts.length - 1].toUpperCase();

            if (isInvalid(targetCurrencyCode)) {
                responseGenerator.codeIsMissing();
                return;
            }

            CurrencyDTO currency = dao.getCurrencyByCode(targetCurrencyCode);

            if (currency == null) { // если не нашлась валюта
                responseGenerator.currencyNotFound();
                return;
            }

            responseGenerator.successResponseGenerator(currency, 200);
        } catch (SQLException e) {
            responseGenerator.DBisNotFound();
        }

    }

    private boolean isInvalid(String targetCurrencyCode) {
        return targetCurrencyCode.length() != 3;
    }
}