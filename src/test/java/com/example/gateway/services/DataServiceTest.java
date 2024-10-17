package com.example.gateway.services;

import com.example.gateway.data.Currency;
import com.example.gateway.repositories.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DataServiceTest {

    private DataService dataService;
    private CurrencyRepository repository;
    private CacheManager cacheManager;
    private Cache cache;

    @BeforeEach
    void setUp() {
        repository = mock(CurrencyRepository.class);
        cacheManager = mock(CacheManager.class);
        cache = mock(Cache.class);
        dataService = new DataService(repository, cacheManager);
    }

    @Test
    void testSaveCurrencies() {
        List<Currency> currencies = Arrays.asList(new Currency(), new Currency());
        when(repository.saveAll(currencies)).thenReturn(currencies);
        when(cacheManager.getCache("latestCurrencies")).thenReturn(cache);

        List<Currency> result = dataService.saveCurrencies(currencies);

        verify(repository).saveAll(currencies);
        currencies.forEach(currency -> verify(cache).put(currency.getName(), currency));
        assertEquals(currencies, result);
    }

    @Test
    void testGetLatestCurrency() {
        Currency currency = new Currency();
        when(repository.findFirstByNameOrderByTimestampDesc("USD")).thenReturn(currency);

        Currency result = dataService.getLatestCurrency("USD");

        verify(repository).findFirstByNameOrderByTimestampDesc("USD");
        assertEquals(currency, result);
    }

    @Test
    void testGetAllCurrenciesWithinHours() {
        List<Currency> currencies = Arrays.asList(new Currency(), new Currency());
        when(repository.findAllByNameAndTimestampAfter(eq("USD"), any(Instant.class)))
                .thenReturn(currencies);

        List<Currency> result = dataService.getAllCurrenciesWithinHours("USD", 24);

        verify(repository).findAllByNameAndTimestampAfter(eq("USD"), any(Instant.class));
        assertEquals(currencies, result);
    }
}
