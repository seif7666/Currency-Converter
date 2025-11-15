package com.curr_convert.currency_converter.service;

import com.curr_convert.currency_converter.dto.CurrencyPair;
import com.curr_convert.currency_converter.model.UserFrequents;
import com.curr_convert.currency_converter.model.UserPrinciple;
import com.curr_convert.currency_converter.repo.UserFrequentsRepo;
import com.curr_convert.currency_converter.repo.UserPreferencesRepo;
import com.curr_convert.currency_converter.repo.cache.CurrencyCache;
import com.curr_convert.currency_converter.service.api.APIConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService {
    @Autowired
    private CurrencyCache cache;
    @Autowired
    private APIConnector apiConnector;
    @Autowired
    private UserPreferencesRepo userPreferencesRepo;
    @Autowired
    private UserFrequentsRepo userFrequentsRepo;
    public List<String> getCurrenciesLst(){
        if(this.cache.getCurrenciesLst().isEmpty()){
            List<String> currencies= this.apiConnector.getCurrencies();
            if(currencies!= null)
                this.cache.addCurrenciesLst(currencies);
        }
        return this.cache.getCurrenciesLst().orElse(null);
    }

    public List<CurrencyPair> getUserPreferences(UserPrinciple userPrinciple){
        throw new RuntimeException("Not Implemented");
    }
    public List<CurrencyPair> getUserFrequents(UserPrinciple userPrinciple){
        throw new RuntimeException("Not Implemented");
    }

    public double computeAmount(CurrencyPair pair){
        throw new RuntimeException("Not Implemented");
    }

}
