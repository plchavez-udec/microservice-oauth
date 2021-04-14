package co.edu.ierdminayticha.sgd.oauth.service;

import org.springframework.http.ResponseEntity;

public interface IUserService {

	ResponseEntity<String> invokeUserByUserName(String userName);

}
