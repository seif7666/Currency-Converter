package com.curr_convert.currency_converter.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Preferences")
@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class UserPreferences {
    @Id
    @GeneratedValue
    long ID;
    @ManyToOne
    UserPrinciple principle;
    @Column(length = 3)
    String fromCurr;
    @Column(length = 3)
    String toCurr;

}
