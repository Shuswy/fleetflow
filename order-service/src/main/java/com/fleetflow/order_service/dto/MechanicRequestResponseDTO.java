package com.fleetflow.order_service.dto;

import java.time.Instant;
import java.util.List;

public record MechanicRequestResponseDTO(
        Long id,
        Long repairShopId,
        Long mechanicId,
        String status,
        Long assignedWarehouseId,
        List<RequestLineItemResponseDTO> lineItems,
        Instant createdAt,
        Instant lastUpdatedAt
) {
}
