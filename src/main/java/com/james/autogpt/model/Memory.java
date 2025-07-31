package com.james.autogpt.model;

import jakarta.persistence.*;

@Entity
@Table(name = "memory")
public class Memory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String agentId;
    private String source;
    @Lob
    private String content;
    private String embedding; // For future vector DB integration
    private String metadata;

    // Getters and setters
    // ...
} 