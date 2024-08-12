package com.maxim.currencyexchanger;

import com.google.gson.Gson;
import com.maxim.currencyexchanger.model.CurrencyDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.maxim.currencyexchanger.model.ErrorMessageDTO;

import java.io.IOException;
import java.io.PrintWriter;

public class ResponseGenerator {
    private HttpServletResponse resp;
    Gson gson;

    public ResponseGenerator(HttpServletResponse res, HttpServletRequest req) {
        this.resp = res;
        this.resp.setContentType("application/json");
        this.resp.setCharacterEncoding("UTF-8");
        gson = new Gson();
    }

    public <T> void responseGenerator(T ans) throws IOException {
        String ansJson = gson.toJson(ans);
        resp.setStatus(200);
        PrintWriter out = resp.getWriter();
        out.print(ansJson);
        out.flush();
    }

    public void DBisNotFound () throws IOException {
        ErrorMessageDTO err = new ErrorMessageDTO("База данных недоступна.");
        String errJson = gson.toJson(err);

        resp.setStatus(500);
        PrintWriter out = resp.getWriter();
        out.print(errJson);
        out.flush();
    }

    public void currencyExists() throws IOException {
        ErrorMessageDTO err = new ErrorMessageDTO("Валюта с таким кодом уже уществует");
        String errJson = gson.toJson(err);

        PrintWriter out = resp.getWriter();
        out.print(errJson);
        out.flush();
    }

    public void currencyIsAlreadyExists() throws IOException {
        ErrorMessageDTO err = new ErrorMessageDTO("Валюта с таким кодом уже уществует");
        String errJson = gson.toJson(err);

        resp.setStatus(409);
        PrintWriter out = resp.getWriter();
        out.print(errJson);
        out.flush();
    }

    public void misField() throws IOException {
        ErrorMessageDTO err = new ErrorMessageDTO("Отсутствует нужное поле формы");
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

    public void codeIsIncorrect() throws IOException {
        ErrorMessageDTO err = new ErrorMessageDTO("Некорректно указан код валюты");
        String errJson = gson.toJson(err);

        resp.setStatus(404);
        PrintWriter out = resp.getWriter();
        out.print(errJson);
        out.flush();
    }
}
