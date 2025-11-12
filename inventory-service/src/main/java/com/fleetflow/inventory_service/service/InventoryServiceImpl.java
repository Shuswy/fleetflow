package com.fleetflow.inventory_service.service;

import com.fleetflow.inventory_service.config.RabbitMQConfig;
import com.fleetflow.inventory_service.dto.RequestCreatedEvent;
import com.fleetflow.inventory_service.dto.StockUpdateEvent;
import com.fleetflow.inventory_service.model.WarehouseStock;
import com.fleetflow.inventory_service.repository.WarehouseStockRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService{

    private final WarehouseStockRepository stockRepository;
    private final RabbitTemplate rabbitTemplate;

    public InventoryServiceImpl(WarehouseStockRepository stockRepository, RabbitTemplate rabbitTemplate) {
        this.stockRepository = stockRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Transactional
    public void checkAndReserveStock(RequestCreatedEvent event) {

        Long warehouseId = event.assignedWarehouseId();
        Long mechanicRequestId = event.mechanicRequestId();

        try {
            for (RequestCreatedEvent.RequestLineItem item : event.lineItems()) {
                WarehouseStock stock = stockRepository.findById_PartIdAndId_WarehouseId(item.partId(), warehouseId)
                        .orElseThrow(() -> new RuntimeException("Part not found: " + item.partId())); // Updated error message

                if (stock.getQuantity() < item.quantity()) {
                    throw new RuntimeException("Insufficient stock for part: " + item.partId());
                }
            }

            for (RequestCreatedEvent.RequestLineItem item : event.lineItems()) {
                WarehouseStock stock = stockRepository.findById_PartIdAndId_WarehouseId(item.partId(), warehouseId).get();
                int newQuantity = stock.getQuantity() - item.quantity();
                stock.setQuantity(newQuantity);
                stockRepository.save(stock);
            }

            var reply = new StockUpdateEvent(mechanicRequestId, StockUpdateEvent.StockUpdateStatus.RESERVED, "All parts reserved.");
            sendReply(reply);

        } catch (InsufficientStockException e) {
            var reply = new StockUpdateEvent(mechanicRequestId, StockUpdateEvent.StockUpdateStatus.BACKORDERED, e.getMessage());
            sendReply(reply);

        } catch (PartNotFoundException e) {
            var reply = new StockUpdateEvent(mechanicRequestId, StockUpdateEvent.StockUpdateStatus.REJECTED_INVALID_PART, e.getMessage());
            sendReply(reply);
        }
    }

    private void sendReply(StockUpdateEvent reply) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.REPLY_EXCHANGE,
                RabbitMQConfig.REPLY_ROUTING_KEY,
                reply
        );
    }

    private static class PartNotFoundException extends RuntimeException {
        public PartNotFoundException(String message) { super(message); }
    }

    private static class InsufficientStockException extends RuntimeException {
        public InsufficientStockException(String message) { super(message); }
    }
}
