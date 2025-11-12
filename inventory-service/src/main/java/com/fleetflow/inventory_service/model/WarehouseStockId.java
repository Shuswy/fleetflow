package com.fleetflow.inventory_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WarehouseStockId implements Serializable {
    @Column(name = "part_id")
    private Long partId;

    @Column(name = "warehouse_id")
    private Long warehouseId;

    public WarehouseStockId() {}
    public WarehouseStockId(Long partId, Long warehouseId) {
        this.partId = partId;
        this.warehouseId = warehouseId;
    }

    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WarehouseStockId warehouseStockId)) return false;
        return Objects.equals(partId, warehouseStockId.partId) && Objects.equals(warehouseId, warehouseStockId.warehouseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partId, warehouseId);
    }
}
