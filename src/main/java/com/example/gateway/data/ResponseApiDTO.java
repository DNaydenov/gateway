package com.example.gateway.data;

import com.example.gateway.services.ScheduledFetchService;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * DTO used as a response from <a href="https://data.fixer.io">fixer.io</a>
 * @see ScheduledFetchService
 */
public record ResponseApiDTO(boolean success, Instant timestamp, String base,
                             Date date, Map<String, Double> rates, ErrorDTO error) {
}
