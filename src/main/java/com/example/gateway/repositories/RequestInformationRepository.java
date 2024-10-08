package com.example.gateway.repositories;

import com.example.gateway.data.RequestInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface RequestInformationRepository extends JpaRepository<RequestInformation, Long> {
    RequestInformation findByRequestId(String requestId);
    Long countAllByServiceNameAndTimeBetween(String serviceName, Instant start, Instant end);
}
