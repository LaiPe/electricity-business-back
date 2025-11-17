package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.controller.handler.AlreadyUsedUsernameException;
import com.laipe.electricitybusiness.controller.handler.AlreadyVerifiedUserException;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.repository.UserRepository;
import com.laipe.electricitybusiness.service.generic.GenericJPAService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class UserService extends GenericJPAService<User, Long> implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final VerificationCodeService verificationCodeService;


    public UserService(UserRepository repository, BCryptPasswordEncoder passwordEncoder, VerificationCodeService verificationCodeService) {
        super(repository);
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
        this.verificationCodeService = verificationCodeService;
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
        if (newEntity.getVerificationCode() != null) {
            newEntity.setVerificationCode(passwordEncoder.encode(newEntity.getVerificationCode())); // Hashage du code de vérification
        }
        if (newEntity.getPassword() != null) {
            newEntity.setPassword(passwordEncoder.encode(newEntity.getPassword()));
        }
        return super.update(newEntity, id);
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

    public void banUser(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setBanned(true);
            userRepository.save(user);
        });
    }
}
