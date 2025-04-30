package com.example.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.library.config.ApiPaths;
import com.example.library.dto.SigninRequest;
import com.example.library.dto.SignupRequest;
import com.example.library.model.User;
import com.example.library.repository.UserRepository;
import com.example.library.security.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
@RestController
@RequestMapping(ApiPaths.AUTH)
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
    public String authenticateUser(@RequestBody SigninRequest user) {
        System.out.println("a");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.username(),
                            user.password()
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


    @Operation(summary = "Registrar un nuevo usuario", description = "Registra un usuario nuevo en el sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error de validación o usuario ya existe")
    })
    @PostMapping("/signup")
    public String registerUser(@RequestBody SignupRequest user) {
        if (userRepository.existsByUsername(user.username())) {
            return "Error: Username is already taken!";
        }
        // Create new user's account
        User newUser = new User(
                user.username(),
                encoder.encode(user.password()),
                user.email()
        );
        userRepository.save(newUser);
        return "User registered successfully!";
    }
}