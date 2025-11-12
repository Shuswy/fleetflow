package com.fleetflow.order_service.dto;

public record StockUpdateEvent(
        Long mechanicRequestId,
        StockUpdateStatus status,
        String notes
) {
    public enum StockUpdateStatus {
        RESERVED,
        BACKORDERED,
        REJECTED_INVALID_PART
    }
}
