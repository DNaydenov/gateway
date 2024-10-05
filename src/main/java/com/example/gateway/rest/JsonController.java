package com.example.gateway.rest;

import com.example.gateway.data.Currency;
import com.example.gateway.data.RequestCurrentDTO;
import com.example.gateway.data.RequestHistoryDTO;
import com.example.gateway.services.DataService;
import com.example.gateway.services.StatisticCollector;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/json_api")
@Validated
public class JsonController {
    private final static String JSON_SERVICE = "json_service";

    private final DataService dataService;
    private final StatisticCollector statisticCollector;

    public JsonController(StatisticCollector statisticCollector, DataService dataService) {
        this.statisticCollector = statisticCollector;
        this.dataService = dataService;
    }

    @RateLimiter(name = "jsonApiRateLimiter")
    @PostMapping("/current")
    public ResponseEntity<String> getCurrent(@Valid @RequestBody RequestCurrentDTO requestCurrentDTO) {
        if (statisticCollector.checkForDuplication(requestCurrentDTO.requestId()))
            return ResponseEntity.badRequest().body("Duplicated request id.");

        statisticCollector.saveRequestInformation(requestCurrentDTO.client(), requestCurrentDTO.requestId(), JSON_SERVICE);

        Currency currency = dataService.getLatestCurrency(requestCurrentDTO.currency());
        if (currency == null) return ResponseEntity.status(NOT_FOUND).body(requestCurrentDTO.currency());

        return ResponseEntity.ok().body(currency.getValue().toString() + " timestamp " + currency.getTimestamp().toString());
    }

    @RateLimiter(name = "jsonApiRateLimiter")
    @PostMapping("/history")
    public ResponseEntity<String> getHistory(@Valid @RequestBody RequestHistoryDTO requestHistoryDTO) {
        if (statisticCollector.checkForDuplication(requestHistoryDTO.requestId()))
            return ResponseEntity.badRequest().body("Duplicated request id.");

        statisticCollector.saveRequestInformation(requestHistoryDTO.client(), requestHistoryDTO.requestId(), JSON_SERVICE);

        List<Currency> currencies = dataService.getAllCurrenciesWithinHours(requestHistoryDTO.currency(), requestHistoryDTO.period());
        if (currencies.isEmpty()) return ResponseEntity.status(NOT_FOUND).body(requestHistoryDTO.currency());

        return ResponseEntity.ok().body(currencies.toString());
    }
}
 
 

