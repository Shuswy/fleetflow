package com.fleetflow.inventory_service.dto;

public record StockUpdateEvent(
        Long mechanicRequestId,
        StockUpdateStatus status,
        String notes
) {
    public enum StockUpdateStatus {
        RESERVED,
        REJECTED_OUT_OF_STOCK,
        REJECTED_PART_NOT_FOUND
    }
}
