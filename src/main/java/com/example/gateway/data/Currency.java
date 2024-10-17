package com.example.gateway.data;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Entity
public class Currency implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    private String name;
    private Double amount;
    private Instant timestamp;
    private String base;

    public String getName() {
        return name;
    }

    public void setName(String code) {
        this.name = code;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double value) {
        this.amount = value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setBase(String base) {
        this.base = base;
    }

    @Override
    public String toString() {
        return "code: " + name +
                " value: " + amount +
                " timestamp: " + timestamp +
                " date: " + date + "\n";
    }
}
