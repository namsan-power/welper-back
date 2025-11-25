package com.example.welperback.controller.client;

import com.example.welperback.dto.client.ClientCreateRequest;
import com.example.welperback.dto.client.ClientResponse;
import com.example.welperback.dto.client.ClientUpdateRequest;
import com.example.welperback.global.response.ApiResponse;
import com.example.welperback.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/users/{userId}/clients")
    public ApiResponse<List<ClientResponse>> getClients(@PathVariable Long userId) {
        return ApiResponse.success(clientService.getClients(userId));
    }

    @PostMapping("/clients")
    public ApiResponse<ClientResponse> createClient(@RequestBody ClientCreateRequest request, @AuthenticationPrincipal Object principal) {
        String email = getEmailFromPrincipal(principal);
        return ApiResponse.success(clientService.createClient(request, email));
    }

    @GetMapping("/clients/{clientId}")
    public ApiResponse<ClientResponse> getClient(@PathVariable Long clientId) {
        return ApiResponse.success(clientService.getClient(clientId));
    }

    @PatchMapping("/clients/{clientId}")
    public ApiResponse<ClientResponse> updateClient(@PathVariable Long clientId, @RequestBody ClientUpdateRequest request) {
        return ApiResponse.success(clientService.updateClient(clientId, request));
    }

    @DeleteMapping("/clients/{clientId}")
    public ApiResponse<Void> deleteClient(@PathVariable Long clientId) {
        clientService.deleteClient(clientId);
        return ApiResponse.success(null);
    }

    private String getEmailFromPrincipal(Object principal) {
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return (String) principal;
    }
}
