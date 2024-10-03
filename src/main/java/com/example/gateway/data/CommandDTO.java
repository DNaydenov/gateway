package com.example.gateway.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "command")
public class CommandDTO {

    @NotBlank(message = "id must not be blank")
    private String id;

    @Valid
    GetElement get;

    @Valid
    HistoryElement history;

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    @XmlElement(name = "get")
    public GetElement getGet() {
        return get;
    }

    @XmlElement(name = "history")
    public HistoryElement getHistory() {
        return history;
    }
}
