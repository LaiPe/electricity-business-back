package com.electricitybusiness.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "refresh_token", indexes = {
        @Index(name = "idx_token", columnList = "token", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @UuidGenerator
    @Column(name = "uuid", updatable = false, nullable = false)
    private UUID uuid;

    @Column(name = "token", nullable = false, unique = true, length = 512)
    @NotBlank(message = "Le token ne peut pas être vide")
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="idUtilisateur", nullable=false)
    @NotNull(message = "L'utilisateur est obligatoire")
    private Utilisateur utilisateur;

    @Column(name = "dateExpiration", nullable = false)
    @NotNull(message = "La date d'expiration est obligatoire")
    private LocalDateTime dateExpiration;

}
