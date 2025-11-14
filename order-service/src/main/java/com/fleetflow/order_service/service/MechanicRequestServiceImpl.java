package com.fleetflow.order_service.service;

import com.fleetflow.order_service.config.RabbitMQConfig;
import com.fleetflow.order_service.dto.*;
import com.fleetflow.order_service.model.MechanicRequest;
import com.fleetflow.order_service.model.RequestLineItem;
import com.fleetflow.order_service.model.RequestStatus;
import com.fleetflow.order_service.repository.MechanicRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MechanicRequestServiceImpl implements MechanicRequestService {

    private final MechanicRequestRepository requestRepository;
    private final WebClient.Builder webClientBuilder;
    private final RabbitTemplate rabbitTemplate;

    private static final Logger log = LoggerFactory.getLogger(MechanicRequestServiceImpl.class);

    private final String ORGANIZATION_SERVICE_URL = "http://organization-service";

    @Autowired
    public MechanicRequestServiceImpl(MechanicRequestRepository requestRepository,
                                      WebClient.Builder webClientBuilder,
                                      RabbitTemplate rabbitTemplate) {
        this.requestRepository = requestRepository;
        this.webClientBuilder = webClientBuilder;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Transactional
    public MechanicRequestResponseDTO createRequest(Long mechanicId, Long repairShopId, CreateMechanicRequestDTO requestDTO) {

        RepairShopResponseDTO repairShop;
        try {
            repairShop = webClientBuilder.build()
                    .get()
                    .uri(ORGANIZATION_SERVICE_URL + "/api/repair-shops/" + repairShopId)
                    .retrieve()
                    .bodyToMono(RepairShopResponseDTO.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Could not retrieve repair shop details for ID: " + repairShopId);
        }

        if (repairShop == null || repairShop.assignedWarehouseId() == null) {
            throw new RuntimeException("Repair shop has no assigned warehouse.");
        }

        MechanicRequest request = new MechanicRequest();
        request.setMechanicId(mechanicId);
        request.setRepairShopId(repairShopId);
        request.setStatus(RequestStatus.PENDING);
        request.setAssignedWarehouseId(repairShop.assignedWarehouseId());

        List<RequestLineItem> lineItems = requestDTO.lineItems().stream()
                .map(itemDTO -> {
                    RequestLineItem lineItem = new RequestLineItem();
                    lineItem.setPartId(itemDTO.partId());
                    lineItem.setQuantityRequested(itemDTO.quantityRequested());
                    lineItem.setMechanicRequest(request);
                    return lineItem;
                })
                .collect(Collectors.toList());

        request.setLineItems(lineItems);

        MechanicRequest savedRequest = requestRepository.save(request);

        List<RequestCreatedEvent.RequestLineItem> eventLineItems = savedRequest.getLineItems().stream()
                .map(item -> new RequestCreatedEvent.RequestLineItem(item.getId(), item.getPartId(), item.getQuantityRequested()))
                .collect(Collectors.toList());

        RequestCreatedEvent event = new RequestCreatedEvent(
                savedRequest.getId(),
                savedRequest.getAssignedWarehouseId(),
                eventLineItems
        );

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, event);

        return mapToResponseDTO(savedRequest);
    }

    @Override
    public List<MechanicRequestResponseDTO> findAllByMechanicId(Long mechanicId) {
        return requestRepository.findAllByMechanicId(mechanicId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MechanicRequestResponseDTO findByIdAndMechanicId(Long requestId, Long mechanicId) {
        return requestRepository.findByIdAndMechanicId(requestId, mechanicId)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new RuntimeException("Request not found or you do not have permission."));
    }

    private MechanicRequestResponseDTO mapToResponseDTO(MechanicRequest request) {
        List<RequestLineItemResponseDTO> lineItemDTOs = request.getLineItems().stream()
                .map(item -> new RequestLineItemResponseDTO(
                        item.getId(),
                        item.getPartId(),
                        item.getQuantityRequested()
                        // TODO: Add item.getStatus().name() to RequestLineItemResponseDTO
                ))
                .collect(Collectors.toList());

        return new MechanicRequestResponseDTO(
                request.getId(),
                request.getRepairShopId(),
                request.getMechanicId(),
                request.getStatus().name(),
                request.getAssignedWarehouseId(),
                lineItemDTOs,
                request.getCreatedAt(),
                request.getLastUpdatedAt()
        );
    }

    @Override
    @Transactional
    public void updateRequestStatus(StockUpdateEvent event) {
        log.info("Received stock update for request ID: {}", event.mechanicRequestId());

        MechanicRequest request = requestRepository.findById(event.mechanicRequestId())
                .orElse(null);

        if (request == null) {
            log.error("Received stock update for non-existent request ID: {}", event.mechanicRequestId());
            return;
        }

        Map<Long, RequestLineItem> lineItemMap = request.getLineItems().stream()
                .collect(Collectors.toMap(RequestLineItem::getId, Function.identity()));

        for (StockUpdateEvent.LineItemStatusUpdate itemUpdate : event.itemUpdates()) {
            RequestLineItem lineItem = lineItemMap.get(itemUpdate.lineItemId());
            if (lineItem != null) {
                switch (itemUpdate.status()) {
                    case RESERVED:
                        lineItem.setStatus(RequestStatus.APPROVED);
                        log.info("Request {} APPROVED", request.getId());
                        break;
                    case BACKORDERED:
                        lineItem.setStatus(RequestStatus.BACKORDERED);
                        log.warn("Request {} BACKORDERED. Logistician must procure.", request.getId());
                        break;
                    case REJECTED_INVALID_PART:
                        lineItem.setStatus(RequestStatus.CANCELLED);
                        log.error("Request {} CANCELLED due to: {}", request.getId());
                        break;
                }
            } else {
                log.warn("Received status for non-existent line item ID: {}", itemUpdate.lineItemId());
            }
        }
        Set<RequestStatus> allItemsStatuses = request.getLineItems().stream()
                .map(RequestLineItem::getStatus)
                .collect(Collectors.toSet());

        if (allItemsStatuses.contains(RequestStatus.PENDING)) {
            request.setStatus(RequestStatus.PENDING);
        } else if (allItemsStatuses.size() == 1) {
            request.setStatus(allItemsStatuses.iterator().next());
        } else {
            request.setStatus(RequestStatus.PARTIALLY_APPROVED);
        }

        log.info("Updating parent request {} status to {}", request.getId(), request.getStatus());
        requestRepository.save(request);
    }
}