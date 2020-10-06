package it.izzonline.securityoauthservice.configuration.security.jwt;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * Manages the information included in access and refresh tokens.
 */
public class CustomAccessTokenConverter extends JwtAccessTokenConverter {

	private static final String AUTHORITIES = "authorities";
	private static final String SCOPE = "scope";
	private static final String USERNAME = "username";
	private static final String ADDITIONAL_INFO = "additionalInfo";

	public CustomAccessTokenConverter() {
		super();
	}

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		OAuth2AccessToken result = super.enhance(accessToken, authentication);
		result.getAdditionalInformation().putAll(getAdditionalInformation(authentication));
		return result;
	}

	@Override
	public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		Map<String, Object> defaultInformation = (Map<String, Object>) super.convertAccessToken(token, authentication);
		return this.isRefreshToken(token) ? getRefreshTokenInformation(defaultInformation)
				: getAccessTokenInformation(defaultInformation);
	}

	/**
	 * Filter the data included in the JWT access token
	 */
	private Map<String, ?> getAccessTokenInformation(Map<String, Object> sourceInformation) {
		Map<String, Object> accessTokenInformation = new HashMap<>(sourceInformation);
		accessTokenInformation.keySet().removeIf(k -> asList(SCOPE).contains(k));
		return accessTokenInformation;
	}

	/**
	 * Filter the data included in the JWT refresh token
	 */
	private Map<String, ?> getRefreshTokenInformation(Map<String, Object> sourceInformation) {
		Map<String, Object> refreshTokenInformation = new HashMap<>(sourceInformation);
		refreshTokenInformation.keySet().removeIf(k -> asList(AUTHORITIES, SCOPE).contains(k));
		return refreshTokenInformation;
	}

	/**
	 * Include an specific section with extra information in the returned
	 * {@link OAuth2AccessToken}
	 */
	private Map<String, Object> getAdditionalInformation(OAuth2Authentication authentication) {

		Map<String, Object> authenticationAdditionalInformation = Stream
				.of(new AbstractMap.SimpleEntry<>(USERNAME, authentication.getUserAuthentication().getName()),
						new AbstractMap.SimpleEntry<>(AUTHORITIES,
								authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
										.collect(toSet())))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		Map<String, Object> retVal = new HashMap<>();
		retVal.put(ADDITIONAL_INFO, authenticationAdditionalInformation);

		return retVal;
	}

}
