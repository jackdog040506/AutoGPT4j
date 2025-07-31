package com.james.autogpt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "summaries")
public class Summary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long summaryId;
    private String agentId;
    private String type;
    @Lob
    private String content;
    private String tags;
    private LocalDateTime createdAt;

    // Getters and setters
    // ...
} 