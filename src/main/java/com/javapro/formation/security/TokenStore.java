package com.javapro.formation.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenStore {
    public record RefreshData(String username, Instant expiresAt) {
    }

    private final Map<String, RefreshData> refreshTokens = new ConcurrentHashMap<>();
    private final Map<String, Instant> revokedAccessTokens = new ConcurrentHashMap<>();

    public void saveRefreshToken(String token, String username, Instant expiresAt) {
        purge();
        refreshTokens.put(token, new RefreshData(username, expiresAt));
    }

    /** Retire et retourne le refresh token (usage unique), ou null s'il est inconnu ou expiré. */
    public RefreshData consumeRefreshToken(String token) {
        var data = refreshTokens.remove(token);
        if (data == null || data.expiresAt().isBefore(Instant.now())) {
            return null;
        }
        return data;
    }

    public void revokeAccessToken(String jti, Instant expiresAt) {
        if (jti != null && expiresAt != null) {
            revokedAccessTokens.put(jti, expiresAt);
        }
    }

    public boolean isAccessTokenRevoked(String jti) {
        return jti != null && revokedAccessTokens.containsKey(jti);
    }

    private void purge() {
        var now = Instant.now();
        refreshTokens.values().removeIf(d -> d.expiresAt().isBefore(now));
        revokedAccessTokens.values().removeIf(exp -> exp.isBefore(now));
    }
}
