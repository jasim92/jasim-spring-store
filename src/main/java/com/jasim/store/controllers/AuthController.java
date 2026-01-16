package com.jasim.store.controllers;

import com.jasim.store.config.JwtConfig;
import com.jasim.store.dtos.JwtResponse;
import com.jasim.store.dtos.UserDto;
import com.jasim.store.dtos.UserLoginRequest;
import com.jasim.store.mappers.CartMapper;
import com.jasim.store.mappers.UserMapper;
import com.jasim.store.repositories.UserRepository;
import com.jasim.store.services.AuthService;
import com.jasim.store.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
   private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtConfig jwtConfig;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody UserLoginRequest request,
                                             HttpServletResponse response){
      
//        var user = userRepository.findUserByEmail(request.getEmail());
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
//                    Map.of("email", "Email not found ")
//            );
//        }
//
//        var password = user.getPassword();
//        var enteredPassword = request.getPassword();
//        if (!passwordEncoder.matches(enteredPassword, password)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
//                    Map.of("password", "Password does not match ")
//            );
//        }

        //New way to do above business logic using Spring Authentication

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findUserByEmail(request.getEmail());

        var accessToken = jwtService.getAccessToken(user);

        var refreshToken = jwtService.getRefreshToken(user);

        var cookies = new Cookie("refreshToken",refreshToken);
        cookies.setHttpOnly(true);
        cookies.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        cookies.setPath("/auth/refresh");
        cookies.setSecure(true);
        response.addCookie(cookies);

        return ResponseEntity.ok(new JwtResponse(accessToken));
    }

//    @PostMapping("/validate")
//    public ResponseEntity<Boolean> validate(@RequestHeader("Authorization") String requestHeader){
//        var token = requestHeader.replace("Bearer ","");
//
//        return ResponseEntity.ok(jwtService.validateToken(token));
//    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@CookieValue(value = "refreshToken") String refreshToken){

        var isValid = jwtService.validateToken(refreshToken);
        if (!isValid)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        var userId = jwtService.getUserIdFromToken(refreshToken);
        var user = userRepository.findById(Long.valueOf(userId)).orElseThrow();

        var refreshedToken = jwtService.getRefreshToken(user);

        return ResponseEntity.ok(new JwtResponse(refreshedToken));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(){
        var user = authService.getCurrentUser();
        if (user == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialException()
    {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
