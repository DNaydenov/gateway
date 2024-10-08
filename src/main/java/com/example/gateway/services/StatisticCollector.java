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

@Service
public class StatisticCollector {
    private final RequestInformationRepository requestInformationRepository;

    public StatisticCollector(RequestInformationRepository requestInformationRepository) {
        this.requestInformationRepository = requestInformationRepository;
    }

    public RequestInformation saveRequestInformation(String customerId, String requestId, String serviceName) {
        RequestInformation requestInformation = new RequestInformation();
        requestInformation.setClientId(customerId);
        requestInformation.setRequestId(requestId);
        requestInformation.setTime(Instant.now());
        requestInformation.setServiceName(serviceName);

        return requestInformationRepository.save(requestInformation);
    }

    public boolean checkForDuplication(String requestId) {
        return requestInformationRepository.findByRequestId(requestId) != null;
    }

    private Long countRequestsInInstantPeriod(String serviceName, Instant start, Instant finish) {
        if (start.plus(5, DAYS).isBefore(Instant.now()))
            throw new IllegalArgumentException("the period must not be older than 5 days");
        if (finish.isBefore(start)) throw new IllegalArgumentException("finish is before start");
        if (start.plus(24, HOURS).isBefore(finish))
            throw new IllegalArgumentException("the maximal period should be 24h");

        return requestInformationRepository.countAllByServiceNameAndTimeBetween(serviceName, start, finish);
    }

    public Long countRequestsInDatePeriod(@NotBlank String serviceName, @NotNull Date start, @NotNull Date finish) {
        Instant instantStart = start.toInstant();
        Instant instantFinish = finish.toInstant();

        return countRequestsInInstantPeriod(serviceName, instantStart, instantFinish);
    }
}
