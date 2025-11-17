package com.fleetflow.order_service.dto;

public record RequestLineItemResponseDTO(
        Long id,
        Long partId,
        Integer quantityRequested,
        String status
) {
}
