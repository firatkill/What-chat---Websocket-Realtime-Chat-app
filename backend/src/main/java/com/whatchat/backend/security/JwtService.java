package com.whatchat.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {


    private static final String SECRET_KEY = "aaf59415517ad10a4624f86be46be82e68c3d15fbef72f6cbb23956e2b2da5128e06958f0ef3c0452f833526ba5961c225d868561a3217455b0e5cb2ea2336ae09c1b58fdc3e1760004739ebcdfbd967200b9a7bd2b05318d3fbe72c4f45136a7525c22e6a7255f4365149543aec695e9676ab79d17c15c73ca5ca34bce3f83e53685d3eea5f8a20d65c9dec7737545e008208d01c7e038221cbf8373e3258bb50b1067b410e082a4154dfff0e7904a0c8a6278a94d83881554cb9cb6c7e9c748a52a850b9c05294b79a05aaf5f7d965b30d4272af01d292ff1228c6d1c389d9696c2e02e72fd72c0abb4410d08ea776ebfe378d7cb0139a554abe6e53230b38";
    private static final long EXPIRATION = 1000 * 60 * 60 * 24; // 1 gün

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(UUID userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public UUID extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();


        return UUID.fromString(claims.getSubject());
    }

    public boolean isTokenValid(String token) {
        try {
            extractUserId(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}