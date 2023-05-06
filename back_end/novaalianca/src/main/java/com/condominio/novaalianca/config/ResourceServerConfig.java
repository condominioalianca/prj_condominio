//package com.condominio.novaalianca.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
//
//import java.util.Arrays;
//
//@Configuration
//@EnableResourceServer
//public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
//
//    @Autowired
//    private Environment env;
//
//    @Autowired
//    private JwtTokenStore tokenStore;
//
//   private static final String[] PUBLICO = { "/oauth/token", "/h2-console/**" , "/swagger-ui/**"};
//
//    private static final String[] ADMIN = { "/parametros/**" };
//
//    private static final String[] SINDICO = { "/boleto/**", "/endereco/**", "/unidade/**" , "/usuarios/**" };
//
//    private static final String[] USUARIO = { "/users/**" };
//
//    @Override
//    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
//       resources.tokenStore(tokenStore);
//    }
//
//
//    //TODO REFATORAR
//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//
//        // H2
//        if (Arrays.asList(env.getActiveProfiles()).contains("dev")) {
//            http.headers().frameOptions().disable();
//        }
//
//
//        http.authorizeRequests()
//                .antMatchers(PUBLICO).permitAll()
//                .antMatchers(HttpMethod.GET,SINDICO).permitAll()
//                .antMatchers(ADMIN).hasAnyRole("ADMINISTRADOR", "SINDICO")
//                .anyRequest().authenticated();
//    }
//}
