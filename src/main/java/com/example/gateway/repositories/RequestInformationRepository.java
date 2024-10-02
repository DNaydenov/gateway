package com.example.gateway.repositories;

import com.example.gateway.data.RequestInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestInformationRepository extends JpaRepository<RequestInformation, Long> {
    public RequestInformation findByRequestId(String requestId);
}
