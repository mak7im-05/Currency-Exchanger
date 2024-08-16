package com.maxim.currencyexchanger.servlets;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

import com.maxim.currencyexchanger.Utils.ResponseGenerator;
import com.maxim.currencyexchanger.model.CurrencyDTO;

import com.maxim.currencyexchanger.DAO.CurrenciesDAO;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "CurrenciesServlet", value = "/currencies/*")
public class CurrenciesServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
        try {
            CurrenciesDAO dao = new CurrenciesDAO();

            String name = request.getParameter("name");
            String code = request.getParameter("code");
            String sign = request.getParameter("sign");

            if (isInvalidParametrs(name, code, sign)) { //проверка полей параметров
                responseGenerator.misField();
                return;
            }

            CurrencyDTO currency = new CurrencyDTO(0, name, code, sign);

            CurrencyDTO createdCurrency = dao.createCurrency(currency);

            responseGenerator.successResponseGenerator(createdCurrency, 201);
        } catch (SQLException e) {
            if (e.getMessage().equals("DB failed")) {
                responseGenerator.DBisNotFound();
            } else if (e.getMessage().equals("Currency is exists")) {
                responseGenerator.currencyIsAlreadyExists();
            }
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
        try {
            CurrenciesDAO dao = new CurrenciesDAO();

            List<CurrencyDTO> currencies = dao.getCurrencies();

            responseGenerator.successResponseGenerator(currencies, 200);
        } catch (SQLException e) {
            responseGenerator.DBisNotFound();
        }
    }

    private boolean isInvalidParametrs(String name, String code, String sign) {
        return name == null || code == null || sign == null;
    }
}



