/*
 * This module contains rules and plans used to provide social evaluations, 
 * like social image, reputation, and references.
 * 
 * @author: Baqueta. 
 */

/**
 * This rule returns all impressions produced by the agent himself about a delegatee.
 */
getMyImpressions(DelegateeActionCode, Impressions) 
:- 	.findall(
		imp(action(Delegatee, DelegateeActionCode), Iteration, Rating)[source(self)], 
		imp(action(Delegatee, DelegateeActionCode), Iteration, Rating)[source(self)], 
		Impressions
	)
.

/**
 * This rule returns all impressions produced by other agents about a delegatee.
 */
getOtherImpressions(DelegateeActionCode, Impressions) 
:- 	.findall(
		imp(action(Delegatee, DelegateeActionCode), Iteration, Rating)[source(Source)], 
		imp(action(Delegatee, DelegateeActionCode), Iteration, Rating)[source(Source)] & Source \== self, 
		Impressions
	)
.

/**
 * This rule returns all agent's references regarding a particular action.
 */
getMyReferences(Me, MyActionCode, References)
:-	.findall(
		imp(action(Me, MyActionCode), Iteration, Rating)[source(Source)],
		imp(action(Me, MyActionCode), Iteration, Rating)[source(Source)] & Source \== self,
		References
	)
.

/**
 * This plan finds the agent's neighbors based on a partner's action code.
 */
+!getNeighbors(PartnerActionCode, Neighbors)
:	
	getMyName(Me)
<-	
	.df_search(assessor(PartnerActionCode), Assessors);
	environments.action_getNeighbors(Me, PartnerActionCode, Assessors, Neighbors);
.

/**
 * This plan merges a list of impressions into a single measure.
 */
+!mergeImpressions(Impressions, MergedValue)
<-	
	environments.action_mergeImpressions(Impressions, MergedValue);
.

/**
 * Plan to manage the agent's memory.
 */
@sm_p1 [atomic]
+imp(action(Delegatee, DelegateeActionCode), Iteration, Rating)[source(Source)]
:	getMyName(Me)
<-	
	environments.action_addImpToMemory(Me, imp(action(Delegatee, DelegateeActionCode), Iteration, Rating, Source));
.