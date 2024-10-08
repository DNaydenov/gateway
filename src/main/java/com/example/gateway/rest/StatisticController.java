package com.example.gateway.rest;

import com.example.gateway.data.RequestCountDTO;
import com.example.gateway.services.StatisticCollector;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StatisticController {
    private final StatisticCollector statisticCollector;

    public StatisticController(StatisticCollector statisticCollector) {
        this.statisticCollector = statisticCollector;
    }

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
