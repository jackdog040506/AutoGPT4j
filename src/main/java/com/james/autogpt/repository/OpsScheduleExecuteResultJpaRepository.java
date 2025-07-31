package com.james.autogpt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.james.autogpt.model.OpsScheduleExecuteResult;

@Repository
public interface OpsScheduleExecuteResultJpaRepository extends JpaRepository<OpsScheduleExecuteResult, String> {

	OpsScheduleExecuteResult findFirstByGroupIdAndScheduleIdOrderByDateCreatedDesc(String groupId, String scheduleId);

	Page<OpsScheduleExecuteResult> findByGroupIdAndScheduleId(String groupId, String scheduleId, Pageable pageable);

}
