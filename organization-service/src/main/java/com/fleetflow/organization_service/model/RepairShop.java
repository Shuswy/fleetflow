package com.fleetflow.organization_service.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "repair_shops")
public class RepairShop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String location;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_warehouse_id", nullable = false)
    private Warehouse assignedWarehouse;

    public RepairShop() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Warehouse getAssignedWarehouse() {
        return assignedWarehouse;
    }

    public void setAssignedWarehouse(Warehouse assignedWarehouse) {
        this.assignedWarehouse = assignedWarehouse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepairShop that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
