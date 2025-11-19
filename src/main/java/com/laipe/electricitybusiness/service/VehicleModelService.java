package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.model.VehicleModel;
import com.laipe.electricitybusiness.repository.VehicleModelRepository;
import com.laipe.electricitybusiness.service.generic.GenericService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class VehicleModelService implements GenericService<VehicleModel, String> {

    private final VehicleModelRepository repository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<VehicleModel> getById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<VehicleModel> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<VehicleModel> deleteById(String s) {
        return Optional.empty();
    }

    @Override
    public Optional<VehicleModel> update(VehicleModel newEntity, String s) {
        return Optional.empty();
    }

    @Override
    public VehicleModel create(VehicleModel entity) {
        return null;
    }

    public List<VehicleModel> search(String q) {
        if (q == null || q.trim().isEmpty()) {
            return repository.findAll();
        }

        String[] tokens = q.trim().split("\\s+");
        List<Criteria> andCriteria = new ArrayList<>();

        for (String token : tokens) {
            List<Criteria> orCriteria = new ArrayList<>();

            // Sensible à la casse si token = 1 caractère, sinon insensible
            String regexOptions = token.length() == 1 ? "" : "i";
            String searchToken = token.length() == 1 ? token : token.toLowerCase();

            // Match marque (ex: "tesla", "renault")
            orCriteria.add(Criteria.where("make").regex(searchToken, regexOptions));

            // Match nom du modèle (ex: "prius", "zoé")
            orCriteria.add(Criteria.where("model").regex(searchToken, regexOptions));

            // Match année (ex: "2018", "2020")
            orCriteria.add(Criteria.where("year").regex(searchToken, regexOptions));

            andCriteria.add(new Criteria().orOperator(orCriteria.toArray(new Criteria[0])));
        }

        Query query = new Query(new Criteria().andOperator(andCriteria.toArray(new Criteria[0])));
        return mongoTemplate.find(query, VehicleModel.class);
    }
}
