package com.condominio.novaalianca.controller;

import com.condominio.novaalianca.config.NovaAliancaProperties;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.repositories.UsuarioRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/oauth/token")
@RequiredArgsConstructor
public class OAuthTokenController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final NovaAliancaProperties properties;

    @PostMapping
    public ResponseEntity<?> token(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("grant_type") String grantType) {

        if (!"password".equals(grantType)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "unsupported_grant_type"));
        }

        // Validate credentials using the AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        Usuario user = usuarioRepository.findByTxEmail(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "invalid_grant", "error_description", "Usuário não encontrado"));
        }

        try {
            // Build JWT payload with identical claims as original enhancer
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + properties.getJwtDuration() * 1000L))
                    .claim("userName", user.getNomeUsuario())
                    .claim("userId", user.getIdUsuario())
                    .claim("roles", user.getListPerfis().stream().map(perfil -> perfil.getNomePerfil()).collect(Collectors.toSet()))
                    .build();

            // Sign the JWT using HMAC-SHA256
            JWSSigner signer = new MACSigner(properties.getJwtSecret().getBytes());
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);

            String tokenString = signedJWT.serialize();

            // Response JSON matching OAuth2 schema
            Map<String, Object> response = new HashMap<>();
            response.put("access_token", tokenString);
            response.put("token_type", "bearer");
            response.put("expires_in", properties.getJwtDuration());
            response.put("scope", "read write");
            response.put("userName", user.getNomeUsuario());
            response.put("userId", user.getIdUsuario());

            return ResponseEntity.ok(response);

        } catch (JOSEException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "token_generation_failed", "error_description", e.getMessage()));
        }
    }
}
