package com.laipe.electricitybusiness.service;

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
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.secret:ThisIsASecretKeyForJwtDontForgetToOverrideItInProduction}")
    private String secretKey;

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
     * Génère un token JWT avec une durée de validité par défaut.
     *
     * @param username le nom d'utilisateur
     * @return le token JWT généré
     */
    public String generateToken(String username) {
        return generateToken(Map.of(), username, 1000 * 60 * 15); // 15 minutes
    }

    // Nouvelle surcharge : inclure l'id utilisateur dans les claims
    public String generateToken(String username, Long userId) {
        return generateToken(Map.of("id", userId), username, 1000 * 60 * 15);
    }

    // Méthode pour extraire **tous les "claims"** (les données contenues dans le token).
    // Les claims sont typiquement : username, date d’expiration, rôles, etc.
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                // On définit la clé de signature pour vérifier l’authenticité du token.
                .setSigningKey(getSigningKey())
                .build()
                // On parse le token JWT et on récupère la partie "Claims" (le payload).
                .parseClaimsJws(token)
                .getBody(); // C’est ici qu’on obtient les données contenues dans le token.
    }

    /**
     * Méthode générique pour extraire **un seul claim** depuis un token JWT.
     * Elle utilise une fonction (`claimsResolver`) pour dire **quel champ** on veut extraire.
     * Par exemple : `Claims::getSubject` pour le username, `Claims::getExpiration` pour la date.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        // On applique la fonction passée en paramètre à l'objet Claims
        return claimsResolver.apply(claims);
    }

    /**
     * Méthode utilitaire pour extraire l'id utilisateur stocké dans les claims.
     * Retourne null si le claim est absent ou ne peut être converti en Long.
     */
    public Long extractUserId(String token) {
        Object idObj = extractClaim(token, claims -> claims.get("id"));
        if (idObj == null) return null;
        if (idObj instanceof Integer) {
            return ((Integer) idObj).longValue();
        }
        if (idObj instanceof Long) {
            return (Long) idObj;
        }
        try {
            return Long.valueOf(idObj.toString());
        } catch (NumberFormatException e) {
            log.warn("Impossible de convertir le claim 'id' en Long: {}", idObj);
            return null;
        }
    }

    /**
     * Vérifie si un token JWT est expiré.
     *
     * @param token le token JWT
     * @return true si le token est expiré, false sinon
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Valide un token JWT en vérifiant le nom d'utilisateur et la date d'expiration.
     *
     * @param token le token JWT
     * @param userDetails les détails de l'utilisateur à vérifier
     * @return true si le token est valide, false sinon
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Extrait la date d'expiration depuis un token JWT.
     *
     * @param token le token JWT
     * @return la date d'expiration extraite
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait le nom d'utilisateur (subject) depuis un token JWT.
     *
     * @param token le token JWT
     * @return le nom d'utilisateur extrait
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
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
