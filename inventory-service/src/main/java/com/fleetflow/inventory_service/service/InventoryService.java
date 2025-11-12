package com.fleetflow.inventory_service.service;

import com.fleetflow.inventory_service.dto.RequestCreatedEvent;

public interface InventoryService {
    void checkAndReserveStock(RequestCreatedEvent event);
}
