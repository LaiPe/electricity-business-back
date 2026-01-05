package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    public void sendVerificationCodeEmail(User user, String code) {
        // Implémentation fictive de l'envoi d'email
        System.out.println("Envoi de l'email de vérification à " + user.getEmail() + " avec le code : " + code);
    }
}
