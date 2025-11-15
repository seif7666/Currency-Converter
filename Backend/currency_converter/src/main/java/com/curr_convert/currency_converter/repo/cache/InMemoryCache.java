package com.curr_convert.currency_converter.repo.cache;

import com.curr_convert.currency_converter.dto.CurrencyPair;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryCache implements CurrencyCache{

    List<String> currencies;
    HashMap<String, CurrencyPair> currencyPairs= new HashMap<>();


    @Override
    public Optional<List<String>> getCurrenciesLst() {
        if(currencies == null)
            return Optional.empty();
        return Optional.of(currencies);
    }

    @Override
    public Optional<CurrencyPair> getCurrencyPairRate(CurrencyPair pair) {
        if(!this.currencyPairs.containsKey(pair.toString()))
            return Optional.empty();

        CurrencyPair cachedCurrencyPair = this.currencyPairs.get(pair.toString());

        if (cachedCurrencyPair.getLastModified().plusHours(2).isBefore(LocalDateTime.now())){
            //Expired Rate
            currencyPairs.remove(pair.toString());
            return Optional.empty();
        }
        return Optional.of(cachedCurrencyPair);
    }

    @Override
    public void addCurrenciesLst(List<String> currencies) {
        this.currencies=currencies;
    }

    @Override
    public void addCurrencyPair(CurrencyPair pair) {
        this.currencyPairs.put(pair.toString(),pair);
    }
}
