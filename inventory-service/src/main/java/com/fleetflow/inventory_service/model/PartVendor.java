package com.fleetflow.inventory_service.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
class PartVendorId implements Serializable {

    @Column(name = "part_id")
    private Long partId;

    @Column(name = "vendor_id")
    private Long vendorId;

    public PartVendorId() {}
    public PartVendorId(Long partId, Long vendorId) {
        this.partId = partId;
        this.vendorId = vendorId;
    }

    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartVendorId partVendorId)) return false;
        return Objects.equals(partId, partVendorId.partId) && Objects.equals(vendorId, partVendorId.vendorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partId, vendorId);
    }
}

@Entity
@Table(name = "parts_vendors")
public class PartVendor {

    @EmbeddedId
    private PartVendorId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("partId")
    @JoinColumn(name = "part_id")
    private Part part;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vendorId")
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(nullable = false)
    private BigDecimal cost;

    public PartVendor() {}

    public PartVendorId getId() { return id; }
    public void setId(PartVendorId id) { this.id = id; }

    public Part getPart() { return part; }
    public void setPart(Part part) { this.part = part; }

    public Vendor getVendor() { return vendor; }
    public void setVendor(Vendor vendor) { this.vendor = vendor; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartVendor that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}