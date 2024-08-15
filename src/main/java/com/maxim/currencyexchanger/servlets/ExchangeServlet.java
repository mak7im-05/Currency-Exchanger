package com.maxim.currencyexchanger.servlets;

import com.maxim.currencyexchanger.DAO.CurrenciesDAO;
import com.maxim.currencyexchanger.DAO.ExchangeRatesDAO;
import com.maxim.currencyexchanger.Utils.ResponseGenerator;
import com.maxim.currencyexchanger.model.CurrencyDTO;
import com.maxim.currencyexchanger.model.ExchangeDTO;
import com.maxim.currencyexchanger.model.ExchangeRatesDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

@WebServlet(name = "ExchangeServlet", urlPatterns = "/exchange/*")
public class ExchangeServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator(response);
        CurrenciesDAO currenciesDAO = null;
        ExchangeRatesDAO exRatesDAO = null;
        try {
            currenciesDAO = new CurrenciesDAO();
            exRatesDAO = new ExchangeRatesDAO();

            String baseCurrencyCode = request.getParameter("from"); // принимаем параметры
            String targetCurrencyCode = request.getParameter("to");
            String amountStr = request.getParameter("amount");

            if (invalidParametrs(baseCurrencyCode, targetCurrencyCode, amountStr)) { //проверка параметров
                responseGenerator.misField();
                return;
            }

            BigDecimal amount = new BigDecimal(amountStr);

            if (amount.compareTo(new BigDecimal("0")) != 1) {
                responseGenerator.misField();
                return;
            }

            CurrencyDTO baseCurrency = currenciesDAO.getCurrencyByCode(baseCurrencyCode); // достаем объекты валют , для респонса
            CurrencyDTO targetCurrency = currenciesDAO.getCurrencyByCode(targetCurrencyCode);

            if (baseCurrency == null || targetCurrency == null) { // одна из валют не существует в бд
                responseGenerator.currencyPairIsNotExist();
                return;
            }
            // дальше идет вроде бизнесс-логика и должно находиться в service class, но из-за не большого кол-во строчек кода решил оставить здесь
            ExchangeRatesDTO exRateForward = exRatesDAO.getExchangeRateByCodes(baseCurrencyCode, targetCurrencyCode); // первый случай АB
            ExchangeRatesDTO exRateReverse = exRatesDAO.getExchangeRateByCodes(targetCurrencyCode, baseCurrencyCode); //второй случай BA
            ExchangeRatesDTO usd_A = exRatesDAO.getExchangeRateByCodes("USD", baseCurrencyCode); // 3 случай USD - A & USD - B
            ExchangeRatesDTO usd_B = exRatesDAO.getExchangeRateByCodes("USD", targetCurrencyCode); //все остальные случаи отпускаютсю

            BigDecimal convertedAmount = null;
            BigDecimal rate = null;
            if (exRateForward != null) {
                rate = exRateForward.getRate();
                convertedAmount = rate.multiply(amount).setScale(2, RoundingMode.HALF_UP);
            } else if (exRateReverse != null) {
                rate = new BigDecimal("1").divide(exRateReverse.getRate(), 2, RoundingMode.HALF_UP); // 2 случай (считаем обратный курс 1/rate)
                convertedAmount = rate.multiply(amount).setScale(2, RoundingMode.HALF_UP);
            } else if (usd_A != null && usd_B != null) { // 3 случай
                rate = new BigDecimal("1").divide(usd_A.getRate(), 2, RoundingMode.HALF_UP); // A - USD
                convertedAmount = rate.multiply(amount).setScale(2, RoundingMode.HALF_UP);

                rate = usd_B.getRate();
                convertedAmount = rate.multiply(convertedAmount).setScale(2, RoundingMode.HALF_UP); // USD - B

                rate = convertedAmount.divide(amount, 2, RoundingMode.HALF_UP); // подсчет нового курса
            } else {
                responseGenerator.exRatesNotFound();
                return;
            }

            // записываем все в respEx
            ExchangeDTO respEx = new ExchangeDTO(baseCurrency,
                                                targetCurrency,
                                                rate,
                                                amount,
                                                convertedAmount
                                                );

            responseGenerator.successResponseGenerator(respEx, 200);
        } catch (SQLException e) {
            responseGenerator.DBisNotFound();
        } finally {
            if (currenciesDAO != null) {
                currenciesDAO.closeConnection();
            }
            if (exRatesDAO != null) {
                exRatesDAO.closeConnection();
            }
        }
    }

    private boolean invalidParametrs(String baseCurrencyCode, String targetCurrencyCode, String amountStr) {
        return baseCurrencyCode == null || targetCurrencyCode == null || amountStr == null || baseCurrencyCode.length() != 3 || targetCurrencyCode.length() != 3;
    }
}