package com.example.gateway.services;

import com.example.gateway.data.RequestInformation;
import com.example.gateway.repositories.RequestInformationRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;

/**
 * Service responsible for collecting and managing statistics related to requests.
 *
 * <p>
 * The {@code StatisticCollector} service provides functionalities to:
 * <ul>
 *   <li>Save information about incoming requests</li>
 *   <li>Check for duplicate requests based on request ID</li>
 *   <li>Count the number of requests within a specified time period</li>
 * </ul>
 * <p>
 * This service interacts with the {@link RequestInformationRepository} to store and retrieve
 * request information from the underlying database.
 * </p>
 * <p>
 */
@Service
public class StatisticCollector {
    private final RequestInformationRepository requestInformationRepository;

    public StatisticCollector(RequestInformationRepository requestInformationRepository) {
        this.requestInformationRepository = requestInformationRepository;
    }

    /**
     * Saves information about a request including the client ID, request ID, service name, and timestamp.
     *
     * <p>
     * This method creates a new {@link RequestInformation} object, populates its fields, and saves it
     * in the database using the {@link RequestInformationRepository}.
     * </p>
     *
     * @param customerId  the ID of the customer making the request
     * @param requestId   the unique ID of the request
     * @param serviceName the name of the service handling the request
     * @return the saved {@link RequestInformation} entity
     */
    public RequestInformation saveRequestInformation(String customerId, String requestId, String serviceName) {
        RequestInformation requestInformation = new RequestInformation();
        requestInformation.setClientId(customerId);
        requestInformation.setRequestId(requestId);
        requestInformation.setTime(Instant.now());
        requestInformation.setServiceName(serviceName);

        return requestInformationRepository.save(requestInformation);
    }

    /**
     * Checks if the request with the specified request ID has already been saved (i.e., checks for duplicates).
     *
     * @param requestId the ID of the request to check for duplication
     * @return {@code true} if a request with the given ID already exists, {@code false} otherwise
     */
    public boolean checkForDuplication(String requestId) {
        return requestInformationRepository.findByRequestId(requestId) != null;
    }

    /**
     * Counts the number of requests for the specified service within a given time period.
     *
     * <p>
     * The time period must meet the following conditions:
     * <ul>
     *   <li>The start time cannot be more than 5 days in the past.</li>
     *   <li>The finish time must be after the start time.</li>
     *   <li>The period between start and finish must not exceed 24 hours.</li>
     * </ul>
     * </p>
     *
     * @param serviceName the name of the service for which requests are counted
     * @param start the start time of the period
     * @param finish the end time of the period
     * @return the number of requests within the specified period
     * @throws IllegalArgumentException if the period is invalid (older than 5 days, finish before start, or period longer than 24 hours)
     */
    private Long countRequestsInInstantPeriod(String serviceName, Instant start, Instant finish) {
        if (start.plus(5, DAYS).isBefore(Instant.now()))
            throw new IllegalArgumentException("the period must not be older than 5 days");
        if (finish.isBefore(start)) throw new IllegalArgumentException("finish is before start");
        if (start.plus(24, HOURS).isBefore(finish))
            throw new IllegalArgumentException("the maximal period should be 24h");

        return requestInformationRepository.countAllByServiceNameAndTimeBetween(serviceName, start, finish);
    }

    /**
     * Counts the number of requests for the specified service within a date range.
     *
     * <p>
     * This method converts the provided {@link Date} objects to {@link Instant} and calls
     * {@link #countRequestsInInstantPeriod(String, Instant, Instant)} to perform the counting.
     * </p>
     *
     * @param serviceName the name of the service for which requests are counted
     * @param start the start date of the period
     * @param finish the end date of the period
     * @return the number of requests within the specified date range
     * @throws IllegalArgumentException if the period is invalid
     */
    public Long countRequestsInDatePeriod(@NotBlank String serviceName, @NotNull Date start, @NotNull Date finish) {
        Instant instantStart = start.toInstant();
        Instant instantFinish = finish.toInstant();

        return countRequestsInInstantPeriod(serviceName, instantStart, instantFinish);
    }
}
