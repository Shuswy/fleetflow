package com.fleetflow.order_service.model;

public enum RequestStatus {
    PENDING,
    APPROVED,
    PARTIALLY_APPROVED,
    SHIPPED,
    COMPLETED,
    CANCELLED,
    BACKORDERED
}
