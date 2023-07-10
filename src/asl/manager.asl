/**
 * This agent creates and manages other agents.
 * 
 * @author: Baqueta.
 */

/**
 * This plan creates a new delegator.
 */ 
+delegator(Name): debug(Status)
<-	
	.create_agent(Name, "agent.asl");
	.send(Name, tell, debug(Status));
	.print("A new delegator was created, his name is: ", Name);
.

/**
 * This plan creates a new delegatee.
 */ 
+delegatee(Name): debug(Status)
<-	
	.create_agent(Name, "agent.asl");
	.send(Name, tell, debug(Status));
	.print("A new delegatee was created, his name is: ", Name);
	
.

/**
 * This plan creates a new hybrid agent.
 */ 
+hybrid(Name): debug(Status)
<-	
	.create_agent(Name, "agent.asl");
	.send(Name, tell, debug(Status));
	.print("A new hybrid agent was created, his name is: ", Name);
.

/**
 * This plan starts the delegations.
 */ 
+start_delegations
:	
	getAgents(Agents)
<-	
	+iteration(1);
	.print("Waiting for the agents to be ready ...");
 	.wait(isAllActivated);
 	.abolish(status(agent(activated))[_]);
 	!setAssessors(Agents);
 	.wait(isAllRegistered);
 	.abolish(status(agent(registered))[_]);
	.print("STARTING THE DELEGATIONS ...");
	!nextIteration;
.

/**
 * These plans define which agents will assess the others.
 */
+!setAssessors([Agent|T])
<-	
	.send(Agent, achieve, makeRegister);
	!setAssessors(T);
.
+!setAssessors([]).

/**
 * This plan controls the loop of execution.
 */
+!nextIteration
:	
	getAgents(Agents) &
	getDelegators(Delegators) & 
	iteration(N_iterations) & 
	max(Max) 
<-	
	if (N_iterations <= Max) {
		.print("[ITERATION]: ", N_iterations);
		.send(Delegators, achieve, nextTask);
		.wait(isFinished);
		!generateResults;
		.send(Agents, achieve, reset);
		.wait(isReseted);
		.abolish(status(finished,_)[_]);
		.abolish(status(removed)[_]);
		!saveReport;
		environments.action_nextEnvironmentState;
		!incIteration;
		!nextIteration;
	} 
	else {
		!finishTasks;
	}
.

/**
 * These plans are used to process the delegation chains.
 */
+!generateResults
: 
	getChains(Chains)
<-	
	!chainProcessing(Chains);
	.abolish(processor(_,_)[_]);
	.abolish(processed(_)[_]);
.
+!chainProcessing([root(Delegator, RootActionCode, Prospect)|T])
<-	
	-+processor(Delegator, RootActionCode);
	.send(Delegator, achieve, processing(RootActionCode, Prospect));
	.wait(isProcessed);
	!chainProcessing(T);
.
+!chainProcessing([]).

/**
 * This plan saves the results obtained in the current iteration.
 */
@m_p1 [atomic]
+!saveReport
<-	
	environments.action_saveReport;
.

/**
 * This plan increments the iteration counter.
 */
+!incIteration
: 
	iteration(N_iterations)
<-	
	new(iteration);
	Iterations = N_iterations + 1;
	-+iteration(Iterations);
.

/**
 * This plan ends the execution loop.
 */
+!finishTasks
<-	
	.print("EXPERIMENT IS OVER!");
	experiment(finished);
.

{ include("src/asl/manager_rules.asl") }