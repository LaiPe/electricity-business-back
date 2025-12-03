package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.controller.handler.AlreadyUsedUsernameException;
import com.laipe.electricitybusiness.controller.handler.AlreadyVerifiedUserException;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.repository.PlaceRepository;
import com.laipe.electricitybusiness.repository.UserRepository;
import com.laipe.electricitybusiness.repository.VehicleRepository;
import com.laipe.electricitybusiness.service.generic.GenericJPAService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class UserService extends GenericJPAService<User, Long> implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final VerificationCodeService verificationCodeService;

    private final PlaceService placeService;
    private final PlaceRepository placeRepository;
    private final VehicleService vehicleService;
    private final VehicleRepository vehicleRepository;


    public UserService(
            UserRepository repository,
            BCryptPasswordEncoder passwordEncoder,
            VerificationCodeService verificationCodeService,
            PlaceService placeService,
            PlaceRepository placeRepository,
            VehicleService vehicleService,
            VehicleRepository vehicleRepository

    ) {
        super(repository);
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
        this.verificationCodeService = verificationCodeService;

        this.placeService = placeService;
        this.placeRepository = placeRepository;
        this.vehicleService = vehicleService;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Utilisateur non trouvé: {}", username);
                    return new UsernameNotFoundException("Utilisateur non trouvé: " + username);
                });
    }

    @Override
    public User create(User entity) {
        if (userRepository.findByUsername(entity.getUsername()).isPresent()) {
            throw new AlreadyUsedUsernameException(entity.getUsername());
        }
        entity.setBanned(false);
        entity.setVerified(false);
        entity.setSigninDate(LocalDateTime.now());
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));

        entity.setVerificationCode(passwordEncoder.encode(entity.getVerificationCode())); // Hashage du code de vérification
        entity.setCodeExpirationDate(LocalDateTime.now().plusMinutes(verificationCodeService.getCODE_EXPIRATION_MINUTES()));
        return super.create(entity);
    }

    @Override
    public Optional<User> update(User newEntity, Long id) {
        if (newEntity.getUsername() != null && userRepository.findByUsername(newEntity.getUsername()).isPresent()) {
            throw new AlreadyUsedUsernameException(newEntity.getUsername());
        }
        if (newEntity.getVerificationCode() != null) {
            newEntity.setVerificationCode(passwordEncoder.encode(newEntity.getVerificationCode())); // Hashage du code de vérification
        }
        if (newEntity.getPassword() != null) {
            newEntity.setPassword(passwordEncoder.encode(newEntity.getPassword()));
        }
        return super.update(newEntity, id);
    }

    @Override
    public Optional<User> deleteById(Long id) {
        // First, soft delete all associated vehicles (can be refused)
        placeRepository.findAllNotDeletedByOwnerId(id)
                .forEach(place -> {
                    placeService.deleteById(place.getId());
                });

        // Then, soft delete all associated places (can be refused)
        vehicleRepository.findAllNotDeletedByOwnerId(id)
                .forEach(vehicle -> {
                    vehicleService.deleteById(vehicle.getId());
                });

        // Finally, anonymize the user & soft delete
        return userRepository.findById(id)
                .map(user -> {
                    // Anonymization
                    user.setUsername("deleted_user_" + user.getId());
                    user.setEmail("deleted_user_" + user.getId() + "@deleted.com");
                    user.setFirstName("Deleted");
                    user.setLastName("User");
                    user.setBirthDate(LocalDate.MIN);

                    // Soft delete
                    user.setDeletedAt(LocalDateTime.now());

                    // Return updated user
                    return userRepository.save(user);
                });
    }

    public Optional<User> verifyUser(Long userId, String code) throws AlreadyVerifiedUserException {
        return userRepository.findById(userId)
                .map(user -> {
                    if (user.getVerified()) {
                        throw new AlreadyVerifiedUserException(userId);
                    }
                    if (!verificationCodeService.validateVerificationCode(user, code)) {
                        return null;
                    }
                    user.setVerified(true);
                    return userRepository.save(user);
                });
    }

    public Optional<User> banUser(Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setBanned(true);
                    return userRepository.save(user);
                });
    }

    public Optional<User> unbanUser(Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setBanned(false);
                    return userRepository.save(user);
                });
    }
}
