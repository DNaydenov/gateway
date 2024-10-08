package com.example.gateway.data;

import com.example.gateway.rest.JsonController;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * DTO used in {@link JsonController}
 */
public record RequestCurrentDTO(@NotBlank String requestId, @NotNull Instant timestamp, @NotBlank String client,
                                @NotBlank String currency) {
}
