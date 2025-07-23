package com.electricitybusiness.api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service pour la gestion des tokens JWT.
 * Gère la génération, validation et extraction des tokens JWT.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    /**
     * Génère un token JWT pour un utilisateur avec une durée de validité de 15 minutes.
     *
     * @param username le nom d'utilisateur
     * @return le token JWT généré
     */
    public String generateToken(String username) {
        return generateToken(new HashMap<>(), username, 900);
    }

    /**
     * Génère un token JWT pour un utilisateur en spécifiant une durée de validité.
     *
     * @param username le nom d'utilisateur
     * @param jwtExpiration la durée de validité
     * @return le token JWT généré
     */
    public String generateToken(String username, long jwtExpiration) {
        return generateToken(new HashMap<>(), username,  jwtExpiration);
    }

    /**
     * Génère un token JWT avec des claims supplémentaires et une durée de validité de 15 minutes.
     *
     * @param extraClaims claims supplémentaires à inclure dans le token
     * @param username le nom d'utilisateur
     * @return le token JWT généré
     */
    public String generateToken(Map<String, Object> extraClaims, String username) {
        return generateToken(extraClaims, username, 900);
    }

    /**
     * Génère un token JWT avec des claims supplémentaires et en spécifiant une durée de validité.
     *
     * @param extraClaims claims supplémentaires à inclure dans le token
     * @param jwtExpiration la durée de validité
     * @param username le nom d'utilisateur
     * @return le token JWT généré
     */
    public String generateToken(Map<String, Object> extraClaims, String username, long jwtExpiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrait le nom d'utilisateur du token JWT.
     *
     * @param token le token JWT
     * @return le nom d'utilisateur
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait la date d'expiration du token JWT.
     *
     * @param token le token JWT
     * @return la date d'expiration
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait une claim spécifique du token JWT.
     *
     * @param token le token JWT
     * @param claimsResolver fonction pour extraire la claim
     * @param <T> le type de la claim
     * @return la claim extraite
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait toutes les claims du token JWT.
     *
     * @param token le token JWT
     * @return toutes les claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Vérifie si le token JWT est expiré.
     *
     * @param token le token JWT
     * @return true si le token est expiré, false sinon
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Valide un token JWT pour un utilisateur donné.
     *
     * @param token le token JWT
     * @param userDetails les détails de l'utilisateur
     * @return true si le token est valide, false sinon
     */
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Obtient la clé de signature pour les tokens JWT.
     *
     * @return la clé de signature
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
} 