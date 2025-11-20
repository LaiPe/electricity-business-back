package com.laipe.electricitybusiness.config;

import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.dto.auth.StatusUserDTO;
import com.laipe.electricitybusiness.dto.auth.StatusUserMapper;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.service.UserService;
import com.laipe.electricitybusiness.utils.SecurityUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserStatusFilter extends OncePerRequestFilter {

    private final SecurityUtil securityUtil;
    private final RoutesConfig routesConfig;

    private final UserService userService;
    private final StatusUserMapper statusUserMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Si la route est publique, continuer la chaîne de filtres
        if (publicRoute(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Si l'utilisateur n'est pas authentifié, continuer la chaîne de filtres
        Long userId = securityUtil.getUserIdFromAuthentification();
        if (userId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Enrichir le DTO avec le statut de l'utilisateur depuis la base de données
        StatusUserDTO statusUser = userService.getById(userId)
                .map(statusUserMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(userId, User.class));

        // Vérifier si l'utilisateur est banni et que la route n'est pas autorisée pour les utilisateurs bannis
        if (statusUser.getBanned() && !authorizedRouteForBannedUsers(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Account banned\"}");
            log.info("Accès refusé pour l'utilisateur banni: {}", statusUser.getUsername());
            log.info(statusUser.toString());
            return;
        }

        // Vérifier si l'utilisateur n'est pas vérifié et que la route n'est pas autorisée pour les utilisateurs non vérifiés
        if (!statusUser.getVerified() && !authorizedRouteForUnverifiedUsers(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Email not verified\"}");
            log.info("Accès refusé pour l'utilisateur non vérifié: {}", statusUser.getUsername());
            log.info(statusUser.toString());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean authorizedRouteForBannedUsers(HttpServletRequest request) {
        return routesConfig.authorizedRoutesForBannedUsers().matches(request);
    }

    private boolean authorizedRouteForUnverifiedUsers(HttpServletRequest request) {
        return routesConfig.authorizedRoutesForUnverifiedUsers().matches(request);
    }

    private boolean publicRoute(HttpServletRequest request) {
        return routesConfig.publicRoutes().matches(request);
    }
}
