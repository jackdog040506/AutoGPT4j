package com.james.autogpt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.james.autogpt.model.OpsTicketsTask;

@Repository
public interface OpsTicketsTaskJpaRepository extends JpaRepository<OpsTicketsTask, String> {

	@Query(value = "select ott.* from ops_tickets_task ott where jsonb_path_exists(CAST(ott.config as jsonb), CAST( ?1 as jsonpath))", nativeQuery = true)
	List<OpsTicketsTask> searchConfig(String query);

}
