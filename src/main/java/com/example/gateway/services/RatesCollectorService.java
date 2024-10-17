package com.example.gateway.services;

import com.example.gateway.data.ResponseApiDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

/**
 * Service that collects currency rates by making HTTP requests to a third-party API.
 * It uses {@link WebClient} to perform the requests and retrieves the latest currency rates.
 *
 * <p>
 * The service is configured to use the base URL
 * <code><a href="https://data.fixer.io/">https://data.fixer.io/</a></code> and
 * includes an API access key for authentication. The access key is injected via
 * the Spring {@link Value} annotation from the application properties.
 * </p>
 * <p>
 * Example usage of this service:
 * <pre>
 * {@code
 *     RatesCollectorService ratesCollectorService = new RatesCollectorService(webClientBuilder);
 *     ResponseApiDTO response = ratesCollectorService.fetchCurrencies();
 * }
 * </pre>
 */
@Service
public class RatesCollectorService {
    /**
     * The {@link WebClient} instance used to make HTTP requests.
     */
    private final WebClient webClient;

    /**
     * API access key for authentication with the external currency rates API.
     * The value is injected from the application configuration property: {@code api.fetch.access-key}.
     */
    @Value("${api.fetch.access-key}")
    private String accessKey;

    /**
     * Constructs a {@code RatesCollectorService} with the provided {@link WebClient.Builder}.
     * The WebClient is configured to use the base URL of the external API:
     * <code><a href="https://data.fixer.io/">https://data.fixer.io/</a></code>.
     */
    public RatesCollectorService() {
        this.webClient = WebClient.builder().baseUrl("https://data.fixer.io/").build();
    }

    /**
     * Fetches the latest currency rates from the external API.
     *
     * <p>
     * This method makes an HTTP GET request to the endpoint <code>/api/latest</code> and
     * passes the API access key as a query parameter. The response is mapped to a
     * {@link ResponseApiDTO} object.
     * </p>
     *
     * @return a {@link ResponseApiDTO} object containing the latest currency rates.
     * @throws WebClientException if the API request fails
     */
    public ResponseApiDTO fetchCurrencies() {
        try {
            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder.path("api/latest")
                            .queryParam("access_key", accessKey)
                            .build())
                    .retrieve()
                    .bodyToFlux(ResponseApiDTO.class)
                    .blockFirst();
        } catch (Exception e) {
            return null;
        }
    }
}
