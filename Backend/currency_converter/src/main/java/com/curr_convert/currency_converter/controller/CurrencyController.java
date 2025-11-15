package com.curr_convert.currency_converter.controller;

import com.curr_convert.currency_converter.configs.SecurityCfg;
import com.curr_convert.currency_converter.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;
    @GetMapping("/")
    public List<String> listAvailableCurrencies(){
        return currencyService.getCurrenciesLst();
    }
}
