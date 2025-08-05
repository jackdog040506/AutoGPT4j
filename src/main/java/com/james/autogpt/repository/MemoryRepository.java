package com.james.autogpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.james.autogpt.model.Memory;

public interface MemoryRepository extends JpaRepository<Memory, Long> {
}