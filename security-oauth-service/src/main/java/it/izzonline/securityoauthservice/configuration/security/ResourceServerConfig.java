package it.izzonline.securityoauthservice.configuration.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

import it.izzonline.securityoauthservice.configuration.rest.RestRoutes.SECURITY_CONTROLLER;
import it.izzonline.securityoauthservice.model.Role;

@EnableResourceServer
@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.headers().frameOptions().disable();
		http.authorizeRequests().antMatchers(SECURITY_CONTROLLER.ROOT + "/api/**").hasAnyAuthority(Role.Code.ROLE_ADMIN,
				Role.Code.ROLE_VULNERABILITY_MANAGER, Role.Code.ROLE_VULNERABILITY_OWNER, Role.Code.ROLE_AUDIT,
				Role.Code.ROLE_ASSESSMENT);

	}

}