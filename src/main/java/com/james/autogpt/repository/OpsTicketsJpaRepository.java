package com.james.autogpt.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.james.autogpt.model.OpsTickets;

@Repository
public interface OpsTicketsJpaRepository extends JpaRepository<OpsTickets, String> {

	// Use EntityGraph instead of manual join fetch for better performance
	@EntityGraph("OpsTickets.withTasks")
	Page<OpsTickets> findByTicketsTemplateId(String ticketsTemplateId, Pageable pageable);

	@Query("select ot from OpsTickets ot where ot.ticketsTemplateId = ?2 "//
			+ "AND (?1 is null or ?1 ='' or "//
			+ "LOWER(ot.name) like concat('%',LOWER(?1),'%') or "//
			+ "LOWER(ot.id) like concat('%',LOWER(?1),'%')) ")
	Page<OpsTickets> findByTicketsTemplateId(String keyword, String ticketsTemplateId, Pageable pageable);

	// Use EntityGraph instead of manual join fetch
	@EntityGraph("OpsTickets.withTasks")
	OpsTickets getReferenceById(String id);

	// Use EntityGraph instead of manual join fetch  
	@EntityGraph("OpsTickets.withTasks")
	Optional<OpsTickets> findById(String id);
	
	// Additional EntityGraph optimized methods
	@EntityGraph("OpsTickets.withTasks")
	Page<OpsTickets> findAll(Pageable pageable);
	
	@EntityGraph("OpsTickets.withTasks")
	Optional<OpsTickets> findByIdAndTicketsTemplateId(String id, String ticketsTemplateId);

}
