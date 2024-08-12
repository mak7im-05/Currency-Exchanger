package com.maxim.currencyexchanger.servlets;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

import com.maxim.currencyexchanger.ResponseGenerator;
import com.maxim.currencyexchanger.model.CurrencyDTO;

import com.maxim.currencyexchanger.DAO.CurrenciesDAO;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "CurrenciesServlet", value = "/currencies/*")
public class CurrenciesServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
        CurrencyDTO currency = null;
        CurrenciesDAO dao = null;
        try {
            dao = new CurrenciesDAO();

            String name = request.getParameter("name");
            String code = request.getParameter("code");
            String sign = request.getParameter("sign");

            if (name == null || code == null || sign == null) { //проверка полей параметров
                responseGenerator.misField();
                return;
            }

            currency = new CurrencyDTO(0, name, code, sign);

            CurrencyDTO createdCurrency = dao.createCurrency(currency);

            responseGenerator.responseGenerator(createdCurrency);
        } catch (SQLException e) {
            if (currency.getId() == -1) {
                responseGenerator.currencyIsAlreadyExists();
            } else {
                responseGenerator.DBisNotFound();
            }
        } finally {
            if (dao != null) {
                dao.closeConnection();
            }
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
        CurrenciesDAO dao = null;
        try {
            dao = new CurrenciesDAO();

            List<CurrencyDTO> currencies = dao.getCurrencies();

            responseGenerator.responseGenerator(currencies);
        } catch (SQLException e) {
            responseGenerator.DBisNotFound();
        } finally {
            if (dao != null) {
                dao.closeConnection();
            }
        }
    }
}



