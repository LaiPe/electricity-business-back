package com.laipe.electricitybusiness.repository;

import com.laipe.electricitybusiness.model.VehicleModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class VehicleModelRepositoryTest {

    @Autowired
    private VehicleModelRepository vehicleModelRepository;

    private VehicleModel createModel() {
        VehicleModel m = new VehicleModel();
        m.setMake("Tesla");
        m.setModel("Model Test");
        m.setYear("2024");
        m.setConsumptionKwhPer100Km(new BigDecimal("15.50"));
        m.setBatteryCapacityKwh(new BigDecimal("75.00"));
        return m;
    }

    @Test
    void testSaveFindAllFindByIdDelete() {
        VehicleModel saved = vehicleModelRepository.save(createModel());
        assertThat(saved.getId()).isNotNull();

        List<VehicleModel> all = vehicleModelRepository.findAll();
        assertThat(all).isNotEmpty();

        Optional<VehicleModel> found = vehicleModelRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getMake()).isEqualTo("Tesla");

        vehicleModelRepository.delete(found.get());
        assertThat(vehicleModelRepository.findById(saved.getId())).isEmpty();
    }
}

