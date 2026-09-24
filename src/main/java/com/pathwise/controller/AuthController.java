package com.pathwise.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.pathwise.entity.User;
import com.pathwise.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${google.client.id:YOUR_GOOGLE_CLIENT_ID.apps.googleusercontent.com}")
    private String googleClientId;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody GoogleAuthRequest request) {
        try {
            String email = request.getEmail();
            String name = request.getName();
            String picture = request.getPicture();

            // Attempt official Google ID Token verification if a valid JWT ID Token format is supplied
            if (request.getToken() != null && request.getToken().split("\\.").length == 3) {
                try {
                    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                            new NetHttpTransport(), GsonFactory.getDefaultInstance())
                            .setAudience(Collections.singletonList(googleClientId))
                            .build();

                    GoogleIdToken idToken = verifier.verify(request.getToken());
                    if (idToken != null) {
                        GoogleIdToken.Payload payload = idToken.getPayload();
                        if (payload.getEmail() != null) email = payload.getEmail();
                        if (payload.get("name") != null) name = (String) payload.get("name");
                        if (payload.get("picture") != null) picture = (String) payload.get("picture");
                    }
                } catch (Exception ex) {
                    System.err.println("ID Token verification fallback to payload: " + ex.getMessage());
                }
            }

            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body("Email is required for authentication");
            }

            final String userEmail = email;
            final String userName = (name != null && !name.isBlank()) ? name : email.split("@")[0];
            final String userPicture = picture;

            User user = userRepository.findByEmail(userEmail).map(existing -> {
                if (userName != null && !userName.isBlank()) existing.setName(userName);
                if (userPicture != null && !userPicture.isBlank()) existing.setProfilePicture(userPicture);
                return userRepository.save(existing);
            }).orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(userEmail);
                newUser.setName(userName);
                newUser.setProfilePicture(userPicture);
                newUser.setRole("STUDENT");
                return userRepository.save(newUser);
            });

            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            String jwtToken = Jwts.builder()
                    .setSubject(user.getEmail())
                    .claim("role", user.getRole())
                    .claim("name", user.getName())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            return ResponseEntity.ok(new AuthResponse(jwtToken, user));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Authentication failed: " + e.getMessage());
        }
    }
}

class GoogleAuthRequest {
    private String token;
    private String email;
    private String name;
    private String picture;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }
}

class AuthResponse {
    private String token;
    private User user;

    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}