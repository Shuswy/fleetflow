package com.fleetflow.inventory_service.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
class ShopStockId implements Serializable {
    @Column(name = "part_id")
    private Long partId;

    @Column(name = "shop_id")
    private Long shopId;

    public ShopStockId() {}
    public ShopStockId(Long partId, Long shopId) {
        this.partId = partId;
        this.shopId = shopId;
    }

    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShopStockId shopStockId)) return false;
        return Objects.equals(partId, shopStockId.partId) && Objects.equals(shopId, shopStockId.shopId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partId, shopId);
    }
}

@Entity
@Table(name = "shop_stock")
public class ShopStock {

    @EmbeddedId
    private ShopStockId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("partId")
    @JoinColumn(name = "part_id")
    private Part part;

    @Column(nullable = false)
    private Integer quantity;

    public ShopStock() {}

    public ShopStockId getId() {
        return id;
    }

    public void setId(ShopStockId id) {
        this.id = id;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShopStock that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
