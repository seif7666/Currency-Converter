package com.curr_convert.currency_converter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.curr_convert.currency_converter.model.UserPrinciple;
import com.curr_convert.currency_converter.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;

   @PostMapping("/register")
   
    public ResponseEntity<String> registerUser( @RequestBody UserCredentials user){
        System.out.println(user);
        System.out.println("Reached!");
        if(userService.signUp(user.username(), user.password()))
            return new ResponseEntity<>("User Created!",HttpStatus.CREATED);
        return new ResponseEntity<>("User Exists!",HttpStatus.BAD_REQUEST);
        
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserCredentials user){
        if(userService.login(user.username(), user.password()))
            return new ResponseEntity<>("Success!",HttpStatus.OK);
        return new ResponseEntity<>("Invalid Username or Password",HttpStatus.UNAUTHORIZED);    
    }

}

record UserCredentials(String username, String password){};
