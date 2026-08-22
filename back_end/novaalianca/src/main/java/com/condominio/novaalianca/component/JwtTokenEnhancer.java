package com.condominio.novaalianca.component;

import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.repositories.UsuarioRepository;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenEnhancer implements TokenEnhancer {

    private final UsuarioRepository userRepository;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        Usuario user = userRepository.findByTxEmail(oAuth2Authentication.getName());
        Map<String, Object> map = new HashMap<>();

        map.put("userName", user.getNomeUsuario());
        map.put("userId", user.getIdUsuario());
        map.put("roles", user.getListPerfis().stream().map(perfil -> perfil.getNomePerfil()).collect(Collectors.toSet()));

        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) oAuth2AccessToken;
        token.setAdditionalInformation(map);

        return oAuth2AccessToken;

    }
}
