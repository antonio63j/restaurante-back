package com.afl.restaurante.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.afl.restaurante.entities.Usuario;
import com.afl.restaurante.services.IUsuarioService;

@Component
public class InfoAdicionalToken implements TokenEnhancer{

	@Autowired
	IUsuarioService usuarioService;
	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		Map <String, Object> info = new HashMap<>();
		info.put ("info adicioanal", "Hola que tal !!".concat(authentication.getName()));
		
		Usuario usuario = usuarioService.findByUsername(authentication.getName());
		info.put ("nombre_usuario", usuario.getNombre());
		info.put ("apellidos_usuario", usuario.getApellidos());
		info.put ("email_usuario", usuario.getEmail());
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
				
		return accessToken;
	}

}
