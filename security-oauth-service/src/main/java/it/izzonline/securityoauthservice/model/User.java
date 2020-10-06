package it.izzonline.securityoauthservice.model;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Loader;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "\"user\"")
@Loader(namedQuery = "findUserById")
@NamedQuery(name = "findUserById", query = "SELECT u " + "from User u " + "WHERE " + "  u.id = ?1 order by u.id")
@SQLDelete(sql = "UPDATE \"user\" " + "SET deleted = true, last_update = now() "
		+ "WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted = false")
public class User extends BasicEntity implements UserDetails {

	private static final long serialVersionUID = -2327408049823234096L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
	@SequenceGenerator(name = "user_generator", sequenceName = "user_seq", allocationSize = 1)
	private Integer id;

	@ColumnTransformer(read = "pgp_sym_decrypt(" + "    username, " + "    current_setting('encrypt.key')"
			+ ")", write = "pgp_sym_encrypt( " + "    ?, " + "    current_setting('encrypt.key')" + ") ")
	@Column(columnDefinition = "bytea", unique = true)
	@Length(min = 5)
	private String username;

	@ColumnTransformer(read = "pgp_sym_decrypt(" + "    secret, " + "    current_setting('encrypt.key')"
			+ ")", write = "pgp_sym_encrypt( " + "    ?, " + "    current_setting('encrypt.key')" + ") ")
	@Column(columnDefinition = "bytea")
	private String secret;

	@ColumnTransformer(read = "pgp_sym_decrypt(" + "    name, " + "    current_setting('encrypt.key')"
			+ ")", write = "pgp_sym_encrypt( " + "    ?, " + "    current_setting('encrypt.key')" + ") ")
	@Column(columnDefinition = "bytea")
	private String name;

	@ColumnTransformer(read = "pgp_sym_decrypt(" + "    email, " + "    current_setting('encrypt.key')"
			+ ")", write = "pgp_sym_encrypt( " + "    ?, " + "    current_setting('encrypt.key')" + ") ")
	@Column(columnDefinition = "bytea")
	private String email;

	@ColumnTransformer(read = "pgp_sym_decrypt(" + "    password, " + "    current_setting('encrypt.key')"
			+ ")", write = "pgp_sym_encrypt( " + "    ?, " + "    current_setting('encrypt.key')" + ") ")
	@Column(columnDefinition = "bytea")
	@Length(min = 8)
	private String password;

	private String role;

	@Column(columnDefinition = "boolean default false")
	private boolean twoFaEnabled = false;

	@Column(columnDefinition = "boolean default false")
	private boolean active = false;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.asList(Role.valueOf(role));
	}

	@Override
	public boolean isAccountNonExpired() {
		return active;
	}

	@Override
	public boolean isAccountNonLocked() {
		return active;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return active;
	}

	@Override
	public boolean isEnabled() {
		return active;
	}

}
