# Test Payloads for AutoGPT4j API

## Create Agent Endpoint

### 1. Minimal Required Payload
```json
{
  "agentId": "test-agent-001",
  "agentType": "TaskExecutor"
}
```

### 2. Complete Payload with All Fields
```json
{
  "agentId": "ai-assistant-001",
  "roles": "code reviewer, bug fixer, documentation writer",
  "focus": "Java development and code quality improvement",
  "personality": "Thorough, detail-oriented, and helpful. Always provides clear explanations and follows best practices.",
  "agentType": "AIThinking"
}
```

### 3. TaskExecutor Agent Example
```json
{
  "agentId": "task-executor-001",
  "roles": "task execution, workflow management",
  "focus": "Executing assigned tasks efficiently and reporting results",
  "personality": "Efficient, reliable, and focused on task completion",
  "agentType": "TaskExecutor"
}
```

### 4. AIObserver Agent Example
```json
{
  "agentId": "observer-001",
  "roles": "monitoring, analysis, reporting",
  "focus": "Observing system behavior and providing insights",
  "personality": "Analytical, observant, and detail-focused",
  "agentType": "AIObserver"
}
```

### 5. AIGot Agent Example
```json
{
  "agentId": "ai-got-001",
  "roles": "refining, backtracking, aggregating, branching",
  "focus": "Handling complex decision-making and process optimization",
  "personality": "Strategic, adaptive, and capable of complex reasoning",
  "agentType": "AIGot"
}
```

## Create AI-Planner Task Endpoint

### 1. Minimal Required Payload
```json
{
  "name": "Test AI-Planner Task",
  "majorAgentId": "ai-thinking-001",
  "agentIds": ["ai-thinking-001", "task-executor-001", "observer-001"]
}
```

### 2. Complete Payload with All Fields
```json
{
  "name": "Complex Development Task",
  "masterPrompt": "You are a development team coordinator. Your task is to manage the development of a new feature.",
  "rootPrompt": "Analyze the requirements and create a development plan with clear milestones.",
  "majorAgentId": "ai-thinking-001",
  "agentIds": [
    "ai-thinking-001",
    "task-executor-001", 
    "observer-001",
    "ai-got-001"
  ]
}
```

### 3. Code Review Task Example
```json
{
  "name": "Code Review and Refactoring Task",
  "masterPrompt": "You are a senior developer responsible for code review and refactoring.",
  "rootPrompt": "Review the provided code, identify issues, and suggest improvements.",
  "majorAgentId": "ai-thinking-001",
  "agentIds": [
    "ai-thinking-001",
    "task-executor-001"
  ]
}
```

### 4. System Monitoring Task Example
```json
{
  "name": "System Performance Monitoring",
  "masterPrompt": "You are a system administrator monitoring application performance.",
  "rootPrompt": "Monitor system metrics and alert on any performance issues.",
  "majorAgentId": "observer-001",
  "agentIds": [
    "observer-001",
    "ai-thinking-001"
  ]
}
```

## Field Descriptions

### Create Agent Request:
- **agentId** (required): Unique identifier for the agent (max 255 characters)
- **roles** (optional): Comma-separated list of roles (max 1000 characters)
- **focus** (optional): Primary focus area of the agent (max 500 characters)
- **personality** (optional): Description of agent's personality traits (max 1000 characters)
- **agentType** (required): One of: `TaskExecutor`, `AIObserver`, `AIThinking`, `AIGot`

### Create AI-Planner Task Request:
- **name** (required): Name of the task (max 255 characters)
- **masterPrompt** (optional): Master prompt for the task
- **rootPrompt** (optional): Root prompt for the task
- **majorAgentId** (required): ID of the major agent that will handle the primary execution
- **agentIds** (required): List of agent IDs that will be associated with the TaskNodeMaster

## Testing with cURL

### Create Agent:
```bash
curl -X POST http://localhost:8080/api/agents \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": "test-agent-001",
    "roles": "code reviewer, bug fixer",
    "focus": "Java development",
    "personality": "Thorough and helpful",
    "agentType": "AIThinking"
  }'
```

### Create AI-Planner Task:
```bash
curl -X POST http://localhost:8080/api/tasks/ai-planner \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test AI-Planner Task",
    "masterPrompt": "You are a development coordinator.",
    "rootPrompt": "Create a development plan.",
    "majorAgentId": "ai-thinking-001",
    "agentIds": ["ai-thinking-001", "task-executor-001", "observer-001"]
  }'
``` 