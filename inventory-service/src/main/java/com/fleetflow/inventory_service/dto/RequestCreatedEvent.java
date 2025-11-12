package com.fleetflow.inventory_service.dto;

import java.util.List;

public record RequestCreatedEvent(
        Long mechanicRequestId,
        Long assignedWarehouseId,
        List<RequestLineItem> lineItems
) {
    public record RequestLineItem(
       Long partId,
       Integer quantity
    ) {}
}
