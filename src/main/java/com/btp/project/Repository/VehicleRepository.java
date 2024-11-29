package com.btp.project.Repository;

import org.springframework.data.repository.CrudRepository;
import com.btp.project.components.vehicle.Vehicle;

public interface VehicleRepository extends CrudRepository<Vehicle, Long> {

    Vehicle findByName(String name);

    Vehicle findById(long id);

}
