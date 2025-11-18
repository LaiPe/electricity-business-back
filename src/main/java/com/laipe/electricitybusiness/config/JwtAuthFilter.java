package com.laipe.electricitybusiness.config;

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
import org.springframework.security.core.userdetails.UserDetails;
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

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String accessToken;
        final String username;

        // Extraire le token depuis la requête via SecurityUtil
        String tokenFromCookie = securityUtil.getTokenFromRequest(request);
        if (tokenFromCookie == null || tokenFromCookie.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        accessToken = tokenFromCookie;

        try {
            // Extraire le nom d'utilisateur du token
            username = jwtService.extractUsername(accessToken);

            // Si le nom d'utilisateur est extrait et qu'aucune authentification n'est déjà présente
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Charger les détails de l'utilisateur
                UserDetails userDetails = this.userService.loadUserByUsername(username);

                // Valider le token pour cet utilisateur
                if (jwtService.isTokenValid(accessToken, userDetails)) {

                    log.debug("AUTHORITY : {}", userDetails.getAuthorities().toString());
                    // Créer un token d'authentification
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            accessToken, // stocke le token JWT dans les credentials pour y accéder plus tard
                            userDetails.getAuthorities()
                    );

                    // Configurer les détails de l'authentification
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Enregistrer l'authentification dans le SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("Utilisateur authentifié avec succès: {}", username);
                } else {
                    log.warn("Token JWT invalide pour l'utilisateur: {}", username);
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
