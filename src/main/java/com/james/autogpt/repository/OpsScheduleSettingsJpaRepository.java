package com.james.autogpt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.james.autogpt.model.OpsScheduleSettings;
import com.james.autogpt.model.OpsScheduleSettingsPk;

@Repository
public interface OpsScheduleSettingsJpaRepository extends JpaRepository<OpsScheduleSettings, OpsScheduleSettingsPk> {

	List<OpsScheduleSettings> findByIdGroupId(String groupId);

	List<OpsScheduleSettings> findByOrderByIdScheduleIdAscIdGroupIdAsc();

	List<OpsScheduleSettings> findByIdGroupIdOrderByIdScheduleIdAscIdGroupIdAsc(String groupId);
}
