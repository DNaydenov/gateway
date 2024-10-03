package com.example.gateway.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

//TODO: add limitation to the period
//TODO: add matching pattern for the requestId
public record RequestHistoryDTO(@NotBlank String requestId, @NotNull Instant timestamp, @NotBlank String client,
                                @NotBlank String currency, @NotNull Integer period) {
}
