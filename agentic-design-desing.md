read the concept of this documents(https://java2ai.com/blog/agent-agentic-patterns/) then follow requirement to implement the AIThinkingProcessor class , the AIThinkingProcessor is the minimium unit of large task tree , this unit will doing single cycle of Agentic Loop: “Think → Reason → Plan → Criticize → Act (Command) → Speak”


concept of each step:
**Think**: in think step will given multiple source for clearing sight and provide concentrate information for Reason step
**Reason**: in think mode will given very focus goal about this step of current node where it placed
**Plan**: collect result from Think and Reason step and make decision about Generate subgoals or next goal of current node
**Criticize**: Evaluate outcomes or suggest alternative strategies
**Act/Command**: convert final decisions to goal data 
**Speak**: summary result of this runtime and write into vector store with metadata:
   - master_id: EngineExecution.goal.taskNode.taskNodeMaster.id
   - node priority: EngineExecution.goal.taskNode.priority

step prompt source preparation:
**Think**: 
    - core prompt:make space for core prompt , i will manually added for each step
    - Agent role: data from EngineExecution.agent.roles
    - Tree of goal: record all goals from master node to current nodes, traverse EngineExecution.goal.taskNode.parentTask and extract every goal with AgentType.AIThinking
**Reason**: 
    - core prompt:make space for core prompt , i will manually added for each step
    - Current Node goals: data from EngineExecution.goal.taskNode.goals filter by AgentType.AIThinking
**Plan**: 
    - core prompt:make space for core prompt , i will manually added for each step
    - result of Think and Reason : this maybe came from chatmemory by conversationId
    - agent options for goal generation: EngineExecution.goal.taskNode.taskNodeMaster.agents
**Criticize**: 
    - RAG from vector stroe with filter master_id: EngineExecution.goal.taskNode.taskNodeMaster.id
    - core prompt:make space for core prompt , i will manually added for each step
**Act/Command**: 
    - core prompt:make space for core prompt , i will manually added for each step
**Speak**
    - core prompt:make space for core prompt , i will manually added for each step




| Phase           | Purpose                                                   | Backed by                     |
| --------------- | --------------------------------------------------------- | ----------------------------- |
| **Think**       | Internal goal reflection. What is the task? What's known? | EngineExecution.config.prompt |
| **Reason**      | Logical reasoning or breakdown                            | LLM reasoning layer           |
| **Plan**        | Generate subgoals or steps                                | Fork planner                  |
| **Criticize**   | Evaluate outcomes or suggest alternative strategies       | Self-reflective prompt        |
| **Act/Command** | Execute a step (API, scrape, write, etc.)                 | Command router                |
| **Speak**       | Report progress or result                                 | Broadcast status              |

