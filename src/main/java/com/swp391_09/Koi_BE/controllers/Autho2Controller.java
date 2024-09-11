package com.swp391_09.Koi_BE.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.swp391_09.Koi_BE.dtos.TokenDTO;
import com.swp391_09.Koi_BE.responses.LoginResponse;
import com.swp391_09.Koi_BE.services.IUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


@RestController
@RequestMapping("/api/v1/autho2") // full path localhost:8080/api/v1/autho2
public class Autho2Controller {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    private final IUserService userService;

    public Autho2Controller(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateGoogle(@RequestBody TokenDTO tokenRequest) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            System.out.println("Token: " + tokenRequest.getToken());

            GoogleIdToken idToken = verifier.verify(tokenRequest.getToken());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                System.out.println("Payload: " + payload);

                String userId = payload.getSubject();
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                String token = userService.loginOrRegisterGoogle(email, name, userId);

                return ResponseEntity.ok(LoginResponse.builder()
                        .message("Google authentication successful")
                        .token(token)
                        .build());
            } else {
                return ResponseEntity.badRequest().body("Invalid ID token.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error during authentication: " + e.getMessage());
        }
    }

}

