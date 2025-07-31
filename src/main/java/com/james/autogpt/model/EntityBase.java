package com.james.autogpt.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
//@EntityListeners(AuditingEntityListener.class)
public class EntityBase {


	@Id
	@UuidGenerator
	private String id;
	
	@LastModifiedDate
	private LocalDateTime dateModified;

	@CreatedDate
	private LocalDateTime dateCreated;

//	@Version
//	private int version;

//	@Column
//	@CreatedBy
//	private String userCreated;

//	@Column
//	@LastModifiedBy
//	private String userModified;

//	@Column
//	private String isDelete = "N";

}
