package com.example.gateway.services;

import com.example.gateway.data.ErrorDTO;
import com.example.gateway.data.ResponseApiDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ScheduledFetchServiceTest {

    @Mock
    private DataService dataService;

    @Mock
    private RatesCollectorService ratesCollectorService;

    @InjectMocks
    private ScheduledFetchService scheduledFetchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    @Test
    void fetchDataPeriodically_SuccessfulFetchAndSave() {
        // Mock the ResponseApiDTO object with valid data
        ResponseApiDTO mockResponse = mock(ResponseApiDTO.class);
        when(mockResponse.success()).thenReturn(true);
        when(mockResponse.rates()).thenReturn(Map.of("USD", 1.0, "EUR", 0.85));
        when(mockResponse.timestamp()).thenReturn(Instant.now());
        when(mockResponse.date()).thenReturn(new Date());
        when(mockResponse.base()).thenReturn("USD");

        when(ratesCollectorService.fetchCurrencies()).thenReturn(mockResponse);

        scheduledFetchService.fetchDataPeriodically();

        verify(dataService, times(1)).saveCurrencies(anyList());
    }

    @Test
    void fetchDataPeriodically_ThirdPartyApiIsDown_ThrowsException() {
        when(ratesCollectorService.fetchCurrencies()).thenReturn(null);

        assertThrows(RuntimeException.class, () -> scheduledFetchService.fetchDataPeriodically());
    }

    @Test
    void fetchDataPeriodically_ApiReturnsError_ThrowsException() {
        ResponseApiDTO mockResponse = mock(ResponseApiDTO.class);
        when(mockResponse.success()).thenReturn(false);
        when(mockResponse.error()).thenReturn(new ErrorDTO(101, "Invalid API key", "Invalid API key"));

        when(ratesCollectorService.fetchCurrencies()).thenReturn(mockResponse);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> scheduledFetchService.fetchDataPeriodically());
        assertEquals("Invalid API key", exception.getMessage());
    }
}
