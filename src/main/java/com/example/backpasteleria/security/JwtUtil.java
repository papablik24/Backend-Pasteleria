package com.example.backpasteleria.security;

import java.security.Key;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;
import java.nio.charset.StandardCharsets;


@Component
public class JwtUtil {
    @Value("${jwt.secret:default-secret-key-which-is-too-short-for-hs256}")
    private String jwtSecret;

    @Value("${jwt.expirationMs:86400000}")
    private long jwtExpirationMs;

    private Key claveSecreta;

    @PostConstruct
    public void init() {
        byte[] keyBytes;
        try {
            // Se espera que el secret esté en Base64; si no, usar el texto UTF-8
            keyBytes = Base64.getDecoder().decode(jwtSecret);
        } catch (IllegalArgumentException e) {
            keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        }
        this.claveSecreta = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rol);
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(claveSecreta, SignatureAlgorithm.HS256)
                .compact();
    }


    // Métodos de validación simplificados para que compile rápido
    public boolean validateToken(String token, String email) {
        return extractUsername(token).equals(email);
    }
    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(claveSecreta).build().parseClaimsJws(token).getBody().getSubject();
    }
    public String extractRole(String token) {
        return Jwts.parserBuilder().setSigningKey(claveSecreta).build().parseClaimsJws(token).getBody().get("rol", String.class);
    }
    
}
