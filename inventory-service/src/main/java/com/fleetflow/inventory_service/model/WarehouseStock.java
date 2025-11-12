package com.fleetflow.inventory_service.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "warehouse_stock")
public class WarehouseStock {

    @EmbeddedId
    private WarehouseStockId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("partId")
    @JoinColumn(name = "part_id")
    private Part part;

    @Column(nullable = false)
    private Integer quantity;

    public WarehouseStock() {}

    public WarehouseStockId getId() { return id; }
    public void setId(WarehouseStockId id) { this.id = id; }

    public Part getPart() { return part; }
    public void setPart(Part part) { this.part = part; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WarehouseStock that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
