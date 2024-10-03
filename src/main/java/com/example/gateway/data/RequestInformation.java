package com.example.gateway.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class RequestInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String serviceName;
    private String requestId;
    private String clientId;
    private Instant time;

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public void setServiceName(String fromJson) {
        this.serviceName = fromJson;
    }
}
