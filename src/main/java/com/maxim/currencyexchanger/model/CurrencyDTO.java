package com.maxim.currencyexchanger.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CurrencyDTO {
    int id;
    private String name;
    private String code;
    private String sign;
}
