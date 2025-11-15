package com.curr_convert.currency_converter.dto;

import java.util.Collection;
import java.util.List;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.curr_convert.currency_converter.model.UserPrinciple;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Data
public class PrincipleUserDetails implements UserDetails {

    private UserPrinciple userPrinciple;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getPassword() {
        return userPrinciple.getPassword();
    }

    @Override
    public String getUsername() {
        return userPrinciple.getUsername();
    }
    
}
