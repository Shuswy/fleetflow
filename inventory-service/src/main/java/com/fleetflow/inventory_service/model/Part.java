package com.fleetflow.inventory_service.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "parts")
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String partNumber;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Integer bufferLevel;

    public Part() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getBufferLevel() {
        return bufferLevel;
    }

    public void setBufferLevel(Integer bufferLevel) {
        this.bufferLevel = bufferLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Part part)) return false;
        return Objects.equals(partNumber, part.partNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partNumber);
    }
}
