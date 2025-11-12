package com.fleetflow.procurement_service.model;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "vendor_orders")
public class VendorOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long warehouseId;

    @Column(nullable = false)
    private Long logisticianId;

    @Column(nullable = false)
    private Long vendorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VendorOrderStatus status;

    @OneToMany(mappedBy = "vendorOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VendorOrderLineItem> lineItems = new ArrayList<>();

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "estimated_delivery")
    private LocalDate estimatedDelivery;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }

    public VendorOrder() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getLogisticianId() {
        return logisticianId;
    }

    public void setLogisticianId(Long logisticianId) {
        this.logisticianId = logisticianId;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public VendorOrderStatus getStatus() {
        return status;
    }

    public void setStatus(VendorOrderStatus status) {
        this.status = status;
    }

    public List<VendorOrderLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<VendorOrderLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDate getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(LocalDate estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void addLineItem(VendorOrderLineItem lineItem) {
        this.lineItems.add(lineItem);
        lineItem.setVendorOrder(this);
    }

    public void removeLineItem(VendorOrderLineItem lineItem) {
        this.lineItems.remove(lineItem);
        lineItem.setVendorOrder(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VendorOrder that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "VendorOrder{" +
                "id=" + id +
                ", warehouseId=" + warehouseId +
                ", vendorId=" + vendorId +
                ", status=" + status +
                ", totalPrice=" + totalPrice +
                ", createdAt=" + createdAt +
                '}';
    }
}