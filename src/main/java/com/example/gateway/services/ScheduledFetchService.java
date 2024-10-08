package com.example.gateway.services;

import com.example.gateway.data.Currency;
import com.example.gateway.data.ResponseApiDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduledFetchService {
    private final DataService dataService;
    private final RatesCollectorService ratesCollectorService;

    public ScheduledFetchService(DataService dataService, RatesCollectorService ratesCollectorService) {
        this.dataService = dataService;
        this.ratesCollectorService = ratesCollectorService;
    }

    @Scheduled(fixedDelayString = "${api.fetch.interval}", initialDelayString = "${api.fetch.initial-delay}")
    public void fetchDataPeriodically() {
        ResponseApiDTO rawData = ratesCollectorService.fetchCurrencies();
        if (!rawData.success()) {
            throw new RuntimeException(rawData.error().info());
        }
        List<Currency> currencies = transformToCurrency(rawData);
        dataService.saveCurrencies(currencies);
    }

    public List<Currency> transformToCurrency(ResponseApiDTO responseApiDTO) {
        List<Currency> currencyList = new ArrayList<>();
        responseApiDTO.rates().forEach((key, value) -> {
            Currency temp = new Currency();
            temp.setCode(key);
            temp.setValue(value);
            temp.setTimestamp(responseApiDTO.timestamp());
            temp.setDate(responseApiDTO.date());
            currencyList.add(temp);
            temp.setBase(responseApiDTO.base());
        });
        return currencyList;
    }
}
