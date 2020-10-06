package it.izzonline.securityoauthservice.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/**
 * Global values used in different part of the application
 */
@EnableAutoConfiguration
public class Constants {

	// Database schemas on which the entities have been included
	public static final class DATABASE_SCHEMA {
		public static final String SECURITY = "security";
	};

}
