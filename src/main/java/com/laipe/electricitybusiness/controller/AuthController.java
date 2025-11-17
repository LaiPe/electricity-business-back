package com.laipe.electricitybusiness.controller;

import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.dto.auth.LoginDTO;
import com.laipe.electricitybusiness.dto.auth.RegisterDTO;
import com.laipe.electricitybusiness.dto.auth.RegisterMapper;
import com.laipe.electricitybusiness.dto.user.GetUserDTO;
import com.laipe.electricitybusiness.dto.user.GetUserMapper;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.model.UserRole;
import com.laipe.electricitybusiness.service.CookieService;
import com.laipe.electricitybusiness.service.JwtService;
import com.laipe.electricitybusiness.service.UserService;
import com.laipe.electricitybusiness.service.VerificationCodeService;
import com.laipe.electricitybusiness.utils.SecurityUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final GetUserMapper getUserMapper;
    private final SecurityUtil securityUtil;
    private final CookieService cookieService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        log.info("Tentative de connexion pour l'utilisateur: {}", loginDTO.getUsername());
        // Authentifier l'utilisateur
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );
        // Récupérer l'utilisateur pour obtenir son id
        User user = (User) userService.loadUserByUsername(loginDTO.getUsername());
        // Générer l'access token avec l'id utilisateur dans le payload
        String accessToken = jwtService.generateToken(user.getUsername(), user.getId());

        // Déposer le token dans un cookie HTTP-only nommé "access_token"
        response.addCookie(cookieService.createAccessTokenCookie(accessToken));

        log.info("Utilisateur connecté avec succès: {}", loginDTO.getUsername());
        return ResponseEntity.ok(new AuthResponse("User logged in successfully", getUserMapper.toDto(user)));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterDTO registerDTO, HttpServletResponse response) {
        log.info("Tentative d'inscription pour l'utilisateur: {}", registerDTO.getUsername());

        // Convertir le DTO en entité User
        User fromDto = registerMapper.toEntity(registerDTO);

        // Enrichir l'utilisateur
        fromDto.setRole(UserRole.USER);
        String verificationCode = verificationCodeService.generateCode();
        fromDto.setVerificationCode(verificationCode); // Le code sera hashé en base par le service UserService

        // Créer le nouvel utilisateur
        User savedUser = userService.create(fromDto);
        // TODO: Envoyer le code par email

        // Générer l'access token avec l'id utilisateur dans le payload
        String accessToken = jwtService.generateToken(savedUser.getUsername(), savedUser.getId());

        // Déposer le token dans un cookie HTTP-only nommé "access_token"
        response.addCookie(cookieService.createAccessTokenCookie(accessToken));

        log.info("Utilisateur créé avec succès: {}", savedUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse("User registered successfull : " + verificationCode, getUserMapper.toDto(savedUser)));
    }

    @PostMapping("/verify")
    public ResponseEntity<AuthResponse> verify(@Valid @RequestBody String code) {
        log.info("Vérification du code pour l'utilisateur courant");

        // Récupérer l'utilisateur courant depuis le contexte de sécurité
        GetUserDTO currentUser = securityUtil.getCurrentUserFromAuthentification();
        Long userId = currentUser.getId();

        // Valider le code de vérification
        currentUser = userService.verifyUser(userId, code)
                .map(getUserMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Échec de la vérification pour l'utilisateur: {}", userId);
                    return new IllegalArgumentException("Invalid verification code");
                });
        log.info("Utilisateur vérifié avec succès: {}", currentUser.getUsername());

        return ResponseEntity.ok(new AuthResponse("User verified successfully", currentUser));
    }

    @PostMapping("/refresh-verification-code")
    public ResponseEntity<AuthResponse> refreshVerificationCode() {
        log.info("Rafraîchissement du code de vérification pour l'utilisateur courant");

        // Récupérer l'utilisateur courant depuis le contexte de sécurité
        GetUserDTO currentUser = securityUtil.getCurrentUserFromAuthentification();
        Long userId = currentUser.getId();

        // Générer un nouveau code de vérification
        String newCode = verificationCodeService.generateCode();
        // TODO: Envoyer le code par email

        // Créer une instance User avec le nouveau code
        User userToUpdate = new User();
        userToUpdate.setVerificationCode(newCode); // Le code sera hashé en base par le service UserService
        userToUpdate.setCodeExpirationDate(LocalDateTime.now().plusMinutes(verificationCodeService.getCODE_EXPIRATION_MINUTES()));

        // Mettre à jour l'utilisateur avec le nouveau code
        User updatedUser = userService.update(userToUpdate, userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId, User.class));

        log.info("Nouveau code de vérification généré pour l'utilisateur: {}", currentUser.getUsername());
        return ResponseEntity.ok(new AuthResponse("New verification code generated: " + newCode, getUserMapper.toDto(updatedUser)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Supprime le cookie d'accès côté client
        response.addCookie(cookieService.createClearAccessTokenCookie());
        return ResponseEntity.noContent().build();
    }

    @Data
    @AllArgsConstructor
    public static class AuthResponse {
        private String message;
        private GetUserDTO user;
    }
}
