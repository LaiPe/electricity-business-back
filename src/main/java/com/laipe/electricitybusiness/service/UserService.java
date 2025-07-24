package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.model.UserRole;
import com.laipe.electricitybusiness.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService extends GenericService<User, Long>{
    protected UserService(UserRepository repository) {
        super(repository);
    }

    @Override
    public User create(User entity) {
        entity.setRole(UserRole.USER);
        entity.setBanned(false);
        entity.setSigninDate(LocalDateTime.now());
        return super.create(entity);
    }
}
