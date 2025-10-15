package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Component
public class JwtUtil {

    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expirationMs = 60*60*1000;

    /**
     * @param id ID do usuário (user_id ou company_id)
     * @param username username único
     * @param role "user" ou "company"
     * @return token JWT no formato HS256
     */
    public String gerarToken(String id, String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", role);
        
        long expTimestamp = expiryDate.getTime() /1000;

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(id)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("exp", expTimestamp)
                .signWith(key)
                .compact();
    }
    
    /*public String pegarId(String token) {
        return parseClaims(token).getSubject();
    }
    
    public String pegarRole(String token) {
        return (String) parseClaims(token).get("role");
    }*/


    public String pegarUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    
    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    public String extrairSubject(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

    
    public int getExpirationInSeconds() {
        return (int) (expirationMs / 1000);
    }
}