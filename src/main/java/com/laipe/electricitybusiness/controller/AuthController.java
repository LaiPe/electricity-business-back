package com.laipe.electricitybusiness.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.laipe.electricitybusiness.controller.handler.InvalidVerificationCodeException;
import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.dto.auth.*;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.model.UserRole;
import com.laipe.electricitybusiness.service.*;
import com.laipe.electricitybusiness.service.MailService;
import com.laipe.electricitybusiness.utils.SecurityUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final VerificationCodeService verificationCodeService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final RegisterMapper registerMapper;
    private final StatusUserMapper statusUserMapper;
    private final SecurityUtil securityUtil;
    private final CookieService cookieService;
    private final MailService mailService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        log.info("Tentative de connexion pour l'utilisateur: {}", loginDTO.getUsername());

        // Authentifier l'utilisateur
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(),
                            loginDTO.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            // Log minimal info and return a generic unauthorized response so that an attacker
            // cannot distinguish between bad credentials and other authentication failures.
            log.warn("Échec d'authentification pour l'utilisateur: {}", loginDTO.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Invalid username or password", null));
        }

        // Récupérer l'utilisateur
        User user = (User) userService.loadUserByUsername(loginDTO.getUsername());

        // Si l'utilisateur est soft deleted, renvoyer la même erreur que pour de mauvais identifiants
        if (user.getDeletedAt() != null) {
            log.warn("Tentative de connexion avec un utilisateur supprimé: {}", loginDTO.getUsername());
            // Retourner une réponse 401 générique (indiscernable d'un mauvais credential)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Invalid username or password", null));
        }

        // Générer l'access token avec l'id utilisateur dans le payload
        String accessToken = jwtService.generateToken(user.getId());

        // Déposer le token dans un cookie HTTP-only nommé "access_token"
        response.addCookie(cookieService.createAccessTokenCookie(accessToken));

        log.info("Utilisateur connecté avec succès: {}", loginDTO.getUsername());
        return ResponseEntity.ok(new AuthResponse("User logged in successfully", statusUserMapper.toDto(user)));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseWithVerificationCode> register(@Valid @RequestBody RegisterDTO registerDTO, HttpServletResponse response) {
        log.info("Tentative d'inscription pour l'utilisateur: {}", registerDTO.getUsername());

        // Convertir le DTO en entité User
        User fromDto = registerMapper.toEntity(registerDTO);

        // Enrichir l'utilisateur
        fromDto.setRole(UserRole.USER);
        String verificationCode = verificationCodeService.generateCode();
        fromDto.setVerificationCode(verificationCode); // Le code sera hashé en base par le service UserService

        // Créer le nouvel utilisateur
        User savedUser = userService.create(fromDto);

        // Envoyer le code de vérification par email
        mailService.sendVerificationCodeEmail(
                savedUser,
                verificationCode
        );

        // Générer l'access token avec l'id utilisateur dans le payload
        String accessToken = jwtService.generateToken(savedUser.getId());

        // Déposer le token dans un cookie HTTP-only nommé "access_token"
        response.addCookie(cookieService.createAccessTokenCookie(accessToken));

        log.info("Utilisateur créé avec succès: {}", savedUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new AuthResponseWithVerificationCode(
                        "User registered successfull",
                        verificationCode, //TODO: Remove code from response in production
                        statusUserMapper.toDto(savedUser)
                )
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<MessageResponse> verify(@Valid @RequestBody VerificationCodeRequest request) {
        String code = request.getVerificationCode();
        log.info("Vérification du code pour l'utilisateur courant");

        // Récupérer l'id de l'utilisateur courant depuis le contexte de sécurité
        Long userId = securityUtil.getUserIdFromAuthentification();

        // Valider le code de vérification
        User user = userService.verifyUser(userId, code)
                .orElseThrow(() -> new InvalidVerificationCodeException());
        log.info("Utilisateur vérifié avec succès: {}", user.getUsername());

        return ResponseEntity.ok(new MessageResponse("User verified successfully"));
    }

    @PostMapping("/refresh-verification-code")
    public ResponseEntity<MessageResponseWithVerificationCode> refreshVerificationCode() {
        log.info("Rafraîchissement du code de vérification pour l'utilisateur courant");

        // Récupérer l'id de l'utilisateur courant depuis le contexte de sécurité
        Long userId = securityUtil.getUserIdFromAuthentification();

        // Générer un nouveau code de vérification
        String newCode = verificationCodeService.generateCode();
        // TODO: Envoyer le code par email

        // Créer une instance User avec le nouveau code
        User userToUpdate = new User();
        userToUpdate.setVerificationCode(newCode); // Le code sera hashé en base par le service UserService
        userToUpdate.setCodeExpirationDate(LocalDateTime.now().plusMinutes(verificationCodeService.getCODE_EXPIRATION_MINUTES()));

        // Mettre à jour l'utilisateur avec le nouveau code
        User user = userService.update(userToUpdate, userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId, User.class));

        log.info("Nouveau code de vérification généré pour l'utilisateur: {}", user.getUsername());
        return ResponseEntity.ok(
                new MessageResponseWithVerificationCode("New verification code generated", newCode));
    }

    @GetMapping("/status")
    public ResponseEntity<AuthResponse> checkAuthStatus() {
        log.info("Vérification du statut d'authentification de l'utilisateur courant");

        // Récupérer l'id de l'utilisateur courant depuis le contexte de sécurité
        Long userId = securityUtil.getUserIdFromAuthentification();

        // Récupérer les informations de status de l'utilisateur
        StatusUserDTO statusUser = userService.getById(userId)
                .map(statusUserMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(userId, User.class));

        log.info("Utilisateur authentifié: {}", statusUser.getUsername());
        return ResponseEntity.ok(new AuthResponse("User is authenticated", statusUser));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Supprime le cookie d'accès côté client
        response.addCookie(cookieService.createClearAccessTokenCookie());
        return ResponseEntity.noContent().build();
    }

    @Data
    @AllArgsConstructor
    public static class AuthResponseWithVerificationCode {
        private String message;
        @JsonProperty("verification_code")
        private String verificationCode;
        private StatusUserDTO user;
    }

    @Data
    @AllArgsConstructor
    public static class AuthResponse {
        private String message;
        private StatusUserDTO user;
    }

    @Data
    @AllArgsConstructor
    public static class MessageResponseWithVerificationCode {
        private String message;
        @JsonProperty("verification_code")
        private String verificationCode;
    }

    @Data
    @AllArgsConstructor
    public static class MessageResponse {
        private String message;
    }

    @Data
    @AllArgsConstructor
    public static class VerificationCodeRequest {
        @JsonProperty("verification_code")
        @NotNull
        @Size(min = 6, max = 6)
        private String verificationCode;
    }
}
