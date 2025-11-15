package com.curr_convert.currency_converter.repo.cache;

import com.curr_convert.currency_converter.dto.CurrencyPair;

import java.util.List;
import java.util.Optional;

public interface CurrencyCache {

    Optional<List<String>> getCurrenciesLst();
    Optional<CurrencyPair> getCurrencyPairRate(CurrencyPair pair);
    void addCurrenciesLst(List<String> currencies);
    void addCurrencyPair(CurrencyPair pair);

}
