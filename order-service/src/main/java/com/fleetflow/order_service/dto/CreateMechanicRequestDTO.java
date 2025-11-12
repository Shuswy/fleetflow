package com.fleetflow.order_service.dto;

import java.util.List;

public record CreateMechanicRequestDTO(
        List<RequestLineItemInputDTO> lineItems) {
}
