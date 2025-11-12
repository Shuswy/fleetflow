package com.fleetflow.procurement_service.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "vendor_order_line_items")
public class VendorOrderLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_order_id", nullable = false)
    private VendorOrder vendorOrder;

    @Column(nullable = false)
    private Long partId;

    @Column(nullable = false)
    private Integer quantityOrdered;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    public VendorOrderLineItem() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VendorOrder getVendorOrder() {
        return vendorOrder;
    }

    public void setVendorOrder(VendorOrder vendorOrder) {
        this.vendorOrder = vendorOrder;
    }

    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public Integer getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(Integer quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VendorOrderLineItem that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "VendorOrderLineItem{" +
                "id=" + id +
                ", vendorOrderId=" + (vendorOrder != null ? vendorOrder.getId() : "null") +
                ", partId=" + partId +
                ", quantityOrdered=" + quantityOrdered +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
