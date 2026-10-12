package com.javapro.formation.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Liée aux propriétés {@code jwt.secret} et {@code jwt.expiration} (durée en minutes). */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, long expiration) {
}
