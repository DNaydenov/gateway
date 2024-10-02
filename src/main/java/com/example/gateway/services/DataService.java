package com.example.gateway.services;

import com.example.gateway.data.Currency;
import com.example.gateway.repositories.CurrencyRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class DataService {
    private final CurrencyRepository repository;
    private final CacheManager cacheManager;

    public DataService(CurrencyRepository repository, CacheManager cacheManager)
    {
        this.repository = repository;
        this.cacheManager = cacheManager;
    }

//    @CachePut(value = "latestCurrencies")
    public List<Currency> saveCurrencies(List<Currency> currencies) {
        List<Currency> savedCurrencies =  repository.saveAll(currencies);

        Cache cache = cacheManager.getCache("latestCurrencies");
        if (cache != null) {
            savedCurrencies.forEach(currency -> cache.put(currency.getCode(), currency));
        }
        return savedCurrencies;
    }

    @CachePut(value = "latestCurrencies", key = "#currency.code")
    public Currency saveCurrency(Currency currency) {
        return repository.save(currency);
    }

    @Cacheable(value = "latestCurrencies", key = "#currencyCode")
    public Currency getLatestCurrency(String currencyCode) {
        return repository.findFirstByCodeOrderByTimestampDesc(currencyCode);
    }

    public List<Currency> getAllCurrenciesWithinHours(String currencyCode, Integer period) {
        Instant instant = (Instant.now().minusSeconds(period*3600));
        return repository.findAllByCodeAndTimestampAfter(currencyCode, instant);
    }
}
