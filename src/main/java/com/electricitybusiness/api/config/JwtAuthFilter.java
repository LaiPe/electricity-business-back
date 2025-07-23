package com.electricitybusiness.api.config;

import com.electricitybusiness.api.service.JwtService;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre d'authentification JWT.
 * Intercepte les requêtes HTTP et valide les tokens JWT dans le header Authorization.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String accessToken;
        final String username;
        
        // Vérifier si le header Authorization existe et commence par "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Extraire le token JWT (enlever "Bearer " du début)
        accessToken = authHeader.substring(7);
        
        try {
            // Extraire le nom d'utilisateur du token
            username = jwtService.extractUsername(accessToken);
            
            // Si le nom d'utilisateur est extrait et qu'aucune authentification n'est déjà présente
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Charger les détails de l'utilisateur
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                // Valider le token pour cet utilisateur
                if (jwtService.isTokenValid(accessToken, userDetails)) {
                    
                    // Créer un token d'authentification
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
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