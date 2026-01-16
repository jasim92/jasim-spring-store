package com.jasim.store.services;

import com.jasim.store.config.JwtConfig;
import com.jasim.store.entities.Role;
import com.jasim.store.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@AllArgsConstructor
@Service
public class JwtService {
    private final JwtConfig jwtConfig;

    public String getAccessToken(User user){
        return generateToken(user, jwtConfig.getAccessTokenExpiration());
    }

    public String getRefreshToken(User user){
        return generateToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    private String generateToken(User user, long expirationTime) {
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * expirationTime))
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    public boolean validateToken(String token){

       try {
           var claims = getClaims(token);
           return claims.getExpiration().after(new Date());
       }catch (JwtException ex){
           return false;
       }
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserIdFromToken(String token){
        return getClaims(token).getSubject();
    }
    public Role getUserRoleFromToken(String token){
        return Role.valueOf(getClaims(token).get("role",String.class));
    }

}
