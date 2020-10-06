package it.izzonline.securityoauthservice.model;

import java.time.LocalDateTime;

import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class BasicEntity {

	private boolean deleted;

	@LastModifiedBy
	private String modifiedBy;

	@LastModifiedDate
	private LocalDateTime lastUpdate;

	@CreatedBy
	private String createdBy;

	@CreationTimestamp
	private LocalDateTime creationDate;

}
