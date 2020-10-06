package it.izzonline.securityoauthservice.service;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;

import it.izzonline.securityoauthservice.model.User;
import it.izzonline.securityoauthservice.repository.UserRepository;

@Service
public class MfaService {

	@Autowired
	private UserRepository userRepository;

	private GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

	public boolean isEnabled(String username) {
		Optional<User> user = userRepository.findByUsernameIgnoreCase(username);
		if (user.isPresent())
			return user.get().isTwoFaEnabled();
		return false;
	}

	public boolean verifyCode(String username, int code) {
		Optional<User> user = userRepository.findByUsernameIgnoreCase(username);
		String secret = StringUtils.EMPTY;
		if (user.isPresent()) {
			secret = user.get().getSecret();
		}
		return code == googleAuthenticator.getTotpPassword(secret);
	}
}
