package com.example.gateway.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

public record ResponseApiDTO(@NotNull boolean success, @NotNull Instant timestamp, @NotBlank String base,
                             @NotNull Date date, @NotNull Map<String, Double> rates) {
}
