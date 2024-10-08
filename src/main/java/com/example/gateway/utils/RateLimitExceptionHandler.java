package com.example.gateway.utils;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

/**
 * Global exception handler for rate-limiting exceptions in the application.
 *
 * <p>
 * This class uses Spring's {@link ControllerAdvice} to handle exceptions globally across the application.
 * Specifically, it handles rate-limiting exceptions thrown by the Resilience4j rate limiter when
 * the number of requests exceeds the defined limit.
 * </p>
 *
 * <p>
 * The exception handler catches instances of {@link RequestNotPermitted} and returns a response with
 * HTTP status {@code 429 TOO_MANY_REQUESTS}.
 * </p>
 * <p>
 * Example response when rate limit is exceeded:
 * <pre>
 * HTTP/1.1 429 TOO_MANY_REQUESTS
 * Content-Type: text/plain
 *
 * Rate limit exceeded. Try again later.
 * </pre>
 */
@ControllerAdvice
public class RateLimitExceptionHandler {

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<String> handleRateLimitException(RequestNotPermitted ignoredException) {
        return new ResponseEntity<>("Rate limit exceeded. Try again later.", TOO_MANY_REQUESTS);
    }
}