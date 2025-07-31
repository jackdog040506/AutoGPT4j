package com.james.autogpt.model;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class RecordEntityBase {


	@Temporal(TemporalType.TIMESTAMP)
//	@CreatedDate
	@CreationTimestamp
	@Column(columnDefinition = "timestamp(6)")
	private Timestamp dateCreated;

	@CreatedBy
	@Column(columnDefinition = "varchar(255)")
	private String userCreated;

}
