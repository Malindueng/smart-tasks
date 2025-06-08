package com.example.smarttasks.authservice.service;

import com.example.smarttasks.authservice.dto.AuthRequest;
import com.example.smarttasks.authservice.dto.AuthResponse;
import com.example.smarttasks.authservice.dto.RegisterRequest;
import com.example.smarttasks.authservice.entity.User;
import com.example.smarttasks.authservice.repository.UserRepository;
import com.example.smarttasks.authservice.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest registerRequest){
        boolean userExists = userRepository.findByUsername(registerRequest.getUsername()).isPresent();
        if(userExists){
            throw new RuntimeException("User already exists");
        }


        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role("ROLE")
                .build();

        System.out.println(user);
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token);
    }

    public AuthResponse login(AuthRequest authRequest){
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(()->new RuntimeException("Invalid username or password"));

        boolean isPasswordValid = passwordEncoder.matches(authRequest.getPassword(), user.getPassword());
        if(!isPasswordValid){
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token);
    }

}
