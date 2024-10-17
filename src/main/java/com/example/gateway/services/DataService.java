package com.example.gateway.services;

import com.example.gateway.data.Currency;
import com.example.gateway.repositories.CurrencyRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Service class responsible for managing currency data.
 * This class interacts with the {@link CurrencyRepository} for
 * data persistence and caching currency information using
 * {@link CacheManager}.
 */
@Service
public class DataService {
    private final CurrencyRepository repository;
    private final CacheManager cacheManager;

    public DataService(CurrencyRepository repository, CacheManager cacheManager) {
        this.repository = repository;
        this.cacheManager = cacheManager;
    }

    /**
     * Save currencies into the database and cache the result in Redis
     *
     * @param currencies list of {@link Currency}
     * @return saved currencies with generated ids
     */
    public List<Currency> saveCurrencies(List<Currency> currencies) {
        List<Currency> savedCurrencies = repository.saveAll(currencies);

        Cache cache = cacheManager.getCache("latestCurrencies");
        if (cache != null) {
            savedCurrencies.forEach(currency -> cache.put(currency.getName(), currency));
        }
        return savedCurrencies;
    }

    /**
     * Check in the Redis for most recent currency that matches currencyCode.
     * If there is no such currency in Redis, query to the database will be created
     *
     * @param currencyCode currency code that the found currency should have
     * @return recent currency in the db that matches the currencyCode
     */
    @Cacheable(value = "latestCurrencies", key = "#currencyCode")
    public Currency getLatestCurrency(String currencyCode) {
        return repository.findFirstByNameOrderByTimestampDesc(currencyCode);
    }

    /**
     * Find all currencies that match currencyCode and creating time no longer than a period of time
     *
     * @param currencyCode currency code that the found currency should have
     * @param period       in hours
     * @return return all currencies that matches requirements
     */
    public List<Currency> getAllCurrenciesWithinHours(String currencyCode, Integer period) {
        Instant instant = (Instant.now().minusSeconds(period * 3600));
        return repository.findAllByNameAndTimestampAfter(currencyCode, instant);
    }
}
