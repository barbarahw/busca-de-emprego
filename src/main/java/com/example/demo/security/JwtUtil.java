package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Collections;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.json.JSONObject;

@Component
public class JwtUtil {

    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expirationMs = 60*60*1000;
    private static final Set<String> loggedUsers = Collections.synchronizedSet(new HashSet<>());
   

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
    
    public Long getCompanyIdFromToken(String token) {
        try {
            Claims claims = parseToken(token);

            String role = claims.get("role", String.class);
            if (!"company".equalsIgnoreCase(role)) {
                throw new SecurityException("Token does not belong to a company");
            }

            String subject = claims.getSubject();
            return Long.parseLong(subject);

        } catch (Exception e) {
            System.out.println("Erro ao extrair company_id do token: " + e.getMessage());
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
    
    public static void addLoggedUser(String username) {
        loggedUsers.add(username);
    }

    public static void removeLoggedUser(String username) {
        loggedUsers.remove(username);
    }

    public static Set<String> getLoggedUsers() {
        return loggedUsers;
    }
}