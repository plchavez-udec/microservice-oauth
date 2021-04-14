package co.edu.ierdminayticha.sgd.oauth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
public class Properties {
	
	@Getter
	@Value("${url.microservice.user.find-by-username}")
	private String urlGetUserByUserName;

}
