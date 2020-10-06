package it.izzonline.microservice.configuration;

/**
 * Global values used in different part of the application
 */
public class Constants {

    // Existing roles to manage the authorizations
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    // Token configuration
    public static final String TOKEN_PREFIX = "Bearer ";

    // Path of the folders in the application
    public static final class PATH {
        public static final String CONTROLLER = "it.izzonline.microservice.controller";
    }

}
