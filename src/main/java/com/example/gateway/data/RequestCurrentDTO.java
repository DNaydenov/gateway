package com.example.gateway.data;

import java.time.Instant;

//TODO: add matching pattern for the requestId
public record RequestCurrentDTO(String requestId, Instant timestamp, Long client, String currency) {
}
