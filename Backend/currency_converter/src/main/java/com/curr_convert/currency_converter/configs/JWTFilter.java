package com.curr_convert.currency_converter.configs;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

import com.curr_convert.currency_converter.service.UserService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component("jwt")
public class JWTFilter implements Filter{

    @Autowired
    private UserService userService;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request1= (HttpServletRequest)request;
        String authorizationToken= request1.getHeader("Authorization");
        if(authorizationToken!= null && authorizationToken.contains("Bearer ")) {
            authorizationToken= authorizationToken.substring("Bearer ".length());
            System.out.printf("{%s}\n", authorizationToken);
            UsernamePasswordAuthenticationToken authentication ;//= SecurityContextHolder.getContext().getAuthentication();
            UserDetails details = userService.loadUserByJWT(authorizationToken);
            authentication = new UsernamePasswordAuthenticationToken(details.getUsername(), details.getPassword(), details.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            authentication.setDetails(details);
        }
        chain.doFilter(request,response);
    }
    
}
