package com.maxim.currencyexchanger.model;

public class ErrorMessageDTO {
    private String message;

    public ErrorMessageDTO(String error) {
        this.message = error;
    }

    public String getError() {
        return message;
    }

    public void setError(String error) {
        this.message = error;
    }
}
