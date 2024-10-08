package com.example.gateway.services;

import com.example.gateway.data.ResponseApiDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RatesCollectorService {
    private final WebClient webClient;

    @Value("${api.fetch.access-key}")
    private String accessKey;

    public RatesCollectorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://data.fixer.io/")
                .build();
    }

    public ResponseApiDTO fetchCurrencies() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("api/latest")
                        .queryParam("access_key", accessKey)
                        .build())
                .retrieve()
                .bodyToFlux(ResponseApiDTO.class)
                .blockFirst();
    }
}
