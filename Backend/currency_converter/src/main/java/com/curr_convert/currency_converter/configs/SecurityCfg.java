package com.curr_convert.currency_converter.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityCfg {
 
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider provider) throws Exception{
        http
        .csrf(c->c.disable())
        .authorizeHttpRequests(
            a->a
            .requestMatchers("/user/login","/user/register").permitAll()
            .anyRequest().authenticated()
            )
            .authenticationProvider(provider);
            // .addFilterBefore(null, BasicAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService){
        DaoAuthenticationProvider authenticationProvider= new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        return authenticationProvider;
    }
}
