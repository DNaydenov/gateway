package com.example.gateway.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

//<command id="1234" >
//<get consumer="13617162" >
//<currency>EUR</currency>
//</get>
//</command>

//<command id="1234-8785" >
//<history consumer="13617162" currency=“EUR” period=”24” />
//</command>
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

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name = "get")
    public GetElement getGet() {
        return get;
    }

    public void setGet(GetElement get) {
        this.get = get;
    }

    @XmlElement(name = "history")
    public HistoryElement getHistory() {
        return history;
    }

    public void setHistory(HistoryElement history) {
        this.history = history;
    }
}
