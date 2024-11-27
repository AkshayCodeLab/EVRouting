package com.btp.project.Repository;

import org.springframework.data.repository.CrudRepository;

import com.btp.project.Models.Vehicle;

public interface VehicleRepository extends CrudRepository<Vehicle, Long>{

    Vehicle findByName(String name);

    Vehicle findById(long id);
    
}