package com.fleetflow.order_service.listener;

import com.fleetflow.order_service.config.RabbitMQConfig;
import com.fleetflow.order_service.dto.StockUpdateEvent;
import com.fleetflow.order_service.service.MechanicRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class StockUpdateListener {

    private final MechanicRequestService mechanicRequestService;
    private static final Logger log = LoggerFactory.getLogger(StockUpdateListener.class);

    public StockUpdateListener(MechanicRequestService mechanicRequestService) {
        this.mechanicRequestService = mechanicRequestService;
    }

    @RabbitListener(queues = RabbitMQConfig.INCOMMING_QUEUE)
    public void handleStockUpdate(StockUpdateEvent event) {
        try {
            mechanicRequestService.updateRequestStatus(event);
        } catch (Exception e) {
            log.error("Error processing stock update event: " + event, e);
        }
    }
}
