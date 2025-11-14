package com.curr_convert.currency_converter.exception;

import java.time.LocalDate;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class JWTExpiredException extends UsernameNotFoundException {

    public JWTExpiredException(String msg) {
        super(msg);
    }
    
}
