package com.javapro.formation.controller;

import com.javapro.formation.dto.AuthResponseDto;
import com.javapro.formation.dto.LoginRequest;
import com.javapro.formation.dto.RegisterRequest;
import com.javapro.formation.dto.UtilisateurResponse;
import com.javapro.formation.security.dto.RefreshTokenDto;
import com.javapro.formation.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UtilisateurResponse register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/refresh")
    public AuthResponseDto refresh(@Valid @RequestBody RefreshTokenDto dto) {
        return authService.refresh(dto);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal Jwt jwt, @RequestBody(required = false) RefreshTokenDto dto) {
        authService.logout(jwt, dto);
    }

    @GetMapping("/me")
    public UtilisateurResponse me(@AuthenticationPrincipal Jwt jwt) {
        return authService.me(jwt);
    }
}
