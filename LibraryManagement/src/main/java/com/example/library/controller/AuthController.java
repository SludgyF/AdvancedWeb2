package com.example.library.controller;

import com.example.library.model.User;
import com.example.library.repository.UserRepository;
import com.example.library.security.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtil jwtUtils;

    
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve un JWT si las credenciales son correctas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa y token generado"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/signin")
    public String authenticateUser(@RequestBody User user) {
        System.out.println("a");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            user.getPassword()
                    )
            );
            System.out.println("b");
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            System.out.println("c");
            return jwtUtils.generateToken(userDetails.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
            return "Authentication failed: " + e.getMessage();
        }
    }
    @PostMapping("/signup")
    public String registerUser(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return "Error: Username is already taken!";
        }
        // Create new user's account
        User newUser = new User(
                user.getUsername(),
                encoder.encode(user.getPassword()),
                user.getEmail()
        );
        userRepository.save(newUser);
        return "User registered successfully!";
    }
}