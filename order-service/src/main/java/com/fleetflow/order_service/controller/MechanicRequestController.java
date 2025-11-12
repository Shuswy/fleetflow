package com.fleetflow.order_service.controller;

import com.fleetflow.order_service.dto.CreateMechanicRequestDTO;
import com.fleetflow.order_service.dto.MechanicRequestResponseDTO;
import com.fleetflow.order_service.service.MechanicRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests") // No "/v1" as requested
public class MechanicRequestController {

    private final MechanicRequestService mechanicRequestService;

    public MechanicRequestController(MechanicRequestService mechanicRequestService) {
        this.mechanicRequestService = mechanicRequestService;
    }

    @PostMapping
    public ResponseEntity<MechanicRequestResponseDTO> createMechanicRequest(
            @RequestBody CreateMechanicRequestDTO requestDTO,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long mechanicId = Long.parseLong(jwt.getSubject());
        Object repairShopIdClaim = jwt.getClaim("repairShopId");

        if (repairShopIdClaim == null) {
            return ResponseEntity.badRequest().build();
        }

        Long repairShopId = ((Number) repairShopIdClaim).longValue();

        MechanicRequestResponseDTO newRequest = mechanicRequestService.createRequest(
                mechanicId,
                repairShopId,
                requestDTO
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(newRequest);
    }

    @GetMapping
    public ResponseEntity<List<MechanicRequestResponseDTO>> getMyRequests(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long mechanicId = Long.parseLong(jwt.getSubject());

        List<MechanicRequestResponseDTO> requests = mechanicRequestService.findAllByMechanicId(mechanicId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<MechanicRequestResponseDTO> getRequestById(
            @PathVariable Long requestId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long mechanicId = Long.parseLong(jwt.getSubject());

        try {
            MechanicRequestResponseDTO request = mechanicRequestService.findByIdAndMechanicId(requestId, mechanicId);
            return ResponseEntity.ok(request);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}