package it.izzonline.securityoauthservice.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@Entity
@EqualsAndHashCode(of = { "name" })
@NoArgsConstructor
@Table(schema = "\"security\"")
public class Role implements Serializable {

	private static final long serialVersionUID = 4833183317359581882L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_generator")
	@SequenceGenerator(name = "role_generator", sequenceName = "role_seq", allocationSize = 1)
	private Integer id;

	@NotNull
	@Enumerated(EnumType.STRING)
	private RoleEnum name;

}