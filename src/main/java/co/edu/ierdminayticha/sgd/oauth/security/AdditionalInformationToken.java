package co.edu.ierdminayticha.sgd.oauth.security;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import co.edu.ierdminayticha.sgd.oauth.service.IUserService;

@Component
public class AdditionalInformationToken implements TokenEnhancer {

	@Autowired
	private IUserService userService;

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

		Map<String, Object> additionalInformation = new HashMap<>();

		ResponseEntity<String> responseUserByUserName = this.userService.invokeUserByUserName(authentication.getName());
		// Extraer datos de la respuesta por medio de JSONObject
		JSONObject JSONObjectUser = new JSONObject(responseUserByUserName.getBody());
		String name = JSONObjectUser.getString("nombre");
		String lastName = JSONObjectUser.getString("apellido");
		String email = JSONObjectUser.getString("email");
		// Ingresar datos al Map
		additionalInformation.put("nombre", name);
		additionalInformation.put("apellido", lastName);
		additionalInformation.put("correo", email);

		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);

		return accessToken;
	}

}
