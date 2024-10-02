package com.example.gateway.rest;

import com.example.gateway.data.*;
import com.example.gateway.repositories.RequestInformationRepository;
import com.example.gateway.services.DataService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/json_api")
public class CurrencyController {
    private final RequestInformationRepository requestInformationRepository;
    private final DataService dataService;

    public CurrencyController(RequestInformationRepository requestInformationRepository, DataService dataService) {
        this.requestInformationRepository = requestInformationRepository;
        this.dataService = dataService;
    }

    @PostMapping("/current")
    public ResponseEntity<String> getCurrent(@RequestBody RequestCurrentDTO requestCurrentDTO) {
        RequestInformation requestInformation = requestInformationRepository.findByRequestId(requestCurrentDTO.requestId());
        if (requestInformation != null) return ResponseEntity.badRequest().body("Duplicated request id.");

        Currency currency = dataService.getLatestCurrency(requestCurrentDTO.currency());

        if (currency == null) return ResponseEntity.status(NOT_FOUND).body(requestCurrentDTO.currency());

        requestInformation = new RequestInformation();
        requestInformation.setClientId(requestCurrentDTO.client());
        requestInformation.setRequestId(requestCurrentDTO.requestId());
        requestInformation.setTime(requestCurrentDTO.timestamp());
        requestInformation.setFromJson(true);

        requestInformationRepository.save(requestInformation);

        return ResponseEntity.ok().body(currency.getValue().toString() + " timestamp " + currency.getTimestamp().toString());
    }


    @PostMapping("/history")
    public ResponseEntity<String> getHistory(@RequestBody RequestHistoryDTO requestHistoryDTO) {
        List<Currency> currencies = dataService.getAllCurrenciesWithinHours(requestHistoryDTO.currency(),requestHistoryDTO.period());
        return ResponseEntity.ok().body(currencies.toString());
    }

}
 
 

