/*
 * This module contains the rules used by the manager.
 * 
 * @author: Baqueta. 
 */

/**
 * This rule returns all delegators.
 */
getDelegators(Delegators) 
:-	.findall(
		Delegator, 
		delegator(Delegator), 
		Delegators
	)
.

/**
 * This rule returns all delegatees.
 */
getDelegatees(Delegatees) 
:-	.findall(
		Delegatee, 
		delegatee(Delegatee), 
		Delegatees
	)
.

/**
 * This rule returns all hybrid agents.
 */
getHybrids(Hybrids) 
:-	.findall(
		Hybrid, 
		hybrid(Hybrid), 
		Hybrids
	)
.

/**
 * This rule returns all agents.
 */
getAgents(Agents) 
:-	getDelegators(Delegators) & 
	getDelegatees(Delegatees) &
	getHybrids(Hybrids) &
	.concat(Delegators, Delegatees, Hybrids, Agents)
.

/**
 * This rule returns all delegation chains sent to the manager.
 */
getChains(Chains)
:-	.findall(
		root(Delegator, RootActionCode, Prospect), 
		status(finished, chain(RootActionCode, Prospect))[source(Delegator)],
		Chains
	)
.

/**
 * This rule returns true if all agents are activated; otherwise, false.
 */
isAllActivated 
:-	getAgents(Agents) &
	.length(Agents, N_agents) &
 	.count(status(agent(activated))[source(_)], N_activated) & 
	N_agents == N_activated
.

/**
 * This rule returns true if all agents are registered; otherwise, false.
 */
isAllRegistered 
:-	getAgents(Agents) &
	.length(Agents, N_agents) &
 	.count(status(agent(registered))[source(_)], N_registered) & 
	N_agents == N_registered
.

/**
 * This rule returns true if all delegators finished their tasks.
 */
isFinished
:-	getDelegators(Delegators) &
	.length(Delegators, N_delegators) &
 	.count(status(finished,_)[source(_)], N_answers) & 
 	N_delegators == N_answers
.

/**
 * This rule returns true if all agents are ready for the next iteration.
 */
isReseted
:-	getAgents(Agents) &
	.length(Agents, N_agents) &
 	.count(status(removed)[source(_)], N_answers) & 
 	N_agents == N_answers
.

/**
 * This rule returns true if a delegator has already processed his delegation chain.
 */
isProcessed
:-	processor(Delegator, RootActionCode) &
	processed(RootActionCode)[source(Delegator)] 
.