package com.curr_convert.currency_converter.controller;

import com.curr_convert.currency_converter.exception.JWTExpiredException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String>jwtTokenExpired(){
        return new ResponseEntity<>("Token Expired!",HttpStatus.NOT_EXTENDED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String>userNameNotFound(){
        return new ResponseEntity<>("User Not Found!",HttpStatus.NOT_FOUND);
    }
}
