package com.condominio.novaalianca.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lombok.Getter;
import lombok.Setter;

@Primary
@Configuration
@ConfigurationProperties(prefix = "inter")
@Getter
@Setter
public class PropertyLoader {
	
    private String url;
    private String headerKey;
    private String headerValue;

    private String client_id;
    private String scope;
    private String client_secret;
    private String grant_type;
    private String token_uri;
	


}
