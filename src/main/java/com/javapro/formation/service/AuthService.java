package com.javapro.formation.service;

import com.javapro.formation.dto.AuthResponseDto;
import com.javapro.formation.dto.LoginRequest;
import com.javapro.formation.dto.RegisterRequest;
import com.javapro.formation.dto.UtilisateurResponse;
import com.javapro.formation.entity.Enum.RoleUtilisateur;
import com.javapro.formation.entity.Services;
import com.javapro.formation.entity.Utilisateur;
import com.javapro.formation.repository.ServiceRepository;
import com.javapro.formation.repository.UtilisateurRepository;
import com.javapro.formation.security.JwtService;
import com.javapro.formation.security.TokenDenylist;
import com.javapro.formation.security.TokenStore;
import com.javapro.formation.security.dto.RefreshTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UtilisateurRepository utilisateurs;
    private final ServiceRepository services;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenStore tokenStore;
    private final TokenDenylist denylist;

    @Transactional
    public UtilisateurResponse register(RegisterRequest req) {
        if (utilisateurs.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé");
        }
        if (utilisateurs.existsByMatricule(req.matricule())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce matricule est déjà utilisé");
        }
        Services service = services.findById(req.serviceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service inconnu"));

        Utilisateur u = new Utilisateur();
        u.setMatricule(req.matricule());
        u.setNom(req.nom());
        u.setPrenom(req.prenom());
        u.setEmail(req.email().toLowerCase());
        u.setMotDePasse(passwordEncoder.encode(req.motDePasse()));
        u.setRole(RoleUtilisateur.COLLABORATEUR);   // jamais choisi par l'utilisateur
        u.setService(service);
        return UtilisateurResponse.from(utilisateurs.save(u));
    }

    public AuthResponseDto login(LoginRequest req) {
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(req.email(), req.motDePasse()));
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        }
        return jwtService.buildAuthResponse(auth.getName(), auth.getAuthorities());
    }

    /** Échange un refresh token (à usage unique) contre une nouvelle paire de jetons. */
    public AuthResponseDto refresh(RefreshTokenDto dto) {
        var data = tokenStore.consumeRefreshToken(dto.refreshToken());
        if (data == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalide ou expiré");
        }
        Utilisateur u = utilisateurs.findByMatricule(data.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur inconnu"));
        return jwtService.buildAuthResponse(u.getUsername(), u.getAuthorities());
    }

    public void logout(Jwt accessToken, RefreshTokenDto dto) {
        // Le décodeur JWT consulte TokenDenylist : c'est elle qui rend le jeton inutilisable.
        if (accessToken != null) {   // /auth/logout est public : le jeton peut être absent ou expiré
            denylist.revoquer(accessToken.getId(), accessToken.getExpiresAt());
        }
        if (dto != null && dto.refreshToken() != null) {
            var data = tokenStore.consumeRefreshToken(dto.refreshToken());
            if (data != null && accessToken != null && !data.username().equals(accessToken.getSubject())) {
                // Le refresh token appartient à un autre utilisateur : on le remet en place.
                tokenStore.saveRefreshToken(dto.refreshToken(), data.username(), data.expiresAt());
            }
        }
    }

    @Transactional(readOnly = true)
    public UtilisateurResponse me(Jwt jwt) {
        return utilisateurs.findByMatricule(jwt.getSubject())   // le subject du jeton est le matricule
                .map(UtilisateurResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
