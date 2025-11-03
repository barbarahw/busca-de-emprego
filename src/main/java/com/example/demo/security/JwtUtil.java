package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Component
public class JwtUtil {

    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expirationMs = 60*60*1000;
    
   

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
    
    public String getRoleFromToken (String token) {
        try {
            
            String[] parts = token.split("\\.");
            
            if (parts.length<2) {
                throw new IllegalArgumentException("Token inválido");
            }
            String payload = new String (Base64.getUrlDecoder().decode(parts[1]));
            JSONObject json = new JSONObject(payload);
            
            return json.optString("role", null);
        } catch (Exception e) {
            System.out.println("erro ao extrair role do token: " + e.getMessage());
            return null;
        }
    }


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
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    public int getExpirationInSeconds() {
        return (int) (expirationMs / 1000);
    }
}