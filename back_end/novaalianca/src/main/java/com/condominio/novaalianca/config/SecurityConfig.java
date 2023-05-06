//package com.condominio.novaalianca.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
//import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
//
//@Configuration
//public class SecurityConfig {
//
//  @Autowired
//  private  NovaAliancaProperties properties;
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public JwtAccessTokenConverter accessTokenConverter() {
//        JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
//        tokenConverter.setSigningKey(properties.getJwtSecret());
//        return tokenConverter;
//    }
//
//    @Bean
//    public JwtTokenStore tokenStore() {
//        return new JwtTokenStore(accessTokenConverter());
//    }
//}
