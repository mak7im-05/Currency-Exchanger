package com.maxim.currencyexchanger.servlets;

import java.io.*;
import java.sql.SQLException;

import com.maxim.currencyexchanger.DAO.CurrenciesDAO;
import com.maxim.currencyexchanger.ResponseGenerator;
import com.maxim.currencyexchanger.model.CurrencyDTO;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "CurrencyServlet", value = "/currency/*")
public class CurrencyServlet extends HttpServlet {

    ResponseGenerator responseGenerator;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        responseGenerator = new ResponseGenerator(response);
        CurrenciesDAO dao = null;
        try {
            dao = new CurrenciesDAO();

            String reqURI = request.getRequestURI(); // принимаем параметры в реквесте
            String[] urlParts = reqURI.split("/");
            String targetCurrencyCode = urlParts[urlParts.length - 1].toUpperCase();

            if (isInvalid(targetCurrencyCode)) {
                responseGenerator.codeIsMissing();
                return;
            }

            CurrencyDTO currency = dao.getCurrencyByCode(targetCurrencyCode);

            if (currency == null) {
                responseGenerator.currencyNotFound();
                return;
            }

            responseGenerator.responseGenerator(currency);
        } catch (SQLException e) {
            responseGenerator.DBisNotFound();
        }
        dao.closeConnection();
    }

    private boolean isInvalid(String targetCurrencyCode) {
        return targetCurrencyCode.length() != 3;
    }
}