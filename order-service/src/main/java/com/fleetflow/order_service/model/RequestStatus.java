package com.fleetflow.order_service.model;

public enum RequestStatus {
    PENDING,
    APPROVED,
    PARTIALLY_APPROVED,
    SHIPPED,
    SHIPPED_PARTIALLY,
    COMPLETED,
    CANCELLED,
    BACKORDERED
}
