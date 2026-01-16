package com.jasim.store.services;

import com.jasim.store.config.JwtConfig;
import com.jasim.store.entities.Role;
import com.jasim.store.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;

import javax.crypto.SecretKey;
import java.util.Date;


public class Jwt {
    private final Claims claims;
    private final SecretKey secretKey;
//    private final JwtConfig jwtConfig;

    public Jwt(Claims claims, SecretKey secretKey) {
        this.claims = claims;
        this.secretKey = secretKey;
    }

    public boolean isValid(){
        return claims.getExpiration().after(new Date());
    }

//    public String getUserId(String token){
//        return getClaims(token).getSubject();
//    }
//    public Role getRole(String token){
//        return Role.valueOf(getClaims(token).get("role",String.class));
//    }


}
