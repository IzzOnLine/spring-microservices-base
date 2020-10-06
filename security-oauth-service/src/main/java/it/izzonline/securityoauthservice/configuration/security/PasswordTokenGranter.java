package it.izzonline.securityoauthservice.configuration.security;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;

import it.izzonline.securityoauthservice.service.MfaService;

public class PasswordTokenGranter extends AbstractTokenGranter {
	private static final String GRANT_TYPE = "password";
	private static final GrantedAuthority PRE_AUTH = new SimpleGrantedAuthority("PRE_AUTH");

	private final AuthenticationManager authenticationManager;
	private final MfaService mfaService;
	private final LoginAttemptService loginAttemptService;
	private final HttpServletRequest request;

	public PasswordTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer,
			AuthenticationManager authenticationManager, MfaService mfaService, LoginAttemptService loginAttemptService,
			HttpServletRequest request) {
		super(endpointsConfigurer.getTokenServices(), endpointsConfigurer.getClientDetailsService(),
				endpointsConfigurer.getOAuth2RequestFactory(), GRANT_TYPE);
		this.authenticationManager = authenticationManager;
		this.mfaService = mfaService;
		this.loginAttemptService = loginAttemptService;
		this.request = request;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		String ip = getClientIP();
		if (loginAttemptService.isBlocked(ip)) {
			// TODO bloccare utente a DB??
			throw new InvalidGrantException("Login attempt blocked");
		}
		Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
		String username = parameters.get("username");
		String password = parameters.get("password");
		parameters.remove("password");
		Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);
		((AbstractAuthenticationToken) userAuth).setDetails(parameters);

		try {
			userAuth = this.authenticationManager.authenticate(userAuth);
		} catch (AccountStatusException | BadCredentialsException e) {
			throw new InvalidGrantException(e.getMessage());
		}

		if (userAuth != null && userAuth.isAuthenticated()) {
			OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);
			if (mfaService.isEnabled(username)) {
				userAuth = new UsernamePasswordAuthenticationToken(username, password, Collections.singleton(PRE_AUTH));
				OAuth2AccessToken accessToken = getTokenServices()
						.createAccessToken(new OAuth2Authentication(storedOAuth2Request, userAuth));
				throw new MfaRequiredException(accessToken.getValue());
			}
			return new OAuth2Authentication(storedOAuth2Request, userAuth);
		} else {
			throw new InvalidGrantException("Could not authenticate user: " + username);
		}
	}

	private String getClientIP() {
		String xfHeader = request.getHeader("X-Forwarded-For");
		if (xfHeader == null) {
			return request.getRemoteAddr();
		}
		return xfHeader.split(",")[0];
	}
}
