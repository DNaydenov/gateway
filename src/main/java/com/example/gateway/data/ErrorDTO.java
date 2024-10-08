package com.example.gateway.data;

/**
 * Part of {@link ResponseApiDTO}
 */
public record ErrorDTO(Integer code, String type, String info) {
}
