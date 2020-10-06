package it.izzonline.securityoauthservice.controller;

import java.net.URLEncoder;
import java.util.Optional;

import javax.annotation.Resource;

import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.izzonline.securityoauthservice.configuration.rest.RestRoutes.SECURITY_CONTROLLER;
import it.izzonline.securityoauthservice.model.User;
import it.izzonline.securityoauthservice.repository.UserRepository;
import it.izzonline.securityoauthservice.service.SecurityUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(SECURITY_CONTROLLER.ROOT + "/api/user")
@Slf4j
public class UserController {

	public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";

	@Autowired
	private UserRepository userRepository;

	@Value("${spring.application.name}")
	private String applicationName;

	@PostMapping("/enable-2fa")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Boolean> enable2FA(String code) {
		log.debug("REST request to enable 2FA authentication");
		Optional<String> username = SecurityUtil.getCurrentUserLogin();
		if (username.isPresent()) {
			Optional<User> dbUser = userRepository.findByUsernameIgnoreCase(username.get());
			if (dbUser.isPresent()) {
				User u = dbUser.get();
				Totp totp = new Totp(u.getSecret());
				boolean verify = totp.verify(code);
				if (verify) {
					u.setTwoFaEnabled(true);
					userRepository.save(u);
					return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
				}
			}
		}
		return new ResponseEntity<>(Boolean.FALSE, HttpStatus.PRECONDITION_FAILED);
	}

	@PostMapping("/generate-qr")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<String> generateQRUrl() throws Exception {
		log.debug("REST request to get a new QR code");
		Optional<String> username = SecurityUtil.getCurrentUserLogin();
		String secret = Base32.random();
		if (username.isPresent()) {
			Optional<User> dbUser = userRepository.findByUsernameIgnoreCase(username.get());
			if (dbUser.isPresent()) {
				User u = dbUser.get();
				u.setSecret(secret);
				userRepository.save(u);
				return new ResponseEntity<>(
						QR_PREFIX + URLEncoder.encode(String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
								applicationName.replaceAll("\\s+", ""), u.getUsername(), secret,
								applicationName.replaceAll("\\s+", "")), "UTF-8"),
						HttpStatus.OK);
			}
		}
		return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
	}

	@PostMapping("/disable-2fa")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Boolean> disable2FA() {
		log.debug("REST request to disable 2FA authentication");
		Optional<String> username = SecurityUtil.getCurrentUserLogin();
		Optional<User> dbUser = userRepository.findByUsernameIgnoreCase(username.get());
		if (dbUser.isPresent()) {
			User u = dbUser.get();
			u.setSecret(null);
			u.setTwoFaEnabled(false);
			userRepository.save(u);
			return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
		}
		return new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Resource(name = "tokenServices")
	ConsumerTokenServices tokenServices;

	@PostMapping("/revoke/{tokenId:.*}")
	@PreAuthorize("isAuthenticated()")
	public void revokeToken(@PathVariable String tokenId) {
		tokenServices.revokeToken(tokenId);
		log.debug(tokenId);
	}

}
