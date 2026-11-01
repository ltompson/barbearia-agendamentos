
package com.barbearia.agendamentos.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getKey() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret nao configurado");
        }

        if (expiration <= 0) {
            throw new IllegalStateException("JWT expiration invalido");
        }

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String gerarToken(String usuario, String role, Long barbeiroId) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("role", role);

        if (barbeiroId != null) {
            claims.put("barbeiroId", barbeiroId);
        }

        Date agora = new Date();

        return Jwts.builder()
                .setSubject(usuario)
                .addClaims(claims)
                .setIssuedAt(agora)
                .setExpiration(new Date(agora.getTime() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extrairUsuario(String token) {
        return getClaims(token).getSubject();
    }

    public String extrairRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validarToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
