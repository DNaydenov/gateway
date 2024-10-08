package com.example.gateway.rest;

import com.example.gateway.data.Currency;
import com.example.gateway.data.RequestCurrentDTO;
import com.example.gateway.data.RequestHistoryDTO;
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

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * REST controller that handles JSON API requests for fetching current and historical currency rates.
 *
 * <p>
 * This controller provides two main endpoints:
 * <ul>
 *   <li>{@code /json_api/current}: Fetches the current currency rate.</li>
 *   <li>{@code /json_api/history}: Fetches historical currency rates within a specified period.</li>
 * </ul>
 * </p>
 *
 * <p>
 * The controller interacts with the following components:
 * <ul>
 *   <li>{@link DataService}: For fetching currency data from the database.</li>
 *   <li>{@link StatisticCollector}: For tracking and storing request information.</li>
 *   <li>{@link RabbitMQProducer}: For sending request information to a message queue.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Rate limiting is applied to the endpoints using the {@link io.github.resilience4j.ratelimiter.annotation.RateLimiter} annotation
 * to prevent excessive requests.
 * </p>
 */
@RestController
@RequestMapping("/json_api")
@Validated
public class JsonController {
    private final static String JSON_SERVICE = "json_service";

    private final DataService dataService;
    private final StatisticCollector statisticCollector;
    private final RabbitMQProducer rabbitMQProducer;

    public JsonController(StatisticCollector statisticCollector, DataService dataService, RabbitMQProducer rabbitMQProducer) {
        this.statisticCollector = statisticCollector;
        this.dataService = dataService;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    /**
     * Endpoint to fetch the current currency rate based on the provided request.
     *
     * <p>
     * The request must include a client ID, a unique request ID, and a currency code.
     * If the request ID has already been processed, the method will return a {@code 400 Bad Request} response.
     * If the currency is not found, it will return a {@code 404 Not Found} response.
     * </p>
     *
     * <p>
     * The request information is logged and sent to RabbitMQ for tracking purposes.
     * </p>
     *
     * <p><strong>Rate Limiting:</strong> This endpoint is rate-limited using the {@code jsonApiRateLimiter} configuration.</p>
     *
     * @param requestCurrentDTO the request data, including client ID, request ID, and currency code
     * @return the current value of the requested currency, or an error message if the request is invalid or the currency is not found
     * @throws IllegalArgumentException if the request body is invalid
     */
    @RateLimiter(name = "jsonApiRateLimiter")
    @PostMapping("/current")
    public ResponseEntity<String> getCurrent(@Valid @RequestBody RequestCurrentDTO requestCurrentDTO) {
        if (statisticCollector.checkForDuplication(requestCurrentDTO.requestId()))
            return ResponseEntity.badRequest().body("Duplicated request id.");

        RequestInformation requestInfo = statisticCollector.saveRequestInformation(requestCurrentDTO.client(), requestCurrentDTO.requestId(), JSON_SERVICE);
        if (requestInfo != null) {
            rabbitMQProducer.sendMessage(requestInfo.toString());
        }

        Currency currency = dataService.getLatestCurrency(requestCurrentDTO.currency());
        if (currency == null) return ResponseEntity.status(NOT_FOUND).body(requestCurrentDTO.currency());

        return ResponseEntity.ok().body(currency.getValue().toString() + " timestamp " + currency.getTimestamp().toString());
    }

    /**
     * Endpoint to fetch historical currency rates for a specific period.
     *
     * <p>
     * The request must include a client ID, a unique request ID, a currency code, and a time period.
     * If the request ID has already been processed, the method will return a {@code 400 Bad Request} response.
     * If no results are found for the currency in the given period, a message will be returned indicating no results.
     * </p>
     *
     * <p>
     * The request information is logged and sent to RabbitMQ for tracking purposes.
     * </p>
     *
     * <p><strong>Rate Limiting:</strong> This endpoint is rate-limited using the {@code jsonApiRateLimiter} configuration.</p>
     *
     * @param requestHistoryDTO the request data, including client ID, request ID, currency code, and time period
     * @return the historical currency data for the specified period, or a message indicating no results found
     * @throws IllegalArgumentException if the request body is invalid
     */
    @RateLimiter(name = "jsonApiRateLimiter")
    @PostMapping("/history")
    public ResponseEntity<String> getHistory(@Valid @RequestBody RequestHistoryDTO requestHistoryDTO) {
        if (statisticCollector.checkForDuplication(requestHistoryDTO.requestId()))
            return ResponseEntity.badRequest().body("Duplicated request id.");

        RequestInformation requestInfo = statisticCollector.saveRequestInformation(requestHistoryDTO.client(), requestHistoryDTO.requestId(), JSON_SERVICE);
        if (requestInfo != null) {
            rabbitMQProducer.sendMessage(requestInfo.toString());
        }

        List<Currency> currencies = dataService.getAllCurrenciesWithinHours(requestHistoryDTO.currency(), requestHistoryDTO.period());
        if (currencies.isEmpty()) return ResponseEntity.ok("no results in for that currency in that period");

        return ResponseEntity.ok().body(currencies.toString());
    }
}
 
 

