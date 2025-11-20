package com.laipe.electricitybusiness.config;

import com.laipe.electricitybusiness.dto.auth.StatusUserDTO;
import com.laipe.electricitybusiness.dto.auth.StatusUserMapper;
import com.laipe.electricitybusiness.service.JwtService;
import com.laipe.electricitybusiness.service.UserService;
import com.laipe.electricitybusiness.utils.SecurityUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre d'authentification JWT.
 * Intercepte les requêtes HTTP et valide les tokens JWT dans le cookie HTTP-only `access_token`.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final StatusUserMapper statusUserMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Extraire le token depuis la requête via SecurityUtil
        String accessToken = securityUtil.getTokenFromRequest(request);
        if (accessToken == null || accessToken.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extraire le nom d'utilisateur du token
            final Long userId = jwtService.extractUserId(accessToken);

            // Si le nom d'utilisateur est extrait et qu'aucune authentification n'est déjà présente
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Charger les détails de l'utilisateur
                StatusUserDTO user = userService.getById(userId)
                        .map(statusUserMapper::toDto)
                        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + userId));

                // Valider le token pour cet utilisateur
                if (jwtService.isTokenValid(accessToken, user)) {

                    log.debug("AUTHORITY : {}", user.getRole());
                    // Créer un token d'authentification
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user, // stocke l'état de l'utilisateur authentifié pour y accéder plus tard (SecurityUtil)
                            accessToken,
                            user.giveAuthorities()
                    );

                    // Configurer les détails de l'authentification
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Enregistrer l'authentification dans le SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("Utilisateur authentifié avec succès: {}", user.getUsername());
                } else {
                    log.warn("Token JWT invalide pour l'utilisateur: {}", user.getUsername());
                }
            }
        } catch (Exception e) {
            log.error("Erreur lors du traitement du token JWT: {}", e.getMessage());
            // Ne pas bloquer la requête en cas d'erreur de token
        }

        // Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}
