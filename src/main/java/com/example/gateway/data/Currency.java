package com.example.gateway.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Entity
public class Currency implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    private String code;
    private Double value;
    private Instant timestamp;
    private String base;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
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
        return "code: " + code +
                " value: " + value +
                " timestamp: " + timestamp +
                " date: " + date + "\n";
    }
}
