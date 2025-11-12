package com.fleetflow.inventory_service.listener;

import com.fleetflow.inventory_service.config.RabbitMQConfig;
import com.fleetflow.inventory_service.dto.RequestCreatedEvent;
import com.fleetflow.inventory_service.service.InventoryService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class StockRequestListener {
    private final InventoryService inventoryService;

    public StockRequestListener(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @RabbitListener(queues = RabbitMQConfig.INCOMING_QUEUE)
    public void handleRequestCreated(RequestCreatedEvent event) {
        System.out.println("Received stock check request: " + event.mechanicRequestId());

        inventoryService.checkAndReserveStock(event);
    }
}
