package com.electricitybusiness.api.controller;

import com.electricitybusiness.api.model.RoleUtilisateur;
import com.electricitybusiness.api.model.Utilisateur;
import com.electricitybusiness.api.service.JpaUserDetailsService;
import com.electricitybusiness.api.service.JwtService;
import com.electricitybusiness.api.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour l'authentification et l'inscription des utilisateurs.
 * Gère l'authentification JWT et l'inscription des nouveaux utilisateurs.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JpaUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    /**
     * Endpoint d'authentification JWT.
     * POST /api/auth/login
     */
    //TODO Retourner un DTO au lieu d'une Map
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody AuthRequest request) {
        log.info("Tentative de connexion pour l'utilisateur: {}", request.username());
        try {
            // Authentifier l'utilisateur
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            
            // Générer l'access token
            String accessToken = jwtService.generateToken(request.username());

            // Générer le refresh token
            String refreshToken = refreshTokenService.generateRefreshToken(request.username());
            
            Map<String, Object> response = new HashMap<>();
            response.put("access_token", accessToken);
            response.put("refresh_token", refreshToken);
            response.put("message", "Authentification réussie");

            log.info("Utilisateur connecté avec succès: {}", request.username());
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            log.warn("Tentative de connexion échouée pour l'utilisateur: {}", request.username());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Identifiants invalides");
            errorResponse.put("message", "Nom d'utilisateur ou mot de passe incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            log.error("Erreur lors de l'authentification: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur d'authentification");
            errorResponse.put("message", "Une erreur est survenue lors de l'authentification");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Endpoint d'inscription d'un nouvel utilisateur.
     * POST /api/auth/register
     */
    //TODO Retourner un DTO au lieu d'une Map
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Tentative d'inscription pour l'utilisateur: {}", request.getPseudo());
        try {
            // Créer un nouvel utilisateur
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setPseudo(request.getPseudo());
            utilisateur.setMotDePasse(passwordEncoder.encode(request.getPassword()));
            utilisateur.setAdresseMail(request.getEmail());
            utilisateur.setRole(request.getRole() != null ? request.getRole() : RoleUtilisateur.CLIENT);
            utilisateur.setNomUtilisateur(request.getNomUtilisateur());
            utilisateur.setPrenom(request.getPrenom());
            utilisateur.setDateDeNaissance(LocalDate.parse(request.getDateDeNaissance(), DateTimeFormatter.ISO_DATE));
            utilisateur.setCompteValide(true);
            utilisateur.setBanni(false);

            Utilisateur savedUtilisateur = userDetailsService.createUser(utilisateur);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", savedUtilisateur.getNumUtilisateur());
            response.put("pseudo", savedUtilisateur.getPseudo());
            response.put("role", savedUtilisateur.getRole());
            response.put("message", "Utilisateur créé avec succès");
            log.info("Utilisateur créé avec succès: {}", savedUtilisateur.getPseudo());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Erreur lors de la création de l'utilisateur: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors de la création de l'utilisateur");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Endpoint pour vérifier le statut d'authentification.
     * GET /api/auth/status
     */
    //TODO Retourner un DTO au lieu d'une Map
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAuthStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);
        response.put("message", "Utilisateur authentifié");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@Valid @RequestBody ) {

    }

    /**
     * Record pour la requête d'authentification.
     */
    public record AuthRequest(String username, String password) {}

    /**
     * Classe interne pour la requête d'inscription.
     */
    public static class RegisterRequest {
        private String pseudo;
        private String password;
        private String email;
        private RoleUtilisateur role;
        private String nomUtilisateur;
        private String prenom;
        private String dateDeNaissance;

        public String getPseudo() { return pseudo; }
        public void setPseudo(String pseudo) { this.pseudo = pseudo; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public RoleUtilisateur getRole() { return role; }
        public void setRole(RoleUtilisateur role) { this.role = role; }
        public String getNomUtilisateur() { return nomUtilisateur; }
        public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }
        public String getPrenom() { return prenom; }
        public void setPrenom(String prenom) { this.prenom = prenom; }
        public String getDateDeNaissance() { return dateDeNaissance; }
        public void setDateDeNaissance(String dateDeNaissance) { this.dateDeNaissance = dateDeNaissance; }
    }
} 