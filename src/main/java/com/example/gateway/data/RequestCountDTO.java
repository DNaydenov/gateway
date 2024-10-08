package com.example.gateway.data;

import com.example.gateway.rest.StatisticController;

import java.util.Date;

/**
 * DTO used in {@link StatisticController}
 */
public record RequestCountDTO(String serviceName, Date startTime, Date endTime) {
}
