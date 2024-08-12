package com.maxim.currencyexchanger.model;

public class CurrencyDTO {
    private int id;
    private String name;
    private String code;
    private String sign;

    public CurrencyDTO(int id, String fullName, String code, String sign) {
        this.id = id;
        this.name = fullName;
        this.code = code;
        this.sign = sign;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSign() {
        return sign;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }


}
