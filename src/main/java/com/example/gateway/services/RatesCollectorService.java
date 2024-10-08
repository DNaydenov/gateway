package com.example.gateway.services;

import com.example.gateway.data.ResponseApiDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RatesCollectorService {
    private final WebClient webClient;
    private final static String ACCESS_KEY = "1e6eda390fa88456d9391a3b878980f2";

    public RatesCollectorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://data.fixer.io/")
                .build();
    }

    public ResponseApiDTO fetchCurrencies() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("api/latest")
                        .queryParam("access_key", ACCESS_KEY)
                        .build())
                .retrieve()
                .bodyToFlux(ResponseApiDTO.class)
                .blockFirst();
    }
}
