package it.izzonline.securityoauthservice.configuration.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import it.izzonline.securityoauthservice.configuration.rest.RestRoutes;
import it.izzonline.securityoauthservice.configuration.security.oauth.CustomJdbcClientDetailsService;
import it.izzonline.securityoauthservice.service.MfaService;
import it.izzonline.securityoauthservice.service.UserService;
import it.izzonline.securityoauthservice.service.cache.ClientDetailsCacheService;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	private AuthenticationManager authManager;
	private MfaService mfaService;
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ClientDetailsCacheService clientDetailsCacheService;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JwtAccessTokenConverter jwtAccessTokenConverter;

	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private UserService userService;

	@Autowired
	private LoginAttemptService loginAttemptService;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	public AuthorizationServerConfig(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
			MfaService mfaService, LoginAttemptService loginAttemptService, HttpServletRequest request) {
		this.passwordEncoder = passwordEncoder;
		this.authManager = authenticationManager;
		this.mfaService = mfaService;
		this.loginAttemptService = loginAttemptService;
		this.request = request;
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		endpoints.pathMapping(RestRoutes.ACCESS_TOKEN_URI.getFirst(), RestRoutes.ACCESS_TOKEN_URI.getSecond())
				.pathMapping(RestRoutes.CHECK_TOKEN_URI.getFirst(), RestRoutes.CHECK_TOKEN_URI.getSecond())
				.pathMapping(RestRoutes.USER_AUTHORIZATION_URI.getFirst(),
						RestRoutes.USER_AUTHORIZATION_URI.getSecond())
				.tokenStore(tokenStore).tokenGranter(tokenGranter(endpoints))
				.accessTokenConverter(jwtAccessTokenConverter).userDetailsService(userService);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) {
		security.checkTokenAccess("isAuthenticated()");
	}

//	@Override
//	public void configure(AuthorizationServerSecurityConfigurer security) {
//		security.checkTokenAccess("permitAll()");
//	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.jdbc(dataSource).passwordEncoder(passwordEncoder);
		clients.withClientDetails(new CustomJdbcClientDetailsService(dataSource, clientDetailsCacheService));
	}

	private TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints) {
		List<TokenGranter> granters = new ArrayList<>();
		granters.add(endpoints.getTokenGranter());
		granters.add(new PasswordTokenGranter(endpoints, authManager, mfaService, loginAttemptService, request));
		granters.add(new MfaTokenGranter(endpoints, authManager, mfaService));
		return new CompositeTokenGranter(granters);
	}

}
