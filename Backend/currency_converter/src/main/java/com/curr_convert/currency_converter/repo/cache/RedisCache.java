package com.curr_convert.currency_converter.repo.cache;

import com.curr_convert.currency_converter.dto.CurrencyPair;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.UnifiedJedis;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Primary
public class RedisCache implements CurrencyCache {
    private static final String CURRENCY_LIST_KEY="Currencies";
    private static final String TIME_KEY="RateTime";
    private static final String RATE_KEY="Rate";


    private UnifiedJedis jedis;
    public RedisCache(){
//        jedis = new UnifiedJedis("redis://localhost:6379");
//        System.out.println("Connected To Reddis!");

    }
    @Override
    public Optional<List<String>> getCurrenciesLst() {
        jedis = new UnifiedJedis("redis://localhost:6379");
        long length= jedis.llen(CURRENCY_LIST_KEY);
        return length==0? Optional.empty(): Optional.of(jedis.lrange(CURRENCY_LIST_KEY,0L,length));
    }

    @Override
    public Optional<CurrencyPair> getCurrencyPairRate(CurrencyPair pair) {
        jedis = new UnifiedJedis("redis://localhost:6379");
        Map<String,String> value = jedis.hgetAll(pair.toString());
        if(value.isEmpty())
            return Optional.empty();
        LocalDateTime dateTime= LocalDateTime.parse(value.get(TIME_KEY));
        System.out.println(dateTime);
        if (dateTime.plusSeconds(10).isBefore(LocalDateTime.now())){
            jedis.del(pair.toString());
            return Optional.empty();
        }
        System.out.println("Cache Hit!");
        pair.setRate(Double.parseDouble(value.get(RATE_KEY)));
        return Optional.of(pair);
    }

    @Override
    public void addCurrenciesLst(List<String> currencies) {
        jedis = new UnifiedJedis("redis://localhost:6379");

        for(String curr : currencies)
            jedis.lpush(CURRENCY_LIST_KEY,curr);
    }

    @Override
    public void addCurrencyPair(CurrencyPair pair) {
        jedis = new UnifiedJedis("redis://localhost:6379");

        HashMap<String,String> map = new HashMap<>();
        map.put(TIME_KEY,pair.getLastModified().toString());
        map.put(RATE_KEY,pair.getRate()+"");
        jedis.hset(pair.toString(),map);
    }

    @PreDestroy
    private void closeConnection(){
        jedis.close();
        System.out.println("Reddis Connection Closed!");
    }
}
