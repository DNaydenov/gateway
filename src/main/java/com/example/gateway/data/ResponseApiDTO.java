package com.example.gateway.data;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

public record ResponseApiDTO(boolean success, Instant timestamp, String base, Date date, Map<String, Double> rates) {
}
