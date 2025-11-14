package com.fleetflow.inventory_service.dto;

import java.util.List;

public record StockUpdateEvent(
        Long mechanicRequestId,
        List<LineItemStatusUpdate> itemUpdates
) {
    public record LineItemStatusUpdate(
       Long lineItemId,
       StockUpdateStatus status,
       String notes
    ) {}

    public enum StockUpdateStatus {
        RESERVED,
        BACKORDERED,
        REJECTED_INVALID_PART
    }
}
