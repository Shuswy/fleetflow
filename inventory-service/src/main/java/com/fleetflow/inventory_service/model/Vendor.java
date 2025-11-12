package com.fleetflow.inventory_service.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "vendors")
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public Vendor() {}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vendor vendor)) return false;
        return Objects.equals(name, vendor.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
