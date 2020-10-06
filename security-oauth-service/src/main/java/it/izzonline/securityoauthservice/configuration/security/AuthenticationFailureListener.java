package it.izzonline.securityoauthservice.configuration.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

	@Autowired
	private LoginAttemptService loginAttemptService;

	@Autowired
	private HttpServletRequest request;

	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
		if (e.getSource() instanceof UsernamePasswordAuthenticationToken)
			loginAttemptService.loginFailed(request.getRemoteAddr());
	}
}
