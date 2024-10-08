package com.example.gateway.rest;

import com.example.gateway.data.CommandDTO;
import com.example.gateway.data.Currency;
import com.example.gateway.data.HistoryElement;
import com.example.gateway.data.RequestInformation;
import com.example.gateway.services.DataService;
import com.example.gateway.services.RabbitMQProducer;
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

/**
 * REST controller that handles XML-based API requests for executing commands related to currency data.
 *
 * <p>
 * This controller provides an endpoint to process commands sent in XML format. Depending on the command,
 * it can either fetch the latest currency data or fetch historical data for a given period.
 * The response is returned in JSON format.
 * </p>
 *
 * <p>
 * The controller interacts with the following components:
 * <ul>
 *   <li>{@link DataService}: For fetching currency data from the database.</li>
 *   <li>{@link StatisticCollector}: For tracking and storing request information.</li>
 *   <li>{@link RabbitMQProducer}: For sending request information to RabbitMQ for processing.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Rate limiting is applied to the endpoints using the {@link io.github.resilience4j.ratelimiter.annotation.RateLimiter} annotation
 * to prevent excessive requests.
 * </p>
 */
@RestController
@RequestMapping("/xml_api")
@Validated
public class XmlController {
    private final static String XML_SERVICE = "xml_service";
    private final DataService dataService;
    private final StatisticCollector statisticCollector;
    private final RabbitMQProducer rabbitMQProducer;

    public XmlController(DataService dataService, StatisticCollector statisticCollector, RabbitMQProducer rabbitMQProducer) {
        this.dataService = dataService;
        this.statisticCollector = statisticCollector;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    /**
     * Endpoint to execute a command sent in XML format and return a JSON response.
     *
     * <p>
     * This method determines whether the command is for fetching the current currency data or
     * historical data, based on the content of the command. If both 'get' and 'history' fields
     * are present or both are missing, it returns a {@code 400 Bad Request}.
     * </p>
     *
     * <p><strong>Rate Limiting:</strong> This endpoint is rate-limited using the {@code jsonApiRateLimiter} configuration.</p>
     *
     * @param command the command data, including request ID, and either 'get' or 'history' field
     * @return the current or historical currency data, or an error message if the request is invalid
     * @throws IllegalArgumentException if the request body is invalid
     */
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


        RequestInformation requestInfo = statisticCollector.saveRequestInformation(command.getGet().getConsumer(), command.getId(), XML_SERVICE);
        if (requestInfo != null) {
            rabbitMQProducer.sendMessage(requestInfo.toString());
        }

        Currency currency = dataService.getLatestCurrency(command.getGet().getCurrency());
        if (currency == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(currency.toString());
    }

    private ResponseEntity<String> executeHistory(CommandDTO command) {
        if (statisticCollector.checkForDuplication(command.getId()))
            return ResponseEntity.badRequest().body("Duplicated request id.");

        RequestInformation requestInfo = statisticCollector.saveRequestInformation(command.getHistory().getConsumer(), command.getId(), XML_SERVICE);
        if (requestInfo != null) {
            rabbitMQProducer.sendMessage(requestInfo.toString());
        }

        HistoryElement historyElement = command.getHistory();
        List<Currency> currencies = dataService.getAllCurrenciesWithinHours(historyElement.getCurrency(), historyElement.getPeriod());

        if (currencies.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(currencies.toString());
    }
}
