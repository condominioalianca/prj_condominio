package com.condominio.novaalianca.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import com.condominio.novaalianca.repositories.ParametrosSistemaRepository;

import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ResourceServerConfig {

    private final NovaAliancaProperties properties;
    private final Environment env;
    private final ParametrosSistemaRepository parametrosSistemaRepository;

    private static final String[] PUBLICO = {
        "/oauth/token", "/api/oauth/token",
        "/h2-console/**", "/api/h2-console/**",
        "/swagger-ui/**", "/api/swagger-ui/**",
        "/testes/**", "/api/testes/**",
        "/extrato/**", "/api/extrato/**",
        "/actuator/health", "/api/actuator/health",
        "/usuarios/cadastrar", "/api/usuarios/cadastrar",
        "/auth/password-reset/**", "/api/auth/password-reset/**",
        "/error", "/api/error"
    };
    private static final String[] ADMIN = {"/parametros/**"};
    private static final String[] SINDICO = {"/boleto/**", "/endereco/**", "/unidade/**", "/usuarios/**"};
    private static final String[] CONCILIACAO_COMPROVANTE_MUTATION = {
        "/conciliacao/extrato/**",
        "/conciliacao/*/gerar-pdf",
        "/comprovante/**"
    };
    private static final String[] ROLES_AUTORIZADAS = {
        "ROLE_ADMINISTRADOR",
        "ROLE_SINDICO",
        "ROLE_USUARIO"
    };

    @Bean
    @org.springframework.core.annotation.Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // H2 Frame options configuration for test profile
        if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
            http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
        }

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(PUBLICO).permitAll()
                .requestMatchers(HttpMethod.GET, SINDICO).permitAll()
                .requestMatchers(ADMIN).hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_SINDICO")
                .requestMatchers(HttpMethod.POST, CONCILIACAO_COMPROVANTE_MUTATION).hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_SINDICO")
                .requestMatchers(HttpMethod.PATCH, CONCILIACAO_COMPROVANTE_MUTATION).hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_SINDICO")
                .requestMatchers(HttpMethod.DELETE, CONCILIACAO_COMPROVANTE_MUTATION).hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_SINDICO")
                .requestMatchers("/saldo/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/conciliacao/**", "/comprovante/**").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .bearerTokenResolver(bearerTokenResolver())
            );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(properties.getJwtSecret().getBytes(), "HmacSHA256")).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver resolver = new DefaultBearerTokenResolver();
        resolver.setAllowUriQueryParameter(true);
        return resolver;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration corsConfig = new CorsConfiguration();
            String corsOrigins = parametrosSistemaRepository.findValorParametro("CORS_ORIGINS");

            List<String> origins = new ArrayList<>();
            if (corsOrigins != null && !corsOrigins.trim().isEmpty()) {
                origins.addAll(Arrays.stream(corsOrigins.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList()));
            }

            // Fallback para garantir funcionamento em desenvolvimento local e via DuckDNS
            if (!origins.contains("https://*.duckdns.org*")) {
                origins.add("https://*.duckdns.org*");
            }
            if (!origins.contains("http://localhost:3001")) {
                origins.add("http://localhost:3001");
            }
            if (!origins.contains("http://192.168.15.10:3001")) {
                origins.add("http://192.168.15.10:3001");
            }

            corsConfig.setAllowedOriginPatterns(origins);
            corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH", "OPTIONS"));
            corsConfig.setAllowCredentials(true);
            corsConfig.setAllowedHeaders(Arrays.asList("*"));
            corsConfig.setExposedHeaders(Arrays.asList("Content-Disposition", "Authorization"));
            return corsConfig;
        };
    }
}
