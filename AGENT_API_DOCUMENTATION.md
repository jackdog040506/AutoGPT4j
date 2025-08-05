# Agent CRUD API Documentation

## Overview

This document describes the REST API endpoints for managing Agent entities in the AutoGPT4j application.

## Base URL

```
/api/agents
```

## Data Models

### CreateAgentRequest

```json
{
  "agentId": "string (required, max 255 chars)",
  "roles": "string (optional, max 1000 chars, comma-separated)",
  "focus": "string (optional, max 500 chars)",
  "personality": "string (optional, max 1000 chars)",
  "agentType": "enum (required: TaskExecutor, AIAgent, AIObserver, AIPlanner, AIThinking, AIGot)"
}
```

### UpdateAgentRequest

```json
{
  "roles": "string (optional, max 1000 chars)",
  "focus": "string (optional, max 500 chars)",
  "personality": "string (optional, max 1000 chars)",
  "agentType": "enum (optional)"
}
```

### AgentResponse

```json
{
  "agentId": "string",
  "roles": "string",
  "focus": "string",
  "personality": "string",
  "agentType": "enum"
}
```

## API Endpoints

### 1. Create Agent

**POST** `/api/agents`

Creates a new agent with the provided details.

**Request Body:** `CreateAgentRequest`

**Response:**

- `200 OK`: Agent created successfully
- `400 Bad Request`: Agent ID already exists or validation error
- `500 Internal Server Error`: Server error

**Example:**

```bash
curl -X POST http://localhost:8080/api/agents \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": "task-executor-001",
    "roles": "Task execution, Process management",
    "focus": "Efficient task completion",
    "personality": "Systematic and methodical",
    "agentType": "TaskExecutor"
  }'
```

### 2. Get Agent by ID

**GET** `/api/agents/{agentId}`

Retrieves a specific agent by its ID.

**Path Parameters:**

- `agentId`: The unique identifier of the agent

**Response:**

- `200 OK`: Agent found and returned
- `404 Not Found`: Agent not found
- `500 Internal Server Error`: Server error

**Example:**

```bash
curl -X GET http://localhost:8080/api/agents/task-executor-001
```

### 3. Get All Agents

**GET** `/api/agents`

Retrieves all agents in the system.

**Response:**

- `200 OK`: List of all agents
- `500 Internal Server Error`: Server error

**Example:**

```bash
curl -X GET http://localhost:8080/api/agents
```

### 4. Get All Agent Types

**GET** `/api/agents/types`

Retrieves all available agent types (enum values) for frontend mapping.

**Response:**

- `200 OK`: Array of all available agent types
- `500 Internal Server Error`: Server error

**Example:**

```bash
curl -X GET http://localhost:8080/api/agents/types
```

**Response Example:**

```json
{
  "code": 200,
  "message": "OK",
  "data": [
    "TaskExecutor",
    "AIAgent",
    "AIObserver",
    "AIPlanner",
    "AIThinking",
    "AIGot"
  ],
  "tid": "transaction-id"
}
```

### 5. Get Agents by Type

**GET** `/api/agents/type/{agentType}`

Retrieves all agents of a specific type.

**Path Parameters:**

- `agentType`: The agent type filter (TaskExecutor, AIAgent, AIObserver, AIPlanner, AIThinking, AIGot)

**Response:**

- `200 OK`: List of agents of the specified type
- `500 Internal Server Error`: Server error

**Example:**

```bash
curl -X GET http://localhost:8080/api/agents/type/TaskExecutor
```

### 6. Update Agent

**PUT** `/api/agents/{agentId}`

Updates an existing agent with new information.

**Path Parameters:**

- `agentId`: The unique identifier of the agent to update

**Request Body:** `UpdateAgentRequest`

**Response:**

- `200 OK`: Agent updated successfully
- `404 Not Found`: Agent not found
- `500 Internal Server Error`: Server error

**Example:**

```bash
curl -X PUT http://localhost:8080/api/agents/task-executor-001 \
  -H "Content-Type: application/json" \
  -d '{
    "focus": "Enhanced task completion with AI assistance",
    "personality": "Adaptive and intelligent"
  }'
```

### 7. Delete Agent

**DELETE** `/api/agents/{agentId}`

Deletes an agent from the system.

**Path Parameters:**

- `agentId`: The unique identifier of the agent to delete

**Response:**

- `200 OK`: Agent deleted successfully
- `404 Not Found`: Agent not found
- `500 Internal Server Error`: Server error

**Example:**

```bash
curl -X DELETE http://localhost:8080/api/agents/task-executor-001
```

## Response Format

All API responses follow the standard `Result<T>` format:

```json
{
  "code": 200,
  "message": "OK",
  "data": {
    // Response data here
  },
  "tid": "transaction-id"
}
```

## Error Handling

The API uses standard HTTP status codes and provides detailed error messages:

- **400 Bad Request**: Validation errors or business logic violations
- **404 Not Found**: Requested resource not found
- **500 Internal Server Error**: Unexpected server errors

## Validation Rules

- `agentId`: Required, maximum 255 characters, must be unique
- `roles`: Optional, maximum 1000 characters
- `focus`: Optional, maximum 500 characters
- `personality`: Optional, maximum 1000 characters
- `agentType`: Required for creation, must be a valid enum value

## Agent Types

Available agent types:

- `TaskExecutor`: Executes specific tasks
- `AIAgent`: General AI agent
- `AIObserver`: Observes and monitors
- `AIPlanner`: Plans and strategizes
- `AIThinking`: Handles thinking and reasoning
- `AIGot`: Handles refining, backtracking, aggregating, and branching
