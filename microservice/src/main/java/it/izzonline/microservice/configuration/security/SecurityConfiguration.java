package it.izzonline.microservice.configuration.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

/**
 * Configuration properties related with authentication/authorization (web service used for it)
 */
@Getter
@Configuration
public class SecurityConfiguration {

    public static final String TOKEN_PREFIX = "Bearer ";

    @Value("${security.restApi.authenticationInformation}")
    private String authenticationInformationWebService;

    @Value("${security.restApi.clientId}")
    private String clientId;

    @Value("${security.restApi.clientPassword}")
    private String clientPassword;

}
