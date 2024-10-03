package com.example.gateway.services;

import com.example.gateway.data.RequestInformation;
import com.example.gateway.repositories.RequestInformationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

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

    public boolean checkRequestId(String requestId) {
        return requestInformationRepository.findByRequestId(requestId) != null;
    }

}
