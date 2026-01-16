package com.jasim.store.services;

import com.jasim.store.entities.User;
import com.jasim.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;

    public User getCurrentUser(){
        var authentication =  SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser"))
            throw new AccessDeniedException("User not Authenticated");
        var userId = Long.parseLong(authentication.getPrincipal().toString());
        return userRepository.findById(userId).orElseThrow();
    }
}
