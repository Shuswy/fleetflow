package com.fleetflow.order_service.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "request_line_items")
public class RequestLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private MechanicRequest mechanicRequest;

    @Column(nullable = false)
    private Long partId;

    @Column(nullable = false)
    private Integer quantityRequested;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @PrePersist
    private void onPrePersist() {
        if (this.status == null) {
            this.status = RequestStatus.PENDING;
        }
    }

    public RequestLineItem() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MechanicRequest getMechanicRequest() {
        return mechanicRequest;
    }

    public void setMechanicRequest(MechanicRequest mechanicRequest) {
        this.mechanicRequest = mechanicRequest;
    }

    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public Integer getQuantityRequested() {
        return quantityRequested;
    }

    public void setQuantityRequested(Integer quantityRequested) {
        this.quantityRequested = quantityRequested;
    }

    public RequestStatus getStatus() { return status; }

    public void setStatus(RequestStatus status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestLineItem that)) return false;

        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "RequestLineItem{" +
                "id=" + id +
                ", requestId=" + (mechanicRequest != null ? mechanicRequest.getId() : "null") +
                ", partId=" + partId +
                ", quantityRequested=" + quantityRequested +
                ", status=" + status +
                '}';
    }
}