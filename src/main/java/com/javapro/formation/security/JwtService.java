package com.javapro.formation.security;

import com.javapro.formation.dto.AuthResponseDto;
import com.javapro.formation.dto.JwtProperties;
import com.javapro.formation.entity.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final JwtProperties props;
    private final TokenStore tokenStore;

    @Value("${jwt.refresh.expiration:864000}")
    private long refreshExpiration;

    private String createAccessToken(String username, Collection<? extends GrantedAuthority> authorities, Instant issuedAt, Instant expireAt) {
        var roles = authorities
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(authority -> authority.replaceFirst("^ROLE_", ""))
                .toList();
        var claims = JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())
                .subject(username)
                .claim("roles", roles)
                .issuedAt(issuedAt)
                .expiresAt(expireAt)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }

    public AuthResponseDto buildAuthResponse(String username, Collection<? extends GrantedAuthority> authorities) {
        var now = Instant.now();
        var accessExpireAt = now.plusSeconds(dureeEnSecondes());
        var accessToken = createAccessToken(username, authorities, now, accessExpireAt);
        var refreshExpireAt = now.plusSeconds(refreshExpiration);
        var refreshToken = UUID.randomUUID().toString();
        tokenStore.saveRefreshToken(refreshToken, username, refreshExpireAt);
        return new AuthResponseDto(accessToken, refreshToken, accessExpireAt, refreshExpireAt);
    }

    public long dureeEnSecondes() {
        return props.expiration() * 60;
    }
}
