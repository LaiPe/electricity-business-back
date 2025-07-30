package com.laipe.electricitybusiness.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laipe.electricitybusiness.model.VehicleModel;
import com.laipe.electricitybusiness.repository.VehicleModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final VehicleModelRepository vehicleModelRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initializeVehicleModels();
    }

    private void initializeVehicleModels() {
        try {
            // Vérifier si la collection est vide
            long count = vehicleModelRepository.count();
            if (count == 0) {
                log.info("Collection VehicleModel is empty. Initializing with default data...");

                // Charger le fichier JSON depuis resources
                ClassPathResource resource = new ClassPathResource("data/vehicle-models.json");

                if (resource.exists()) {
                    try (InputStream inputStream = resource.getInputStream()) {
                        // Convertir JSON en liste d'objets
                        List<VehicleModel> vehicleModels = objectMapper.readValue(
                                inputStream,
                                new TypeReference<List<VehicleModel>>() {}
                        );

                        // Sauvegarder en base
                        vehicleModelRepository.saveAll(vehicleModels);
                        log.info("Successfully initialized {} vehicle models", vehicleModels.size());
                    }
                } else {
                    log.warn("vehicle-models.json file not found in resources/data/");
                }
            } else {
                log.info("VehicleModel collection already contains {} documents. Skipping initialization.", count);
            }
        } catch (IOException e) {
            log.error("Error initializing vehicle models from JSON", e);
        }
    }
}