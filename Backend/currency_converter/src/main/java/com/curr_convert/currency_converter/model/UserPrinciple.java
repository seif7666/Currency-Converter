package com.curr_convert.currency_converter.model;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@ToString
public class UserPrinciple {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    long ID;
    @Column(unique = true)
    String username;
    String password;
    String privateKey;
}
