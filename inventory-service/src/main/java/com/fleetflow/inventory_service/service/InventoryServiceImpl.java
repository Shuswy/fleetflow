package com.fleetflow.inventory_service.service;

import com.fleetflow.inventory_service.config.RabbitMQConfig;
import com.fleetflow.inventory_service.dto.RequestCreatedEvent;
import com.fleetflow.inventory_service.dto.StockUpdateEvent;
import com.fleetflow.inventory_service.model.WarehouseStock;
import com.fleetflow.inventory_service.repository.WarehouseStockRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService{

    private final WarehouseStockRepository stockRepository;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    public InventoryServiceImpl(WarehouseStockRepository stockRepository, RabbitTemplate rabbitTemplate) {
        this.stockRepository = stockRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Transactional
    public void checkAndReserveStock(RequestCreatedEvent event) {
        Long warehouseId = event.assignedWarehouseId();
        Long mechanicRequestId = event.mechanicRequestId();

        List<StockUpdateEvent.LineItemStatusUpdate> itemUpdates = new ArrayList<>();
        List<RequestCreatedEvent.RequestLineItem> itemsToReserve = new ArrayList<>();

        for (RequestCreatedEvent.RequestLineItem item : event.lineItems()) {
            Optional<WarehouseStock> stockOpt = stockRepository.findById_PartIdAndId_WarehouseId(item.partId(), warehouseId);

            if (stockOpt.isEmpty()) {
                log.warn("Part ID {} not found in warehouse {}", item.partId(), warehouseId);
                itemUpdates.add(new StockUpdateEvent.LineItemStatusUpdate(
                        item.lineItemId(),
                        StockUpdateEvent.StockUpdateStatus.REJECTED_INVALID_PART,
                        "Part not found in catalog"
                ));
            } else {
                WarehouseStock stock = stockOpt.get();
                if (stock.getQuantity() >= item.quantity()) {
                    itemUpdates.add(new StockUpdateEvent.LineItemStatusUpdate(
                            item.lineItemId(),
                            StockUpdateEvent.StockUpdateStatus.RESERVED,
                            "Stock reserved"
                    ));
                    itemsToReserve.add(item);
                } else {
                    log.warn("Part ID {} is out of stock. Needed: {}, Have: {}", item.partId(), item.quantity(), stock.getQuantity());
                    itemUpdates.add(new StockUpdateEvent.LineItemStatusUpdate(
                            item.lineItemId(),
                            StockUpdateEvent.StockUpdateStatus.BACKORDERED,
                            "Out of stock. Available: " + stock.getQuantity()
                    ));
                }
            }
        }

        for (RequestCreatedEvent.RequestLineItem itemToReserve : itemsToReserve) {
            WarehouseStock stock = stockRepository.findById_PartIdAndId_WarehouseId(itemToReserve.partId(), warehouseId).get();
            int newQuantity = stock.getQuantity() - itemToReserve.quantity();
            stock.setQuantity(newQuantity);
            stockRepository.save(stock);
        }

        StockUpdateEvent reply = new StockUpdateEvent(mechanicRequestId, itemUpdates);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.REPLY_EXCHANGE,
                RabbitMQConfig.REPLY_ROUTING_KEY,
                reply
        );
        log.info("Sent stock update reply for request ID: {}", mechanicRequestId);
    }
}
