package com.curr_convert.currency_converter.repo;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.curr_convert.currency_converter.model.UserPrinciple;

@Repository
public interface UserRepo extends JpaRepository<UserPrinciple,Long>{
    public Optional<UserPrinciple> findByUsername(String userName);
}
