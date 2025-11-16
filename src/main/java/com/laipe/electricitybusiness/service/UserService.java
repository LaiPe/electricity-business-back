package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.controller.handler.AlreadyUsedUsernameException;
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
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository repository) {
        super(repository);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Tentative de chargement de l'utilisateur: {}", username);
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
        entity.setSigninDate(LocalDateTime.now());
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        return super.create(entity);
    }

    @Override
    public Optional<User> update(User newEntity, Long id) {
        newEntity.setPassword(passwordEncoder.encode(newEntity.getPassword()));
        return super.update(newEntity, id);
    }
}
