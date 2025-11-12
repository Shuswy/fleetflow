package com.fleetflow.order_service.service;

import com.fleetflow.order_service.config.RabbitMQConfig;
import com.fleetflow.order_service.dto.*;
import com.fleetflow.order_service.model.MechanicRequest;
import com.fleetflow.order_service.model.RequestLineItem;
import com.fleetflow.order_service.model.RequestStatus;
import com.fleetflow.order_service.repository.MechanicRequestRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MechanicRequestServiceImpl implements MechanicRequestService {

    private final MechanicRequestRepository requestRepository;
    private final WebClient.Builder webClientBuilder;
    private final RabbitTemplate rabbitTemplate;

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
                .map(item -> new RequestCreatedEvent.RequestLineItem(item.getPartId(), item.getQuantityRequested()))
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
}