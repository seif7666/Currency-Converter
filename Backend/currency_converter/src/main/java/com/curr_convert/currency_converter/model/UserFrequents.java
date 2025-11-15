package com.curr_convert.currency_converter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Frequents")
@Data
public class UserFrequents extends UserPreferences{
    private LocalDateTime lastUsed;
}
