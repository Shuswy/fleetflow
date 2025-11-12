package com.fleetflow.order_service.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "mechanic_requests")
public class MechanicRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long repairShopId;

    @Column(nullable = false)
    private Long mechanicId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    private Long assignedWarehouseId;

    @OneToMany(mappedBy = "mechanicRequest", cascade = CascadeType.ALL)
    private List<RequestLineItem> lineItems;

    @Column(updatable = false)
    private Instant createdAt;

    private Instant lastUpdatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
        this.lastUpdatedAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.lastUpdatedAt = Instant.now();
    }

    public MechanicRequest() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRepairShopId() {
        return repairShopId;
    }

    public void setRepairShopId(Long repairShopId) {
        this.repairShopId = repairShopId;
    }

    public Long getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(Long mechanicId) {
        this.mechanicId = mechanicId;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Long getAssignedWarehouseId() {
        return assignedWarehouseId;
    }

    public void setAssignedWarehouseId(Long assignedWarehouseId) {
        this.assignedWarehouseId = assignedWarehouseId;
    }

    public List<RequestLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<RequestLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Instant lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public void addLineItem(RequestLineItem lineItem) {
        this.lineItems.add(lineItem);
        lineItem.setMechanicRequest(this);
    }

    public void removeLineItem(RequestLineItem lineItem) {
        this.lineItems.remove(lineItem);
        lineItem.setMechanicRequest(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MechanicRequest that)) return false;

        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "MechanicRequest{" +
                "id=" + id +
                ", repairShopId=" + repairShopId +
                ", mechanicId=" + mechanicId +
                ", status=" + status +
                ", assignedWarehouseId=" + assignedWarehouseId +
                ", createdAt=" + createdAt +
                ", lastUpdatedAt=" + lastUpdatedAt +
                '}';
    }
}
