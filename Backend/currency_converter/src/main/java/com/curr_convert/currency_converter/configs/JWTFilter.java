package com.curr_convert.currency_converter.configs;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

import com.curr_convert.currency_converter.exception.JWTExpiredException;
import com.curr_convert.currency_converter.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Service
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private ApplicationContext context;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionResolver;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String authorizationToken= request.getHeader("Authorization");
            if(authorizationToken!= null && authorizationToken.contains("Bearer ")) {
                authorizationToken = authorizationToken.substring("Bearer ".length());
                UsernamePasswordAuthenticationToken authentication;//= SecurityContextHolder.getContext().getAuthentication();
                UserDetails details = this.context.getBean(UserService.class).loadUserByJWT(authorizationToken);
                authentication = new UsernamePasswordAuthenticationToken(details.getUsername(), details.getPassword(), details.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                authentication.setDetails(details);
            }
            filterChain.doFilter(request, response);

        }catch (ExpiredJwtException | UsernameNotFoundException e){
                exceptionResolver.resolveException(request,response,null,e);
            }
    }
}
