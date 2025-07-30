package com.laipe.electricitybusiness.repository;

import com.laipe.electricitybusiness.model.VehicleModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VehicleModelRepository extends MongoRepository<VehicleModel, String> {
}
