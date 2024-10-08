package com.example.gateway.rest;

import com.example.gateway.data.RequestCountDTO;
import com.example.gateway.services.StatisticCollector;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that provides endpoints for retrieving request statistics.
 *
 * <p>
 * This controller allows users to fetch the number of requests made to a specific service
 * within a given date range.
 * </p>
 *
 * <p>
 * It interacts with the {@link StatisticCollector} service to perform the counting operation based
 * on the provided request parameters.
 * </p>
 */
@RestController
@RequestMapping("/api")
public class StatisticController {
    private final StatisticCollector statisticCollector;

    public StatisticController(StatisticCollector statisticCollector) {
        this.statisticCollector = statisticCollector;
    }

    /**
     * Endpoint to get the count of requests for a specific service within a date range.
     *
     * <p>
     * The request must include the service name, start time, and end time.
     * If the input parameters are invalid (e.g., invalid dates), a {@code 400 Bad Request} response is returned.
     * </p>
     *
     * @param requestEntity the DTO containing the service name, start time, and end time for the request count
     * @return the number of requests within the specified time period, or an error message if the input is invalid
     */
    @GetMapping("/count")
    public ResponseEntity<String> getRequestCount(@RequestBody RequestCountDTO requestEntity) {
        try {
            Long count = statisticCollector.countRequestsInDatePeriod(requestEntity.serviceName(),
                    requestEntity.startTime(), requestEntity.endTime());
            return ResponseEntity.ok().body(count.toString());
        } catch (
                Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
