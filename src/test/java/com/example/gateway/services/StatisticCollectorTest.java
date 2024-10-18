package com.example.gateway.services;

import com.example.gateway.data.RequestInformation;
import com.example.gateway.repositories.RequestInformationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Date;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StatisticCollectorTest {

    @Mock
    private RequestInformationRepository requestInformationRepository;

    @InjectMocks
    private StatisticCollector statisticCollector;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test for saving request information and verifying that it is saved via the repository.
     */
    @Test
    public void testSaveRequestInformation() {
        String customerId = "customer-1";
        String requestId = "request-123";
        String serviceName = "service-test";
        RequestInformation mockRequestInformation = new RequestInformation();
        mockRequestInformation.setClientId(customerId);
        mockRequestInformation.setRequestId(requestId);
        mockRequestInformation.setServiceName(serviceName);
        mockRequestInformation.setTime(Instant.now());

        when(requestInformationRepository.save(any(RequestInformation.class)))
                .thenReturn(mockRequestInformation);

        RequestInformation savedInfo = statisticCollector.saveRequestInformation(customerId, requestId, serviceName);

        assertNotNull(savedInfo);
        assertEquals(customerId, savedInfo.getClientId());
        assertEquals(requestId, savedInfo.getRequestId());
        assertEquals(serviceName, savedInfo.getServiceName());
        verify(requestInformationRepository, times(1)).save(any(RequestInformation.class));
    }

    /**
     * Test for checking duplicate requests by requestId.
     */
    @Test
    public void testCheckForDuplication_RequestExists() {
        String requestId = "request-123";
        RequestInformation mockRequestInformation = new RequestInformation();
        when(requestInformationRepository.findByRequestId(requestId)).thenReturn(mockRequestInformation);

        boolean result = statisticCollector.checkForDuplication(requestId);

        assertTrue(result);
        verify(requestInformationRepository, times(1)).findByRequestId(requestId);
    }

    @Test
    public void testCheckForDuplication_NoRequest() {
        String requestId = "request-456";
        when(requestInformationRepository.findByRequestId(requestId)).thenReturn(null);

        boolean result = statisticCollector.checkForDuplication(requestId);

        assertFalse(result);
        verify(requestInformationRepository, times(1)).findByRequestId(requestId);
    }

    /**
     * Test for counting requests within a valid time period.
     */
    @Test
    public void testCountRequestsInDatePeriod_ValidPeriod() {
        String serviceName = "service-test";
        Date startDate = new Date();
        Date finishDate = Date.from(Instant.now().plus(2, HOURS));

        when(requestInformationRepository.countAllByServiceNameAndTimeBetween(anyString(), any(Instant.class), any(Instant.class)))
                .thenReturn(10L);

        Long count = statisticCollector.countRequestsInDatePeriod(serviceName, startDate, finishDate);

        assertNotNull(count);
        assertEquals(10L, count);
        verify(requestInformationRepository, times(1))
                .countAllByServiceNameAndTimeBetween(anyString(), any(Instant.class), any(Instant.class));
    }

    /**
     * Test for counting requests within a period that is too old (over 5 days).
     */
    @Test
    public void testCountRequestsInDatePeriod_TooOldPeriod() {
        String serviceName = "service-test";
        Date startDate = Date.from(Instant.now().minus(6, DAYS));  // Start date older than 5 days
        Date finishDate = Date.from(Instant.now().plus(1, HOURS));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                statisticCollector.countRequestsInDatePeriod(serviceName, startDate, finishDate)
        );
        assertEquals("the period must not be older than 5 days", exception.getMessage());
    }

    /**
     * Test for counting requests when the finish date is before the start date.
     */
    @Test
    public void testCountRequestsInDatePeriod_FinishBeforeStart() {
        String serviceName = "service-test";
        Date startDate = new Date();
        Date finishDate = Date.from(Instant.now().minus(2, HOURS));  // Finish date is before start

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                statisticCollector.countRequestsInDatePeriod(serviceName, startDate, finishDate)
        );
        assertEquals("finish is before start", exception.getMessage());
    }

    /**
     * Test for counting requests when the period is longer than 24 hours.
     */
    @Test
    public void testCountRequestsInDatePeriod_PeriodTooLong() {
        String serviceName = "service-test";
        Date startDate = new Date();
        Date finishDate = Date.from(Instant.now().plus(25, HOURS));  // Period longer than 24 hours

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                statisticCollector.countRequestsInDatePeriod(serviceName, startDate, finishDate)
        );
        assertEquals("the maximal period should be 24h", exception.getMessage());
    }
}
