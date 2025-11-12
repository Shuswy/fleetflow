package com.fleetflow.inventory_service.repository;

import com.fleetflow.inventory_service.model.WarehouseStock;
import com.fleetflow.inventory_service.model.WarehouseStockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, WarehouseStockId> {
    Optional<WarehouseStock> findById_PartIdAndId_WarehouseId(Long partId, Long warehouseId);
}
