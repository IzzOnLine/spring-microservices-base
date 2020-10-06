package it.izzonline.securityoauthservice.configuration.security;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class LoginAttemptService {

	// TODO
//	@Value("${login.max_attempt:3}")
//	private Integer MAX_ATTEMPT;
//
//	@Value("${login.blocking_minutes:3}")
//	private Integer BLOCKING_MINUTES;

	private LoadingCache<String, Integer> attemptsCache;

	public LoginAttemptService() {
		super();
		attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.MINUTES)
				.build(new CacheLoader<String, Integer>() {
					public Integer load(String key) {
						return 0;
					}
				});
	}

	public void loginSucceeded(String key) {
		attemptsCache.invalidate(key);
	}

	public void loginFailed(String key) {
		int attempts = 0;
		try {
			attempts = attemptsCache.get(key);
		} catch (ExecutionException e) {
			attempts = 0;
		}
		attempts++;
		attemptsCache.put(key, attempts);
	}

	public boolean isBlocked(String key) {
		try {
			return attemptsCache.get(key) >= 3;
		} catch (ExecutionException e) {
			return false;
		}
	}
}
