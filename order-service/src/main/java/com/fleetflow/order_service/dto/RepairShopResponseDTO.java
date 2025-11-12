package com.fleetflow.order_service.dto;

public record RepairShopResponseDTO(
        Long id,
        String name,
        Long assignedWarehouseId
) {
}
