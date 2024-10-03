package com.example.gateway.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record RequestCurrentDTO(@NotBlank String requestId, @NotNull Instant timestamp, @NotBlank String client,
                                @NotBlank String currency) {
}
