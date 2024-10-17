package com.example.gateway.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * DTO used in the requests of the xml api
 */
@XmlRootElement(name = "command")
public class CommandDTO {

    @NotBlank(message = "id must not be blank")
    private String id;

    @Valid
    private GetElement get;

    @Valid
    private HistoryElement history;

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

    public void setId(@NotBlank(message = "id must not be blank") String id) {
        this.id = id;
    }

    public void setGet(@Valid GetElement get) {
        this.get = get;
    }

    public void setHistory(@Valid HistoryElement history) {
        this.history = history;
    }
}
