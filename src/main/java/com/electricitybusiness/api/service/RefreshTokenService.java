package com.electricitybusiness.api.service;

import com.electricitybusiness.api.model.RefreshToken;
import com.electricitybusiness.api.model.Utilisateur;
import com.electricitybusiness.api.repository.RefreshTokenRepository;
import com.electricitybusiness.api.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final JwtService jwtService;

    @Value("${jwt.refresh-token.expiration-days:7}")
    private int refreshTokenExpirationDays;


    @Transactional
    public String generateRefreshToken(String username) {
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findByPseudo(username);
        if (utilisateurOptional.isEmpty()) return null;

        String token = jwtService.generateToken(username);
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(refreshTokenExpirationDays);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUtilisateur(utilisateurOptional.get());
        refreshToken.setDateExpiration(expiryDate);
        refreshToken.setToken(hashToken(token));

        refreshTokenRepository.save(refreshToken);

        return token;
    }

    public boolean validateRefreshToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        String hashedToken = hashToken(token);
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(hashedToken);

        if (refreshTokenOpt.isEmpty()) {
            return false;
        }

        RefreshToken refreshToken = refreshTokenOpt.get();

        // Vérifier si le token a expiré
        if (refreshToken.getDateExpiration().isBefore(LocalDateTime.now())) {
            // Token expiré, le supprimer de la base
            refreshTokenRepository.delete(refreshToken);
            return false;
        }

        return true;
    }


    public Long getUtilisateurIdFromRefreshToken(String token) {
        if (!validateRefreshToken(token)) {
            return null;
        }

        String hashedToken = hashToken(token);
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(hashedToken);

        return refreshTokenOpt
                .map(refreshToken -> refreshToken.getUtilisateur().getNumUtilisateur())
                .orElse(null);
    }

    public void deleteRefreshToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return;
        }

        String hashedToken = hashToken(token);
        refreshTokenRepository.deleteByToken(hashedToken);
    }

    public void deleteUtilisateurRefreshTokens(Long userId) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNumUtilisateur(userId);
        refreshTokenRepository.deleteByUtilisateur(utilisateur);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du hashage du token", e);
        }
    }
}
