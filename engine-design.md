read the concept of this documents(<https://arxiv.org/pdf/2308.09687>) then follow requirement to development an executor engine  

based concept of model design:

- The TaskNode model will be the Node of graph of tought.
- The EngineGoal model is present as goal, data is spawn during agent reasoning by AgentType.AIAgent.
- The EngineExecution model is present as proccessing unit of EngineGoal that execute the alternation of TaskNode or tool command of java.
- conversationId in TaskNode is the key of the chat memory that contains intire proccessing of current TaskNode.
- TaskNode.priority is present about layer of task in the hierarchy

requirements:
 1.design an executor engine for proccessing:
    - engine is basiclly an queue with serviceexecutor, considering that have aggregate and Backtracking(observer) the queue may have capability to re order the sequance of TaskNode's priority
    - design EngineExecutionFactory that proccessing that input EngineExecution and cadidate the implement by EngineExecution.AgentType 
    - AgentType design as define core proccessing of different purpose:
        - AIThinking: when TaskNode spawn this is default goal  
        - AIGot: if AIThinking goal decided to Refining, Backtracking, Aggregating, Branching of current TaskNode, then implement containing :
            - Refining: re-spawn a new AIThinking goal on TaskNode then, queue back to engine queue and resort the priority of tasks
            - Backtracking: implement the no go summary , and maybe Refining proximity associate with queue 
            - Aggregating: make empty implement for now 
            - Branching: implement the create new TaskNode into child of current node ,new TaskNode will have default AIThinking goal of branched goal prompt , queue the new TaskNode to engine queue
