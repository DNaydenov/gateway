package com.example.gateway.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAttribute;

public class HistoryElement {

    @NotBlank(message = "consumer must not be blank")
    private String consumer;

    @NotBlank(message = "currency must not be blank")
    private String currency;

    @NotNull(message = "period must not be null")
    private Integer period;

    @XmlAttribute(name = "consumer")
    public String getConsumer() {
        return consumer;
    }

    @XmlAttribute(name = "currency")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @XmlAttribute(name = "period")
    public Integer getPeriod() {
        return period;
    }
}
