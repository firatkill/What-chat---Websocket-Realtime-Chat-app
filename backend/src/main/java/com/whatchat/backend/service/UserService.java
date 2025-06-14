package com.whatchat.backend.service;

import com.whatchat.backend.dto.auth.UserResponse;
import com.whatchat.backend.dto.user.UpdateProfileRequest;
import com.whatchat.backend.entity.User;
import com.whatchat.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse updateProfile(User user, UpdateProfileRequest request) {
        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getStatusText() != null) {
            user.setStatusText(request.getStatusText());
        }
        User savedUser = userRepository.save(user);

        return UserResponse.transformFromUser(savedUser);
    }

    public void uploadAvatar(User user, byte[] avatarData) {
        user.setAvatar(avatarData);
        userRepository.save(user);
    }

    public List<User> searchUsers(String query, User currentUser) {
        return userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .filter(u -> u.getDisplayName() != null && u.getDisplayName().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    public User getUserStatus(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
    }
}