package com.example.gateway.repositories;

import com.example.gateway.data.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Currency findFirstByNameOrderByTimestampDesc(String name);

    List<Currency> findAllByNameAndTimestampAfter(String name, Instant timestamp);
}
