package com.laipe.electricitybusiness.service;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service utilitaire pour la création / suppression des cookies liés à l'authentification.
 */
@Service
@RequiredArgsConstructor
public class CookieService {

    @Value("${auth.cookie.name:access_token}")
    private String cookieName;

    @Value("${auth.cookie.maxAge:900}") // 15 minutes par défaut
    private int maxAge;

    @Value("${auth.cookie.secure:false}")
    private boolean secure;

    @Value("${auth.cookie.path:/}")
    private String path;

    /**
     * Crée un cookie HTTP-only contenant le token d'accès.
     * @param token le JWT à stocker
     * @return Cookie prêt à être ajouté à la réponse
     */
    public Cookie createAccessTokenCookie(String token) {
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(secure);
        return cookie;
    }

    /**
     * Crée un cookie qui supprime le cookie d'authentification côté client.
     * @return Cookie avec maxAge = 0
     */
    public Cookie createClearAccessTokenCookie() {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setPath(path);
        cookie.setMaxAge(0);
        cookie.setSecure(secure);
        return cookie;
    }
}