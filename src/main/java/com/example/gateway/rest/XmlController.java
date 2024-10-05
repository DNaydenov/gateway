package com.example.gateway.rest;

import com.example.gateway.data.CommandDTO;
import com.example.gateway.data.Currency;
import com.example.gateway.data.HistoryElement;
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

@RestController
@RequestMapping("/xml_api")
@Validated
public class XmlController {
    private final static String XML_SERVICE = "xml_service";
    private final DataService dataService;
    private final StatisticCollector statisticCollector;

    public XmlController(DataService dataService, StatisticCollector statisticCollector) {
        this.dataService = dataService;
        this.statisticCollector = statisticCollector;
    }

    @RateLimiter(name = "jsonApiRateLimiter")
    @PostMapping(value = "/command", consumes = "application/xml", produces = "application/json")
    public ResponseEntity<String> executeCommand(@Valid @RequestBody CommandDTO command) {
        if (command.getGet() != null && command.getHistory() == null) {
            return executeGet(command);
        } else if (command.getGet() == null && command.getHistory() != null) {
            return executeHistory(command);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    private ResponseEntity<String> executeGet(CommandDTO command) {
        if (statisticCollector.checkForDuplication(command.getId()))
            return ResponseEntity.badRequest().body("Duplicated request id.");

        statisticCollector.saveRequestInformation(command.getGet().getConsumer(), command.getId(), XML_SERVICE);

        Currency currency = dataService.getLatestCurrency(command.getGet().getCurrency());
        if (currency == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(currency.toString());
    }

    private ResponseEntity<String> executeHistory(CommandDTO command) {
        if (statisticCollector.checkForDuplication(command.getId()))
            return ResponseEntity.badRequest().body("Duplicated request id.");

        statisticCollector.saveRequestInformation(command.getHistory().getConsumer(), command.getId(), XML_SERVICE);

        HistoryElement historyElement = command.getHistory();
        List<Currency> currencies = dataService.getAllCurrenciesWithinHours(historyElement.getCurrency(), historyElement.getPeriod());

        if (currencies.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(currencies.toString());
    }
}
