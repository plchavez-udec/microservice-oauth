package co.edu.ierdminayticha.sgd.oauth.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import co.edu.ierdminayticha.sgd.oauth.exception.GeneralException;
import co.edu.ierdminayticha.sgd.oauth.util.Properties;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class UserServiceImpl implements IUserService, UserDetailsService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Properties properties;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// Consumir microservicio usuarios para obtner el usuario por
		// medio del userName informado
		ResponseEntity<String> responseUserByUserName = this.invokeUserByUserName(username);

		// Extraer datos de la respuesta por medio de JSONObject
		JSONObject JSONObjectUser = new JSONObject(responseUserByUserName.getBody());
		String password = JSONObjectUser.getString("password");
		boolean enabled = JSONObjectUser.getBoolean("enabled");
		JSONArray JSONObjecRoleList = JSONObjectUser.getJSONArray("roles");

		// La lista de roles especificos de spring securuty son de la interface
		// GrantedAuthority
		// Los roles especificos se manejan por medio de la clase SimpleGrantedAuthority
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (int i = 0; i < JSONObjecRoleList.length(); i++) {
			JSONObject JSONObjectRole = JSONObjecRoleList.getJSONObject(i);
			authorities.add(new SimpleGrantedAuthority(JSONObjectRole.getString("name")));
		}

		// Retorno de User, la impl especifica de Usuaris que maneja Spring Security
		return new User(username, password, enabled, true, true, true, authorities);
	}

	@Override
	public ResponseEntity<String> invokeUserByUserName(String userName) {
		log.info("DocumentaryUnitServiceImpl : invokeUserByUserName - Invocando microservicio document " + "process");
		ResponseEntity<String> response = null;
		Map<String, Object> uriParams = new HashMap<>();
		uriParams.put("user-name", userName);
		try {
			log.info("endpoint - {}", properties.getUrlGetUserByUserName());
			response = restTemplate.getForEntity(properties.getUrlGetUserByUserName(), String.class, uriParams);
		} catch (HttpClientErrorException e) {
			log.error("DocumentaryUnitServiceImpl : invokeUserByUserName - (HttpClientErrorException) falló el "
					+ "consumo del microservicio, error: {}", e.getCause());
			throw new GeneralException(e.getMessage());
		} catch (RestClientException e) {
			log.error("DocumentaryUnitServiceImpl : invokeUserByUserName - (HttpClientErrorException) falló el "
					+ "consumo del microservicio, error: {}", e.getCause());
			throw new GeneralException(e.getMessage());
		}
		return response;
	}
	

}
