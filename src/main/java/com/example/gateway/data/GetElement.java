package com.example.gateway.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

public class GetElement {

    @NotBlank(message = "consumer must not be blank")
    private String consumer;

    @NotBlank(message = "currency must not be blank")
    private String currency;

    @XmlAttribute(name = "consumer")
    public String getConsumer() {
        return consumer;
    }

    @XmlElement(name = "currency")
    public String getCurrency() {
        return currency;
    }
}
