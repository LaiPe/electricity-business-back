package com.laipe.electricitybusiness.controller.handler;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlreadyVerifiedUserException extends RuntimeException {

    private final Long userId;

    public AlreadyVerifiedUserException(Long userId) {
        super("L'utilisateur est déjà vérifié");
        this.userId = userId;
    }
}

