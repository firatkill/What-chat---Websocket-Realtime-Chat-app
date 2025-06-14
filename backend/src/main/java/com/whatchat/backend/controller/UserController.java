package com.whatchat.backend.controller;

import com.whatchat.backend.dto.auth.UserResponse;
import com.whatchat.backend.dto.user.UpdateProfileRequest;
import com.whatchat.backend.entity.User;
import com.whatchat.backend.payload.response.ApiResponse;
import com.whatchat.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;



    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@AuthenticationPrincipal User user,
                                                                   @RequestBody UpdateProfileRequest request) {

        UserResponse data=userService.updateProfile(user,request);

        ApiResponse<UserResponse> response = new ApiResponse<>(true,"User Fetched Successfully",null,data);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/me/avatar")
    public ResponseEntity<ApiResponse<UserResponse>> uploadAvatar(@AuthenticationPrincipal User user,
                             @RequestBody Map<String, String> body) {
        String base64 = body.get("avatar");
        userService.uploadAvatar(user, java.util.Base64.getDecoder().decode(base64));
        ApiResponse<UserResponse> response = new ApiResponse<>(true,"Avatar uploaded successfully",null,null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<User>>> search(@RequestParam String q,
                             @AuthenticationPrincipal User user) {
        List<User> data= userService.searchUsers(q, user);
        // TODO: paging yap, şu anda tüm datalar çekiliyor

        ApiResponse<List<User>> response = new ApiResponse<>(true,"Matching Users Fetched Successfully",null,data);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/status/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> status(@PathVariable UUID id) {
        User user = userService.getUserStatus(id);
        Map<String, Object> data= Map.of(
                "isOnline", user.isOnline(),
                "lastSeen", user.getLastSeen()
        );

        ApiResponse<Map<String, Object>> response = new ApiResponse<>(true,"User Status Fetched Successfully",null,data);
        return ResponseEntity.ok(response);

    }
}