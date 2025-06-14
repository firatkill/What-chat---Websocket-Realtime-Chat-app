package com.whatchat.backend.service;

import com.whatchat.backend.dto.auth.AuthResponse;
import com.whatchat.backend.dto.auth.LoginRequest;
import com.whatchat.backend.dto.auth.RegisterRequest;
import com.whatchat.backend.entity.User;
import com.whatchat.backend.repository.UserRepository;
import com.whatchat.backend.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletResponse httpServletResponse;

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Bu email ile zaten kayıt var.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName())
                .build();

        User saved = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(saved.getId());

    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Geçersiz e-posta veya şifre.");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Geçersiz e-posta veya şifre.");
        }

        String token = jwtTokenProvider.generateToken(user.getId());
        return new AuthResponse(token,user.getId(),user.getEmail(),user.getDisplayName());
    }

    public User getCurrentUser(User user) {
        return user; // SecurityContext üzerinden gelen kimlik zaten User olarak çözülüyor
    }
}