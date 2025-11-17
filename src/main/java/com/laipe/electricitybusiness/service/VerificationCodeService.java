package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.model.User;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@Data
public class VerificationCodeService {

    private BCryptPasswordEncoder passwordEncoder;

    private final int CODE_EXPIRATION_MINUTES;

    public VerificationCodeService(BCryptPasswordEncoder passwordEncoder) {
        this.CODE_EXPIRATION_MINUTES = 15;
        this.passwordEncoder = passwordEncoder;
    }

    public String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(900000) + 100000; // 6 chiffres
        return String.valueOf(code);
    }

    public Boolean validateVerificationCode(User user, String code) {
        boolean equalsCode = passwordEncoder.matches(code, user.getVerificationCode());
        boolean notExpired = user.getCodeExpirationDate().isAfter(LocalDateTime.now());
        return equalsCode && notExpired;
    }
}
