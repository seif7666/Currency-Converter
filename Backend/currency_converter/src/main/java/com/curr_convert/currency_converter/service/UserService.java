package com.curr_convert.currency_converter.service;

import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.curr_convert.currency_converter.dto.PrincipleUserDetails;
import com.curr_convert.currency_converter.exception.JWTExpiredException;
import com.curr_convert.currency_converter.model.UserPrinciple;
import com.curr_convert.currency_converter.repo.UserRepo;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ApplicationContext context;

    public UserService(){
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserPrinciple> userOptional= userRepo.findByUsername(username);
        if (userOptional.isEmpty())
            throw new UsernameNotFoundException(username + " not found!");
        return new PrincipleUserDetails(userOptional.get());           
    }
    
    
    public PrincipleUserDetails loadUserByJWT(String jwt) throws JWTExpiredException, UsernameNotFoundException {
        String [] splitter= jwt.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(splitter[1]));
        HashMap map=new Gson().fromJson(payload, HashMap.class);
        String userName= map.get("UserName").toString();
        PrincipleUserDetails userDetails= (PrincipleUserDetails) this.loadUserByUsername(userName);
        UserPrinciple principle= ((PrincipleUserDetails)userDetails).getUserPrinciple();
        JwtParser jwtParser = Jwts.parser()
                .verifyWith((SecretKey) getSigningKey(principle.getPrivateKey()))
                .build();
        jwtParser.parse(jwt);

        return userDetails;
    }

    public boolean signUp(String username, String password){
        Optional<UserPrinciple> userOptional= userRepo.findByUsername(username);
        if(userOptional.isPresent())
            return false;
        
        password= new BCryptPasswordEncoder(12).encode(password);
        UserPrinciple userPrinciple= new UserPrinciple();
        userPrinciple.setUsername(username);
        userPrinciple.setPassword(password);
        this.userRepo.save(userPrinciple);
        return true;
    }
    public String login(String username, String password) {
        System.out.println("Called!");
        AuthenticationProvider provider= this.context.getBean(AuthenticationProvider.class);
        Authentication authentication= new UsernamePasswordAuthenticationToken(username, password);   
        authentication=provider.authenticate(authentication);
        if(!authentication.isAuthenticated())
            return  null;
        PrincipleUserDetails userDetails= (PrincipleUserDetails)(this.loadUserByUsername(username));
        UserPrinciple principle =  userDetails.getUserPrinciple();
        String random= Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
        principle.setPrivateKey(random);
        this.userRepo.save(principle);
        return this.generateJWT(principle);
    }

    private String generateJWT(UserPrinciple user){
        long expirationMillis = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
        Date expirationDate = new Date(expirationMillis);
        Key key= this.getSigningKey(user.getPrivateKey());
        return Jwts.builder()
                .claim("UserName", user.getUsername())
                .expiration(expirationDate)
                .signWith(key)
                .compact();
    }

    private Key getSigningKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
