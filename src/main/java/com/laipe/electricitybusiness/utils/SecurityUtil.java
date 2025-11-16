package com.laipe.electricitybusiness.utils;

import com.laipe.electricitybusiness.dto.user.GetUserDTO;
import com.laipe.electricitybusiness.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Utilitaire pour récupérer le JWT et certaines informations (claims) depuis le SecurityContext.
 * Dépend de JwtService pour parser les claims.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityUtil {

    private final JwtService jwtService;

    /**
     * Extrait l'access token (cookie HTTP-only `access_token`) depuis la requête HTTP.
     * Retourne null si le cookie est absent ou vide.
     */
    public String getTokenFromRequest(HttpServletRequest request) {
        if (request == null) return null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie c : cookies) {
            if ("access_token".equals(c.getName())) {
                String value = c.getValue();
                if (value == null || value.isBlank()) return null;
                return value;
            }
        }
        return null;
    }

    public String getTokenFromAuthentication() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        Object credentials = auth.getCredentials();
        if (!(credentials instanceof String token)) return null;
        if (!StringUtils.hasText(token)) return null;

        return token;
    }

    /**
     * Tente d'extraire le username et l'id à partir d'un token JWT, puis de les retourner dans un GetUserDTO.
     *
     * @return GetUserDTO rempli si le token est présent et valide selon JwtService, sinon null
     */
    public GetUserDTO getCurrentUserFromToken(String token) {
        if (token == null || token.isBlank()) return null;

        try {
            String username = jwtService.extractUsername(token);
            Long id = jwtService.extractUserId(token);
            if (username == null && id == null) return null;

            GetUserDTO dto = new GetUserDTO();
            dto.setUsername(username);
            dto.setId(id);
            return dto;
        } catch (Exception e) {
            log.warn("Erreur lors de l'extraction des claims depuis le token: {}", e.getMessage());
            return null;
        }
    }

    public GetUserDTO getCurrentUserFromAuthentification() {
        String token = getTokenFromAuthentication();
        return getCurrentUserFromToken(token);
    }
}
