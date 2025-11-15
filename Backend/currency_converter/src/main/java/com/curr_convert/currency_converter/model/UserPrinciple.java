package com.curr_convert.currency_converter.model;

import jakarta.annotation.Generated;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class UserPrinciple {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long ID;
    @Column(unique = true)
    private String username;
    private String password;
    private String privateKey;
    @OneToMany(mappedBy = "principle", fetch = FetchType.LAZY)
    private List<UserFrequents> userFrequentsList;
    @OneToMany(mappedBy = "principle" , fetch = FetchType.LAZY)
    private List<UserPreferences> userPreferencesList;

    @Override
    public String toString() {
        return "UserPrinciple{" +
                "privateKey='" + privateKey + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", ID=" + ID +
                '}';
    }
}
