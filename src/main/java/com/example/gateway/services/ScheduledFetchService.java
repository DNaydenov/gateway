package com.example.gateway.services;

import com.example.gateway.data.Currency;
import com.example.gateway.data.ResponseApiDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that periodically fetches currency data from an external API and saves it to the database.
 *
 * <p>
 * This service uses the {@link RatesCollectorService} to fetch the latest currency rates and
 * the {@link DataService} to save the transformed currency data. The fetching is scheduled
 * to occur periodically, based on the interval and initial delay values defined in the application
 * configuration.
 * </p>
 *
 * <p>
 * The scheduling is controlled by the {@link Scheduled} annotation, which reads the values for
 * {@code fixedDelay} and {@code initialDelay} from the application properties:
 * <ul>
 *   <li>{@code api.fetch.interval} - Time in milliseconds between successive fetches.</li>
 *   <li>{@code api.fetch.initial-delay} - Initial delay in milliseconds before the first fetch occurs.</li>
 * </ul>
 * </p>
 *
 * <p>
 * If the API call fails or returns an error, an exception is thrown to notify the failure.
 * </p>
 */
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
        if(rawData == null) {
            throw new RuntimeException("The third part api is down");
        }
        if (!rawData.success()) {
            throw new RuntimeException(rawData.error().info());
        }
        List<Currency> currencies = transformToCurrency(rawData);
        dataService.saveCurrencies(currencies);
    }

    private List<Currency> transformToCurrency(ResponseApiDTO responseApiDTO) {
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
