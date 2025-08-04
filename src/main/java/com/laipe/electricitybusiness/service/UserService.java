package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.repository.UserRepository;
import com.laipe.electricitybusiness.service.generic.GenericJPAService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class UserService extends GenericJPAService<User, Long> {
    protected UserService(UserRepository repository) {
        super(repository);
    }

    @Override
    public User create(User entity) {
        entity.setBanned(false);
        entity.setSigninDate(LocalDateTime.now());
        return super.create(entity);
    }
}
