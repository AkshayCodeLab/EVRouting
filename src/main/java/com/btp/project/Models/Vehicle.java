package com.btp.project.Models;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private float efficiency;

    protected Vehicle() {};

    public Vehicle(String name, float efficiency) {
        this.name = name;
        this.efficiency = efficiency;
    }

    @Override
    public String toString() {
        return String.format("Vehicle[id=%d, name='%s', efficiency='%s']", id, name, efficiency);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getEfficiency() {
        return efficiency;
    }
}
