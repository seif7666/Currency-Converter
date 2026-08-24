package com.curr_convert.currency_converter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
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
@Tag(name = "Users", description = "Registration and login. These two endpoints are open to everyone.")
@SecurityRequirements
public class UserController {
    
    @Autowired
    private UserService userService;

   @Operation(
           summary = "Register a new user",
           description = "Creates an account. The password is stored BCrypt-hashed; usernames are unique.")
   @ApiResponses({
           @ApiResponse(responseCode = "201", description = "User Created!",
                   content = @Content(schema = @Schema(implementation = String.class))),
           @ApiResponse(responseCode = "400", description = "User Exists!",
                   content = @Content(schema = @Schema(implementation = String.class)))
   })
   @PostMapping("/register")
   
    public ResponseEntity<String> registerUser( @RequestBody UserCredentials user){
        System.out.println(user);
        System.out.println("Reached!");
        if(userService.signUp(user.username(), user.password()))
            return new ResponseEntity<>("User Created!",HttpStatus.CREATED);
        return new ResponseEntity<>("User Exists!",HttpStatus.BAD_REQUEST);
        
    }

    @Operation(
            summary = "Log in and receive a JWT",
            description = "Checks the credentials and returns a JWT in the `Token` response header — note " +
                    "the header, not the body. Send it back as `Authorization: Bearer <token>` on the " +
                    "protected endpoints. The token is valid for one hour, and logging in again invalidates " +
                    "any token issued earlier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success! — the JWT is in the `Token` header",
                    headers = @Header(name = "Token", description = "The signed JWT",
                            schema = @Schema(type = "string")),
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "Invalid Username or Password",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User Not Found!",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserCredentials user, HttpServletResponse response){
        String jwt=userService.login(user.username(), user.password());
        if(jwt != null) {
            response.setHeader("Token",jwt);
            return new ResponseEntity<>("Success!", HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid Username or Password",HttpStatus.UNAUTHORIZED);    
    }

}

@Schema(name = "UserCredentials", description = "A username and password pair.")
record UserCredentials(
        @Schema(description = "Unique username", example = "seif") String username,
        @Schema(description = "Plain-text password, hashed before storage", example = "s3cret") String password){};
