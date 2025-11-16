package com.curr_convert.currency_converter.model;

import com.curr_convert.currency_converter.dto.CurrencyPair;
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

    public UserFrequents(CurrencyPair cachedPair, UserPrinciple userPrinciple) {
        super();
        super.setPrinciple(userPrinciple);
        super.setFromCurr(cachedPair.getFromCurrency());
        super.setToCurr(cachedPair.getToCurrency());
    }
}
