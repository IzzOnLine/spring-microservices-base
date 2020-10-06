package it.izzonline.microservice.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

import it.izzonline.microservice.configuration.Constants;

@Documented
@Retention(RUNTIME)
@Target({METHOD, TYPE})
@PreAuthorize("hasAnyAuthority('" + Constants.ROLE_ADMIN +"','" + Constants.ROLE_USER + "')")
public @interface RoleAdminOrUser {
}
