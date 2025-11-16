package com.laipe.electricitybusiness.controller.handler;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlreadyUsedUsernameException extends RuntimeException {

    private final String username;

    public AlreadyUsedUsernameException(String username) {
        super("Nom d'utilisateur déjà utilisé");
        this.username = username;
    }
}
