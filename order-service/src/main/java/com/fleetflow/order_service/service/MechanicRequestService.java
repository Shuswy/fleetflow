package com.fleetflow.order_service.service;

import com.fleetflow.order_service.dto.CreateMechanicRequestDTO;
import com.fleetflow.order_service.dto.MechanicRequestResponseDTO;

import java.util.List;

public interface MechanicRequestService {
    MechanicRequestResponseDTO createRequest(
            Long mechanicId,
            Long repairShopId,
            CreateMechanicRequestDTO requestDTO
    );

    List<MechanicRequestResponseDTO> findAllByMechanicId(Long mechanicId);

    MechanicRequestResponseDTO findByIdAndMechanicId(Long requestId, Long mechanicId);
}
