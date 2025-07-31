package com.james.autogpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.james.autogpt.model.Summary;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
} 