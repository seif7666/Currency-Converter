package com.curr_convert.currency_converter.service;

import com.curr_convert.currency_converter.dto.CurrencyPair;
import com.curr_convert.currency_converter.dto.PrincipleUserDetails;
import com.curr_convert.currency_converter.model.UserFrequents;
import com.curr_convert.currency_converter.model.UserPrinciple;
import com.curr_convert.currency_converter.repo.UserFrequentsRepo;
import com.curr_convert.currency_converter.repo.UserPreferencesRepo;
import com.curr_convert.currency_converter.repo.cache.CurrencyCache;
import com.curr_convert.currency_converter.service.api.APIConnector;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    @Autowired
    private  UserService userService;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private TransactionTemplate transactionTemplate;
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

    public double computeAmount(CurrencyPair pair, double amount){
        Optional<CurrencyPair> cachedPair=this.cache.getCurrencyPairRate(pair);
        if(cachedPair.isEmpty()){
            double rate=this.apiConnector.getRate();
            pair.setRate(rate);
            this.cache.addCurrencyPair(pair);
            cachedPair= this.cache.getCurrencyPairRate(pair);
        }
        saveFrequentConversion(cachedPair.get(), ((PrincipleUserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails()).getUserPrinciple());
        return cachedPair.get().compute(amount);
    }

    private void saveFrequentConversion(CurrencyPair cachedPair, UserPrinciple userPrinciple) {
        Runnable runnable = () -> {
            UserFrequents userFrequents = new UserFrequents(cachedPair, userPrinciple);
            Optional<UserFrequents>fetched=this.userFrequentsRepo.findByPrincipleAndFromCurrAndToCurr(userPrinciple,cachedPair.getFromCurrency(),cachedPair.getToCurrency());
            if(fetched.isEmpty()){
                saveUserFrequentWithRespectToCount(userFrequents);
            }else{
                fetched.get().setLastUsed(LocalDateTime.now());
                this.userFrequentsRepo.save(fetched.get());
            }

        };
        Thread thread= new Thread(runnable);
        thread.start();
    }

    private void saveUserFrequentWithRespectToCount(UserFrequents userFrequents) {
        int count=this.userFrequentsRepo.findRecordsCountByUserId(userFrequents.getPrinciple());
        userFrequents.setLastUsed(LocalDateTime.now());
        if(count<=3){
            this.userFrequentsRepo.save(userFrequents);
        }
        else{
            //Get Max Timestamp Record
            deleteAndInsertNewFrequent(userFrequents);
            System.out.println("Cannot be Saved!");
        }
    }

    private void deleteAndInsertNewFrequent(UserFrequents userFrequents) {
        LocalDateTime oldestTime= this.userFrequentsRepo.findOldestTimeId(userFrequents.getPrinciple());
        System.out.println(oldestTime);
        transactionTemplate.execute(transactionStatus -> {
            entityManager.createQuery("DELETE FROM UserFrequents u2 WHERE u2.principle=?1 and u2.lastUsed=?2")
                    .setParameter(1,userFrequents.getPrinciple())
                    .setParameter(2,oldestTime)
                    .executeUpdate();
            this.userFrequentsRepo.save(userFrequents);
            return null;
        });
    }
}
