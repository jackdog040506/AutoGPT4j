# AutoGPT4J - AI Agent Engine

A Java-based AI agent system implementing Graph of Thought reasoning paradigm for complex problem solving.

## Overview

This project implements an advanced AI agent execution engine based on the Graph of Thought (GoT) research paper. The engine supports sophisticated reasoning patterns including:

- **AIThinking**: Default reasoning process for basic task analysis
- **AIGot**: Advanced Graph of Thought reasoning with:
  - **Refining**: Re-process tasks with higher priority
  - **Backtracking**: No-go analysis and alternative path exploration  
  - **Aggregating**: Synthesis of multiple reasoning paths (placeholder)
  - **Branching**: Create child tasks for parallel exploration

## Architecture

### Core Components

#### ExecutorEngine
- Priority-based queue processing system
- Observer pattern for backtracking
- Dynamic priority reordering
- Concurrent execution management

#### EngineExecutionFactory
- Factory pattern for AgentType-specific processing
- Pluggable processor architecture
- Extensible for new reasoning types

#### Models
- **TaskNode**: Graph nodes representing tasks with hierarchy
- **EngineGoal**: Goals spawned during agent reasoning
- **EngineExecution**: Processing units that execute goals
- **Agent**: Different agent types for specialized processing

### Processing Flow

1. **Task Creation**: TaskNode with initial AIThinking goal
2. **Queue Processing**: Priority-based execution scheduling
3. **Agent Processing**: Type-specific reasoning logic
4. **Result Handling**: Success, failure, refinement, or branching
5. **Observer Notification**: Backtracking and monitoring

## API Endpoints

### Engine Management
- `GET /api/engine/status` - Get engine status
- `POST /api/engine/restart` - Restart the engine
- `POST /api/engine/queue/{executionId}` - Queue specific execution
- `POST /api/engine/queue/all` - Queue all pending executions

### Task Management  
- `POST /api/tasks/master-node-ai-planner` - Create AIPlanner task
- `POST /api/tasks/master-node` - Create generic task

## Configuration

### Database
PostgreSQL with JPA/Hibernate for entity persistence.

### AI Integration
Configured for OpenAI API integration (configurable in application.properties).

### Monitoring
Comprehensive logging and status monitoring via REST endpoints.

## Getting Started

### Prerequisites
- Java 17+
- PostgreSQL 12+
- Maven 3.6+

### Setup
1. Configure database connection in `application.properties`
2. Set OpenAI API key (optional for basic functionality)
3. Run with `mvn spring-boot:run`

### Creating Your First Task
```bash
curl -X POST http://localhost:18787/api/tasks/master-node-ai-planner \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Analyze Problem",
    "masterPrompt": "System analysis task",
    "rootPrompt": "Analyze the given problem and provide solution approaches"
  }'
```

### Monitoring Engine
```bash
curl http://localhost:18787/api/engine/status
```

## Advanced Features

### Graph of Thought Reasoning
The system implements sophisticated reasoning patterns:

- **Refining**: Automatically improves task processing by re-queueing with higher priority
- **Backtracking**: Analyzes failed approaches and explores alternatives
- **Branching**: Creates parallel reasoning paths for comprehensive exploration

### Priority Management
Dynamic priority adjustment based on:
- Task complexity and hierarchy
- Refinement operations (increase priority)
- Branching operations (inherit/adjust priority)
- Backtracking insights

### Observer Pattern
Extensible monitoring system for:
- Execution lifecycle tracking
- Automatic backtracking triggers
- Performance monitoring
- Custom analytics

## Extension Points

### Custom Processors
Implement `ExecutionProcessor` for new agent types:

```java
@Component
public class CustomProcessor implements ExecutionProcessor {
    public ExecutionResult process(EngineExecution execution) {
        // Custom processing logic
    }
    
    public AgentType getAgentType() {
        return AgentType.CUSTOM;
    }
}
```

### Custom Observers
Implement `ExecutionObserver` for monitoring:

```java
@Component  
public class CustomObserver implements ExecutionObserver {
    public void onExecutionEvent(ExecutionEvent event, EngineExecution execution) {
        // Custom monitoring logic
    }
}
```

## Research Foundation

Based on "Graph of Thoughts: Solving Elaborate Problems with Large Language Models" (arXiv:2308.09687), this implementation provides a practical framework for advanced AI reasoning patterns in enterprise Java applications.

## License

[Add your license information here] 