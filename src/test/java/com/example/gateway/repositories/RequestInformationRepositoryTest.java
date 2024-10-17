package com.example.gateway.repositories;

import com.example.gateway.data.RequestInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RequestInformationRepositoryTest {

    @Autowired
    private RequestInformationRepository repository;

    private RequestInformation requestInfo1;

    @BeforeEach
    void setUp() {
        requestInfo1 = new RequestInformation();
        requestInfo1.setRequestId("REQ123");
        requestInfo1.setServiceName("ServiceA");
        requestInfo1.setClientId("Client1");
        requestInfo1.setTime(Instant.now().minusSeconds(3600)); // 1 hour ago

        RequestInformation requestInfo2 = new RequestInformation();
        requestInfo2.setRequestId("REQ124");
        requestInfo2.setServiceName("ServiceA");
        requestInfo2.setClientId("Client2");
        requestInfo2.setTime(Instant.now().minusSeconds(1800)); // 30 minutes ago

        RequestInformation requestInfo3 = new RequestInformation();
        requestInfo3.setRequestId("REQ125");
        requestInfo3.setServiceName("ServiceB");
        requestInfo3.setClientId("Client3");
        requestInfo3.setTime(Instant.now()); // Now

        repository.saveAll(List.of(requestInfo1, requestInfo2, requestInfo3));
    }

    @Test
    void testFindByRequestId() {
        RequestInformation result = repository.findByRequestId(requestInfo1.getRequestId());
        assertNotNull(result);
        assertEquals(requestInfo1.getRequestId(), result.getRequestId());
        assertEquals(requestInfo1.getServiceName(), result.getServiceName());
        assertEquals(requestInfo1.getClientId(), result.getClientId());
    }

    @Test
    void testFindByRequestId_NotFound() {
        RequestInformation result = repository.findByRequestId("NON_EXISTENT");
        assertNull(result);
    }

    @Test
    void testCountAllByServiceNameAndTimeBetween() {
        // Define a time range: 2 hours ago to now
        Instant start = Instant.now().minusSeconds(7200); // 2 hours ago
        Instant end = Instant.now();

        // Test countAllByServiceNameAndTimeBetween
        Long count = repository.countAllByServiceNameAndTimeBetween("ServiceA", start, end);
        assertEquals(2, count);
    }

    @Test
    void testCountAllByServiceNameAndTimeBetween_NoMatch() {
        // Define a future time range where no entries exist
        Instant start = Instant.now().plusSeconds(3600); // 1 hour in the future
        Instant end = Instant.now().plusSeconds(7200);   // 2 hours in the future

        // Test no match
        Long count = repository.countAllByServiceNameAndTimeBetween("ServiceA", start, end);
        assertEquals(0, count);  // No records should match
    }
}
