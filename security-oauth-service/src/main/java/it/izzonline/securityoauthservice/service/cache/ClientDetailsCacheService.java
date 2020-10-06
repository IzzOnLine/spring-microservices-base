package it.izzonline.securityoauthservice.service.cache;

import static java.util.Optional.ofNullable;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.stereotype.Service;

import it.izzonline.securityoauthservice.configuration.cache.CacheConfiguration;

@Service
public class ClientDetailsCacheService {

	private CacheConfiguration cacheConfiguration;
	private CacheService cacheService;

	@Autowired
	public ClientDetailsCacheService(@Lazy CacheConfiguration cacheConfiguration, @Lazy CacheService cacheService) {
		this.cacheConfiguration = cacheConfiguration;
		this.cacheService = cacheService;
	}

	/**
	 * Check if exists the given {@link ClientDetails#getClientId()} inside the
	 * related cache.
	 *
	 * @param clientId {@link ClientDetails#getClientId()} to search
	 *
	 * @return {@code true} if the {@code clientId} exists, {@code false} otherwise
	 */
	public boolean contains(String clientId) {
		return ofNullable(clientId).map(id -> cacheService.contains(cacheConfiguration.getOauthClientCacheName(), id))
				.orElse(false);
	}

	/**
	 * Return the {@link ClientDetails} related with the given {@code clientId}
	 * inside the related cache.
	 *
	 * @param clientId {@link ClientDetails#getClientId()} to search
	 *
	 * @return @return {@link Optional} with the {@link ClientDetails} if it was
	 *         found, {@link Optional#empty()} otherwise
	 */
	public Optional<ClientDetails> get(String clientId) {
		return cacheService.get(cacheConfiguration.getOauthClientCacheName(), clientId);
	}

	/**
	 * Include a pair of {@link ClientDetails#getClientId()} - {@link ClientDetails}
	 * inside the related cache.
	 *
	 * @param clientId      {@link ClientDetails#getClientId()} used to identify the
	 *                      {@link ClientDetails} to store
	 * @param clientDetails {@link ClientDetails} to store
	 *
	 * @return {@code true} if the data was stored, {@code false} otherwise
	 */
	public boolean put(String clientId, ClientDetails clientDetails) {
		return cacheService.put(cacheConfiguration.getOauthClientCacheName(), clientId, clientDetails);
	}

}
