package com.javapro.formation.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenDenylist {
    private final Map<String, Instant> revoques = new ConcurrentHashMap<>();

    public void revoquer(String jti, Instant expiration) {
        purger();
        revoques.put(jti, expiration);
    }

    public boolean estRevoque(String jti) {
        Instant exp = revoques.get(jti);
        return exp != null && exp.isAfter(Instant.now());
    }

    private void purger() {   // supprime les entrées dont le jeton a de toute façon expiré
        Instant now = Instant.now();
        revoques.values().removeIf(exp -> !exp.isAfter(now));
    }

}
