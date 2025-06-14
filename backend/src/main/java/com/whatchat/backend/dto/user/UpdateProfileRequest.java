package com.whatchat.backend.dto.user;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String displayName;
    private String statusText;
}