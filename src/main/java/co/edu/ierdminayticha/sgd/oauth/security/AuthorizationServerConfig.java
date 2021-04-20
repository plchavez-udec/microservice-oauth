package co.edu.ierdminayticha.sgd.oauth.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@RefreshScope
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private Environment env;

	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private AdditionalInformationToken additionalInformationToken;

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {

		security.tokenKeyAccess("permitAll()") // Quien tiene permisos al endint para generar el token
				.checkTokenAccess("isAuthenticated()") // Validar si el cliente esta autenticado
		;
	}

	// Metodo para realizar la config de cada uno de los clientes que pueden
	// consumir nuestros microservicios. Pueden ser Angular, JSP etc
	// por seguridad la autenticación no solo se hace por medio de las credenciales
	// de los usuario
	// tambien se tienen en cienta las cledenciales de las app que nos consumen
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().withClient(env.getProperty("config.security.oauth.client.id"))
				.secret(bcryptPasswordEncoder.encode(env.getProperty("config.security.oauth.client.secret")))
				.scopes("read", "write")// Alcance o permisos que tiene la app
				.authorizedGrantTypes("password", "refresh_token") // como se va a obtener el token (credenciales)
				.accessTokenValiditySeconds(300)// Tiempo de valide del token
				.refreshTokenValiditySeconds(360); // Tiempo de valide del refreshtoken
	}

	// endpoints relacionado al endpoints del servidor de autorización que se
	// encarga de generar
	// token /oauth/token
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

		// Agregar la información adicional para generar el token JWT
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(additionalInformationToken, accessTokenConverter()));

		// Se le pasan todos los datos del usuario al endpoint para que a partir de
		// ellos genere el token.
		endpoints.authenticationManager(authenticationManager)// registrar authenticationManager añl servidor
				.tokenStore(tokenStore())// Componenetee que se encarga de generar el token, teniendo en cuenta el
											// siguiente punto
				.accessTokenConverter(accessTokenConverter()).tokenEnhancer(tokenEnhancerChain);// Configuracion del
																								// convertidor de token
	}

	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	// Configuracion del convertidor de token, para este caso es
	// JwtAccessTokenConverter, convierte el token el JWT
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		// Codigo secreto para generar y validar el token
		accessTokenConverter.setSigningKey(env.getProperty("config.security.oauth.jwt.key"));
		return accessTokenConverter;
	}

}
