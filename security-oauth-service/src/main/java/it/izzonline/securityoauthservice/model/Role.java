package it.izzonline.securityoauthservice.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

	ROLE_ADMIN(Code.ROLE_ADMIN), ROLE_VULNERABILITY_MANAGER(Code.ROLE_VULNERABILITY_MANAGER),
	ROLE_VULNERABILITY_OWNER(Code.ROLE_VULNERABILITY_OWNER), ROLE_AUDIT(Code.ROLE_AUDIT),
	ROLE_ASSESSMENT(Code.ROLE_ASSESSMENT), ROLE_ANONYMOUS(Code.ROLE_ANONYMOUS);

	private final String authority;

	Role(String authority) {
		this.authority = authority;
	}

	@Override
	public String getAuthority() {
		return authority;
	}

	public class Code {
		public static final String ROLE_ADMIN = "ROLE_ADMIN";
		public static final String ROLE_VULNERABILITY_MANAGER = "ROLE_VULNERABILITY_MANAGER";
		public static final String ROLE_VULNERABILITY_OWNER = "ROLE_VULNERABILITY_OWNER";
		public static final String ROLE_ASSESSMENT = "ROLE_ASSESSMENT";
		public static final String ROLE_AUDIT = "ROLE_AUDIT";

		public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";

		public static final String IS_AUTHENTICATED_FULLY = "IS_AUTHENTICATED_FULLY";

		public static final String ROLE_HIERARCHY = Role.Code.ROLE_ADMIN + " > " + Role.Code.ROLE_VULNERABILITY_MANAGER
				+ " > " + Role.Code.ROLE_VULNERABILITY_OWNER + " > " + Role.Code.ROLE_AUDIT + " > "
				+ Role.Code.ROLE_ASSESSMENT;
	}
}