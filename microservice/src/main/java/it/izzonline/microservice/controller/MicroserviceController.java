package it.izzonline.microservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/microservice/api/x")
@Api("Microservice Controller")
public class MicroserviceController {

	@PreAuthorize("hasRole('ROLE_USER')")
	// @RoleAdminOrUser
	@GetMapping("/a")
	public void testA(Authentication authentication) {
		log.debug("TEST A -------------------------");
	}
	
	@PreAuthorize("hasRole('ROLE_PIPPO')")
	@GetMapping("/b")
	public void testB(Authentication authentication) {
		log.debug("TEST B -------------------------");
	}

}
