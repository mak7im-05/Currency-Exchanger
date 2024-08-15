package com.maxim.currencyexchanger.Utils;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import com.maxim.currencyexchanger.model.ErrorMessageDTO;

import java.io.IOException;
import java.io.PrintWriter;

public class ResponseGenerator {
    private final HttpServletResponse resp;
    Gson gson;

    public ResponseGenerator(HttpServletResponse res) {
        this.resp = res;
        this.resp.setContentType("application/json");
        this.resp.setCharacterEncoding("UTF-8");
        gson = new Gson();
    }

    public <T> void successResponseGenerator(T ans, int statusCode) throws IOException {
        String ansJson = gson.toJson(ans);
        resp.setStatus(statusCode);
        PrintWriter out = resp.getWriter();
        out.print(ansJson);
        out.flush();
    }

    public void DBisNotFound() throws IOException {
        ErrorMessageDTO err = new ErrorMessageDTO("База данных недоступна.");
        String errJson = gson.toJson(err);

        resp.setStatus(500);
        PrintWriter out = resp.getWriter();
        out.print(errJson);
        out.flush();
    }

    public void currencyIsAlreadyExists() throws IOException {
        ErrorMessageDTO err = new ErrorMessageDTO("Валюта/Валютная пара с таким кодом уже уществует");
        String errJson = gson.toJson(err);

        resp.setStatus(409);
        PrintWriter out = resp.getWriter();
        out.print(errJson);
        out.flush();
    }

    public void misField() throws IOException {
        ErrorMessageDTO err = new ErrorMessageDTO("Отсутствует нужное поле формы или содержит недопустимые значения");
        String errJson = gson.toJson(err);

        resp.setStatus(400);
        PrintWriter out = resp.getWriter();
        out.print(errJson);
        out.flush();
    }

    public void codeIsMissing() throws IOException {
        ErrorMessageDTO err = new ErrorMessageDTO("Код валюты отсутствует в адресе");
        String errJson = gson.toJson(err);

        resp.setStatus(400);
        PrintWriter out = resp.getWriter();
        out.print(errJson);
        out.flush();
    }

    public void currencyNotFound() throws IOException {
        ErrorMessageDTO err = new ErrorMessageDTO("Валюта не найдена");
        String errJson = gson.toJson(err);

        resp.setStatus(404);
        PrintWriter out = resp.getWriter();
        out.print(errJson);
        out.flush();
    }

    public void exRatesNotFound() throws IOException {
        ErrorMessageDTO err = new ErrorMessageDTO("Обменный курс для пары не найден");
        String errJson = gson.toJson(err);

        resp.setStatus(404);
        PrintWriter out = resp.getWriter();
        out.print(errJson);
        out.flush();
    }

    public void currencyPairIsNotExist() throws IOException {
        ErrorMessageDTO err = new ErrorMessageDTO("Одна (или обе) валюта из валютной пары не существует в БД");
        String errJson = gson.toJson(err);

        resp.setStatus(404);
        PrintWriter out = resp.getWriter();
        out.print(errJson);
        out.flush();
    }
}
