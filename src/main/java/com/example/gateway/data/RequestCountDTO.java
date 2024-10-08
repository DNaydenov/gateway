package com.example.gateway.data;

import java.util.Date;

public record RequestCountDTO(String serviceName, Date startTime, Date endTime) {
}
