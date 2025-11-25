package com.example.welperback.controller.user;

import com.example.welperback.dto.user.UserCreateRequest;
import com.example.welperback.dto.user.UserResponse;
import com.example.welperback.global.response.ApiResponse;
import com.example.welperback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody UserCreateRequest request) {
        // TODO: Get current admin ID from security context
        Long adminId = 0L; // Placeholder
        return ApiResponse.success(userService.createUser(request, adminId));
    }
}
