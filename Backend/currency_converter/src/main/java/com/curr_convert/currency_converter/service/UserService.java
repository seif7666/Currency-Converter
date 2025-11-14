package com.curr_convert.currency_converter.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.curr_convert.currency_converter.dto.PrincipleUserDetails;
import com.curr_convert.currency_converter.exception.JWTExpiredException;
import com.curr_convert.currency_converter.model.UserPrinciple;
import com.curr_convert.currency_converter.repo.UserRepo;

@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepo userRepo;

    public UserService(){
        System.out.println("Called!");
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserPrinciple> userOptional= userRepo.findByUsername(username);
        if (userOptional.isEmpty())
            throw new UsernameNotFoundException(username + " not found!");
        return new PrincipleUserDetails(userOptional.get());           
    }
    
    
    public UserDetails loadUserByJWT(String jwt) throws JWTExpiredException, UsernameNotFoundException {
        /**
         * Get UserDetails from DB.
         */
        throw new UnsupportedOperationException("Unimplemented method 'loadUserByUsername'");
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


}
