package com.example.gateway.repositories;

import com.example.gateway.data.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    private Currency currency1;
    private Currency currency2;
    private Currency currency3;

    @BeforeEach
    void setUp() {
        currency1 = new Currency();
        currency1.setName("USD");
        currency1.setAmount(100.5);
        currency1.setTimestamp(Instant.now());
        currency1.setDate(new Date());
        currency1.setBase("USD");

        currency2 = new Currency();
        currency2.setName("USD");
        currency2.setAmount(101.5);
        currency2.setTimestamp(Instant.now().minusSeconds(3600)); // 1 hour ago
        currency2.setDate(new Date());
        currency2.setBase("USD");

        currency3 = new Currency();
        currency3.setName("USD");
        currency3.setAmount(101.5);
        currency3.setTimestamp(Instant.now().minusSeconds(7200)); // 2 hour ago
        currency3.setDate(new Date());
        currency3.setBase("USD");

        currencyRepository.saveAll(List.of(currency1,currency2,currency3));
    }

    @Test
    void testFindFirstByNameOrderByTimestampDesc() {
        Currency latestCurrency = currencyRepository.findFirstByNameOrderByTimestampDesc("USD");
        assertNotNull(latestCurrency);
        assertEquals(currency1.getName(), latestCurrency.getName());
        assertEquals(currency1.getAmount(), latestCurrency.getAmount());
        assertTrue(latestCurrency.getTimestamp().isAfter(currency2.getTimestamp()));
        assertTrue(latestCurrency.getTimestamp().isAfter(currency3.getTimestamp()));
    }

    @Test
    void testFindAllByNameAndTimestampAfter() {
        Instant timestamp = Instant.now().minusSeconds(5400); // 90 minutes ago
        List<Currency> currencies = currencyRepository.findAllByNameAndTimestampAfter("USD", timestamp);
        assertNotNull(currencies);
        assertEquals(2, currencies.size()); // Both currency1 and currency2 are after this timestamp
    }

    @Test
    void testFindAllByNameAndTimestampAfter_withOlderTimestamp() {
        Instant timestamp = Instant.now().plusSeconds(7200); // 2 hours into the future
        List<Currency> currencies = currencyRepository.findAllByNameAndTimestampAfter("USD", timestamp);
        assertTrue(currencies.isEmpty()); // No currencies should match this future timestamp
    }
}
