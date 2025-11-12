package com.fleetflow.user_service.dto;

public record RegisterRequest(String username,
                              String password,
                              String role,
                              Long repairShopId,
                              Long warehouseId) {
}
