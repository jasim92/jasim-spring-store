package com.jasim.store.services;

import com.jasim.store.dtos.JwtResponse;
import com.jasim.store.entities.Role;
import com.jasim.store.entities.User;
import com.jasim.store.mappers.UserMapper;
import com.jasim.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${google.auth.client_id}")
    private String client_id;
    @Value("${google.auth.client_secret}")
    private String client_secret = "";
    @Value("${google.redirect_url}")
    private String redirect_url = "";

    public User getCurrentUser(){
        var authentication =  SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser"))
            throw new AccessDeniedException("User not Authenticated");
        var userId = Long.parseLong(authentication.getPrincipal().toString());
        return userRepository.findById(userId).orElseThrow();
    }

    public String handleGoogleLogin(String code){
        // 1. exchange code with tokens

        String tokenEndpoint = "https://oauth2.googleapis.com/token";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", client_id);
        params.add("client_secret", client_secret);
        params.add("redirect_uri", redirect_url);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
        String tokenId = tokenResponse.getBody().get("id_token").toString();
        String userInfoEndpoint = "https://oauth2.googleapis.com/tokeninfo?id_token=" + tokenId;
        ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoEndpoint, Map.class);
        if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
            String email = userInfoResponse.getBody().get("email").toString();
            User user = userRepository.findUserByEmail(email);
            if (user == null) {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setName(email);
                newUser.setRole(Role.USER);
                newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                userRepository.save(newUser);
            }
            String jwt = jwtService.getAccessToken(user);
           return jwt;

        }
        return null;
    }
}
