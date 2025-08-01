
---

read the concept of this documents(https://java2ai.com/blog/agent-agentic-patterns/) then follow requirement to implement the AIThinkingProcessor class , the AIThinkingProcessor is the minimium unit of large task tree , this unit will doing single cycle of Agentic Loop: “Think → Reason → Plan → Criticize → Act (Command) → Speak”

---

##  Conceptual Flow

### 🔹 Think

* **Goal**: Collect and concentrate sources of truth to prepare for reasoning.
* **Sources**:

  * `Core Prompt`: *(To be manually added)*
  * `Agent Role`: From `EngineExecution.agent.roles`
  * `Tree of Goal`: Traverse `EngineExecution.goal.taskNode.parentTask`
    *(Only nodes with `AgentType.AIThinking`)*

---

###  Reason

* **Goal**: Generate a focused breakdown of the current subgoal.
* **Sources**:

  * `Core Prompt`: *(To be manually added)*
  * `Current Node Goals`: From `EngineExecution.goal.taskNode.goals`
    *(Filtered by `AgentType.AIThinking`)*

---

###  Plan

* **Goal**: Decide on subgoals or the next goal for the current node.
* **Sources**:

  * `Core Prompt`: *(To be manually added)*
  * `Result of Think & Reason`: Possibly from chat memory via `conversationId`
  * `Agent Options`: From `EngineExecution.goal.taskNode.taskNodeMaster.agents`

---

###  Criticize

* **Goal**: Evaluate plans and provide constructive alternatives.
* **Sources**:

  * `RAG Result`: From vector store, filtered by
    `master_id = EngineExecution.goal.taskNode.taskNodeMaster.id`
  * `Core Prompt`: *(To be manually added)*

---

###  Act / Command

* **Goal**: Transform final decisions into actionable goals.
* **Sources**:

  * `Core Prompt`: *(To be manually added)*

---

###  Speak

* **Goal**: Summarize the process and store in vector DB.
* **Vector Store Metadata**:

  * `master_id`: `EngineExecution.goal.taskNode.taskNodeMaster.id`
  * `node priority`: `EngineExecution.goal.taskNode.priority`
* **Sources**:

  * `Core Prompt`: *(To be manually added)*

---
