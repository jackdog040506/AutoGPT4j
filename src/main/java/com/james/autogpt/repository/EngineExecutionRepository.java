package com.james.autogpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.james.autogpt.model.EngineExecution;

public interface EngineExecutionRepository extends JpaRepository<EngineExecution, String> {
} 