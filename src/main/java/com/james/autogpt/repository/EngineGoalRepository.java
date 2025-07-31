package com.james.autogpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.james.autogpt.model.EngineGoal;

public interface EngineGoalRepository extends JpaRepository<EngineGoal, String> {
} 