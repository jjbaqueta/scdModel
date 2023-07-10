/*
 * This module contains plans to ensure the agents' correct 
 * operation before a new iteration starts.
 * 
 * @author: Baqueta. 
 */

/**
 * This plan resets the agent's mind, removing 
 * unnecessary beliefs before a new iteration.
 */
+!reset
<-	
	-+requestId(1);
	.send(manager, tell, status(removed));
.

@agAux_p1 [atomic]
+!countBid(DelegatorActionCode, DelegateeActionCode)
<-
	environments.action_countBid(DelegatorActionCode, DelegateeActionCode);
.