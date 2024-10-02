package com.example.gateway.data;

import java.time.Instant;

//TODO: add limitation to the period
//TODO: add matching pattern for the requestId
public record RequestHistoryDTO(String requestId, Instant timestamp, Integer client, String currency, Integer period) {
}
