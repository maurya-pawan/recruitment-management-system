package com.example.recruitment.controller;

import com.example.recruitment.entity.User;
import com.example.recruitment.repository.UserRepository;
import com.example.recruitment.service.JwtService;
import com.example.recruitment.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String email = body.get("email");
        String password = body.get("password");
        String userType = body.getOrDefault("userType", "APPLICANT");
        String profileHeadline = body.get("profileHeadline");
        String address = body.get("address");

        if (name == null || email == null || password == null) return ResponseEntity.badRequest().body(Map.of("message", "Missing required fields"));
        if (userRepository.findByEmail(email).isPresent()) return ResponseEntity.status(409).body(Map.of("message", "Email already registered"));

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAddress(address);
        user.setProfileHeadline(profileHeadline);
        user.setUserType(User.Role.valueOf(userType.toUpperCase()));

        userService.createUser(user, password);
        return ResponseEntity.status(201).body(Map.of("message", "User created"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        if (email == null || password == null) return ResponseEntity.badRequest().body(Map.of("message", "Missing credentials"));

        Optional<User> optional = userRepository.findByEmail(email);
        if (optional.isEmpty()) return ResponseEntity.status(400).body(Map.of("message", "Invalid credentials"));
        User user = optional.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) return ResponseEntity.status(400).body(Map.of("message", "Invalid credentials"));
        String token = jwtService.generateToken(user.getId());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
