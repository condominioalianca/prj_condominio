package com.condominio.novaalianca.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import com.condominio.novaalianca.repositories.ParametrosSistemaRepository;
import com.condominio.novaalianca.enums.ParametrosSistema;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ResourceServerConfig {

    private final NovaAliancaProperties properties;
    private final Environment env;
    private final ParametrosSistemaRepository parametrosSistemaRepository;

    private static final String[] PUBLICO = {"/oauth/token", "/h2-console/**", "/swagger-ui/**", "/testes/**", "/extrato/**", "/actuator/health"};
    private static final String[] ADMIN = {"/parametros/**"};
    private static final String[] SINDICO = {"/boleto/**", "/endereco/**", "/unidade/**", "/usuarios/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // H2 Frame options configuration for test profile
        if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
            http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
        }

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(PUBLICO).permitAll()
                .requestMatchers(HttpMethod.GET, SINDICO).permitAll()
                .requestMatchers(ADMIN).hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_SINDICO")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));

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
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            String originsStr = null;
            try {
                originsStr = parametrosSistemaRepository.findValorParametro(ParametrosSistema.CORS_ORIGINS.toString());
            } catch (Exception e) {
                // Fallback se o banco não estiver acessível
            }

            if (originsStr == null || originsStr.trim().isEmpty()) {
                originsStr = properties.getCorsOrigins();
            }

            java.util.List<String> allowedOrigins = Arrays.stream(originsStr.split(","))
                    .map(String::trim)
                    .map(origin -> origin.endsWith("/") ? origin.substring(0, origin.length() - 1) : origin)
                    .filter(origin -> !origin.isEmpty())
                    .collect(java.util.stream.Collectors.toList());

            CorsConfiguration corsConfig = new CorsConfiguration();
            corsConfig.setAllowedOriginPatterns(allowedOrigins);
            corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH", "OPTIONS"));
            corsConfig.setAllowCredentials(true);
            corsConfig.setAllowedHeaders(Arrays.asList("*"));
            return corsConfig;
        };
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfigurationSource().getCorsConfiguration(null));
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
