package com.example.gateway.services;

import com.example.gateway.data.ResponseApiDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RatesCollectorServiceTest {

    @Test
    public void testFetchCurrenciesFailure() {
        RatesCollectorService ratesCollectorService = new RatesCollectorService();

        ResponseApiDTO result = ratesCollectorService.fetchCurrencies();

        assertNotNull(result);
        assertFalse(result.success());
        assertNotNull(result.error());
    }
}
