package com.electricitybusiness.api.repository;

import com.electricitybusiness.api.model.RefreshToken;
import com.electricitybusiness.api.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUtilisateur(Utilisateur utilisateur);

    void deleteByToken(String token);

}
