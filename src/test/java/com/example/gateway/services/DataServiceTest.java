package com.example.gateway.services;

import com.example.gateway.data.ResponseApiDTO;
import com.example.gateway.repositories.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;


public class DataServiceTest {

    @InjectMocks
    private DataService dataService;

    @Mock
    private RatesCollectorService ratesCollectorService;

    @Mock
    private CurrencyRepository currencyRepository;

    ResponseApiDTO responseApiDTO;

//    @BeforeEach
    public void setUp() {
        responseApiDTO = new ResponseApiDTO(
                true,
                Instant.now(),
                "EUR",
                Date.from(Instant.now()),
                Map.of("USD", 1.2, "BNG", 0.9)
        );
        MockitoAnnotations.openMocks(this);
    }

//    @Test
    void saveApiData() {

        when(ratesCollectorService.fetchCurrencies()).thenReturn(responseApiDTO);

        // Act: call the method to test
//        dataService.saveApiData();

        // Assert: verify that the data is transformed and saved to the repository
        verify(currencyRepository, times(1)).saveAll(anyList());
    }

}