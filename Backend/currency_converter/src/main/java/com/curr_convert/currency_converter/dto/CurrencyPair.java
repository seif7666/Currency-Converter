package com.curr_convert.currency_converter.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@NoArgsConstructor
@Data
public class CurrencyPair {
    private String fromCurrency;
    private String toCurrency;
    private double rate;
    private LocalDateTime lastModified;

    public CurrencyPair(String fromCurrency, String toCurrency){
        this.fromCurrency= fromCurrency;
        this.toCurrency= toCurrency;
    }
    public void setRate(double rate){
        this.rate= rate;
        this.lastModified= LocalDateTime.now();
    }

    public double compute(double amount){
        return amount*this.rate;
    }

    @Override
    public String toString() {
        return fromCurrency+":"+toCurrency;
    }
}
