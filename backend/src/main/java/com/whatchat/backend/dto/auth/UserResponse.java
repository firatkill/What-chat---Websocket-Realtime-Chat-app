package com.whatchat.backend.dto.auth;


import com.whatchat.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserResponse {

    private final String displayName;
    private final String email;
    private final String publicId;
    private final UUID id;
    private final String statusText;
    private final byte[] avatar;


    public static UserResponse transformFromUser(User user) {
        return new UserResponse(
                user.getDisplayName(),
                user.getEmail(),
                user.getPublicId(),
                user.getId(),
                user.getStatusText(),
                user.getAvatar()
        );
    }

}
