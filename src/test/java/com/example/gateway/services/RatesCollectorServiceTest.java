package com.example.gateway.services;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isNotNull;

public class RatesCollectorServiceTest {

//    @Test
    void fetchCurrencies() {
        RatesCollectorService service = new RatesCollectorService(WebClient.builder());
        var a  = service.fetchCurrencies();
        assertThat(a, isNotNull());
    }
}