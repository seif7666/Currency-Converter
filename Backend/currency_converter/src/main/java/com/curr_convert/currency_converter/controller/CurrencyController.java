package com.curr_convert.currency_converter.controller;

import com.curr_convert.currency_converter.configs.SecurityCfg;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CurrencyController {


    @GetMapping("/")
    public String listAvailableCurrencies(){
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return "Returning List...";
    }
}
