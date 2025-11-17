package com.fleetflow.order_service.controller;

import com.fleetflow.order_service.dto.MechanicRequestResponseDTO;
import com.fleetflow.order_service.model.RequestStatus;
import com.fleetflow.order_service.service.MechanicRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/logistics")
public class LogisticsController {

    private final MechanicRequestService mechanicRequestService;

    public LogisticsController(MechanicRequestService mechanicRequestService) {
        this.mechanicRequestService = mechanicRequestService;
    }

    @GetMapping("/requests")
    public ResponseEntity<List<MechanicRequestResponseDTO>> getRequestsByStatus(
            @RequestParam("status") String status
    ) {
        RequestStatus requestStatus;

        try {
            requestStatus = RequestStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        List<MechanicRequestResponseDTO> requests = mechanicRequestService.findRequestsByStatus(requestStatus);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/requests/by-item-status")
    public ResponseEntity<List<MechanicRequestResponseDTO>> getRequestsByLineItemStatus(
            @RequestParam("status") String status
    ) {
        RequestStatus requestStatus;
        try {
            requestStatus = RequestStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        List<MechanicRequestResponseDTO> requests = mechanicRequestService.findRequestsByLineItemStatus(requestStatus);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/requests/{requestId}/ship")
    public ResponseEntity<MechanicRequestResponseDTO> shipRequest(
            @PathVariable Long requestId
    ) {
        try {
            MechanicRequestResponseDTO updatedRequest = mechanicRequestService.shipApprovedItems(requestId);
            return ResponseEntity.ok(updatedRequest);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
