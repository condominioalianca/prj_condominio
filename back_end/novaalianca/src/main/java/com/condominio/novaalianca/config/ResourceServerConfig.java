package com.condominio.novaalianca.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import com.condominio.novaalianca.repositories.ParametrosSistemaRepository;
import com.condominio.novaalianca.enums.ParametrosSistema;

import java.util.Arrays;

@Configuration
@EnableResourceServer
@RequiredArgsConstructor
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private final NovaAliancaProperties properties;
    private final Environment env;

    private final JwtTokenStore tokenStore;

    private final ParametrosSistemaRepository parametrosSistemaRepository;

    private static final String[] PUBLICO = {"/oauth/token", "/h2-console/**", "/swagger-ui/**","/testes/**","/extrato/**"};

    private static final String[] ADMIN = {"/parametros/**"};

    private static final String[] SINDICO = {"/boleto/**", "/endereco/**", "/unidade/**", "/usuarios/**"};

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(tokenStore);
    }

    //TODO REFATORAR
    @Override
    public void configure(HttpSecurity http) throws Exception {

        // H2
        if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
            http.headers().frameOptions().disable();
        }

        http.
                authorizeRequests()
                .antMatchers(PUBLICO).permitAll()
                .antMatchers(HttpMethod.GET, SINDICO).permitAll()
                .antMatchers(ADMIN).hasAnyAuthority("ADMINISTRADOR", "SINDICO")
                .anyRequest().authenticated();
//        http
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); //não guardar a sessao


        http.cors().configurationSource(corsConfigurationSource());
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(javax.servlet.http.HttpServletRequest request) {
                String originsStr = null;
                try {
                    originsStr = parametrosSistemaRepository.findValorParametro(ParametrosSistema.CORS_ORIGINS.toString());
                } catch (Exception e) {
                    // Fallback se o banco não estiver acessível ou o parâmetro não existir
                }

                if (originsStr == null || originsStr.trim().isEmpty()) {
                    originsStr = properties.getCorsOrigins();
                }

                String[] origins = originsStr.split(",");

                CorsConfiguration corsConfig = new CorsConfiguration();
                corsConfig.setAllowedOriginPatterns(Arrays.asList(origins));
                corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH"));
                corsConfig.setAllowCredentials(true);
                corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
                return corsConfig;
            }
        };
    }

    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> bean
                = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }


}
