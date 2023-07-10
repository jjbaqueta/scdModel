/*
 * An agent can play any of the following roles:
 * - Delegator: the agent is the root of a delegation chain and can only delegate tasks.
 * - Delegatee: the agent is a leaf of a delegation chain and can only execute tasks. 
 * - Hybrid: the agent is an inner node of a delegation chain and can delegate and execute tasks.
 * 
 * @author: Baqueta. 
 */

!start.

/**
 * This plan initializes the agent's beliefs.
 */
+!start: getMyName(Me)
<-	
	+requestId(1);
	environments.action_loadBeliefs(Me);
 	.send(manager, tell, status(agent(activated)));
.

/**
 * These plans register the agents who assess the others.
 */
+!makeRegister: getMyName(Me)
<- 	
	environments.action_getAgentActions(Me, Actions);
	!register(Actions);
	.send(manager, tell, status(agent(registered)));
.
+!register([ActionCode|T]) 
<- 
	.df_register(assessor(ActionCode));
	!register(T);
.
+!register([]).

/**
 * This plan generates a new task.
 */ 
+!nextTask: getMyName(Me) & debug(Status)
<-	
	environments.action_getNextTask(Me, task(MyActionCode));
	.abolish(task(_)[source(self)]);
	
	if (Status == true) {
		.print("[START] executing action: ", MyActionCode);
	}
//	!delegate(MyActionCode, chain([], 0), none, none, none);
	!delegate(MyActionCode, chain([], 1), none, none, none);
//	!delegate(MyActionCode, chain([], 0.5), none, none, none);
.

/**
 * This plan starts a new delegation.
 * A delegator can delegate a task to himself or another agent.
 * A self-delegation implies the execution of the task by the delegator himself.
 */
+!delegate(
	MyActionCode, 
	chain(Chain, ASR), 
	MyDelegator, 
	MyDelegatorActionCode, 
	MyDelegatorRequest
): 	
	getMyName(Me) & 
	debug(Status)
<-	
	environments.action_getPartners(MyActionCode, Partners);
	if (Status == true) {
		.print("[PARTNERS] for task (", MyActionCode , "): ", Partners);
	}
	!createRequest(
		MyActionCode, 
		MyDelegator, 
		MyDelegatorActionCode, 
		MyDelegatorRequest, 
		Partners, 
		MyRequestId
	);
	.concat(Chain, [Me], C);
	!getQuotes(
		MyActionCode, 
		MyRequestId, 
		chain(C, ASR), 
		Partners
	);
	.wait(checkQuotations(MyActionCode, MyRequestId));
	!chooseDelegatees(MyActionCode, MyRequestId);
.
/**
 * This plan creates a new task request.
 * Each delegation is associated with an exclusive task request id.
 */
@ag_p1 [atomic]
+!createRequest(
	MyActionCode, 
	MyDelegator, 
	MyDelegatorActionCode, 
	MyDelegatorRequest, 
	Partners, 
	MyRequestId
):	
	requestId(MyRequestId)
<-	
	+request(
		id(MyRequestId), 
		client(MyDelegator, MyDelegatorActionCode, MyDelegatorRequest), 
		task(MyActionCode), 
		partners(Partners)
	);
	RequestId = MyRequestId + 1;
	-+requestId(RequestId);
.

/**
 * In these plans, a delegator requests quotes for a task.
 */
+!getQuotes(
	DelegatorActionCode, 
	DelegatorRequest, 
	chain(Chain, ASR), 
	[partner(Partner, PartnerActionCode)|T]
): 
	getMyName(Me)
<-	
	environments.action_computeASR(DelegatorActionCode, PartnerActionCode, ASR, NewASR);
	.send(Partner, achieve, quoteFor(DelegatorActionCode, DelegatorRequest, 
			chain(Chain, NewASR), PartnerActionCode)
	);
	!getQuotes(DelegatorActionCode, DelegatorRequest, chain(Chain, ASR), T);
.
+!getQuotes(_,_,_,[]).

/**
 * In this plan, a partner checks if he can perform the task by himself. 
 * If the partner needs someone to complete the task, he must perform a new delegation.
 */
+!quoteFor(
	DelegatorActionCode, 
	DelegatorRequest, 
	chain(Chain, ASR), 
	MyActionCode
)[source(Delegator)]
:	
	getMyName(Me) & 
	getMyReferences(Me, MyActionCode, References) &
	debug(Status)
<-		
	if (Delegator \== self) {
		!delegate(
			MyActionCode, 
			chain(Chain, ASR), 
			Delegator, 
			DelegatorActionCode, 
			DelegatorRequest
		);
	}
	else {
		!mergeImpressions(References, ReferenceMeasure)
		environments.action_quoteFor(MyActionCode, Promise);
		environments.action_executeTask(MyActionCode, Promise, Outcome);
		
		.length(Chain, ChainSize);
		
		if (Status == true) {
			.print("[OFFER] for task (", MyActionCode , "): Promise: ", Promise, "; Outcome: ", Outcome);
		}
		
		!countBid(DelegatorActionCode, MyActionCode);
		
		if (Outcome == []){
			if (Delegator == self){
				.send(Delegator, tell,
					quotation(
						requestId(DelegatorRequest), 
						task(DelegatorActionCode), 
						//asr(ASR / ChainSize),
						asr(ASR), 
						proposal(
							action(self, MyActionCode), 
							promise(Promise), 
							outcome(Outcome),
							failure(1),
							ref(ReferenceMeasure)
						), [])
				);	
			} 
			else {
				.send(Delegator, tell, 
					quotation(
						requestId(DelegatorRequest), 
						task(DelegatorActionCode), 
						//asr(ASR / ChainSize),
						asr(ASR), 
						proposal(
							action(Me, MyActionCode), 
							promise(Promise), 
							outcome(Outcome),
							failure(1),
							ref(ReferenceMeasure)
						), [])
				);
			}
		} 
		else {
			if (Delegator == self){
				.send(Delegator, tell, 
					quotation(
						requestId(DelegatorRequest), 
						task(DelegatorActionCode), 
						//asr(ASR / ChainSize),
						asr(ASR), 
						proposal(
							action(self, MyActionCode), 
							promise(Promise), 
							outcome(Outcome),
							failure(0),
							ref(ReferenceMeasure)
						), [])
				);	
			} 
			else {
				.send(Delegator, tell, 
					quotation(
						requestId(DelegatorRequest), 
						task(DelegatorActionCode), 
						//asr(ASR / ChainSize),
						asr(ASR), 
						proposal(
							action(Me, MyActionCode), 
							promise(Promise), 
							outcome(Outcome),
							failure(0),
							ref(ReferenceMeasure)
						), [])
				);
			}
		}
	}
.

/**
 * In these plans, the delegator selects his delegatees.
 */
+!chooseDelegatees(MyActionCode, MyRequestId) 
:	
	request(
		id(MyRequestId), 
		client(MyDelegator, MyDelegatorActionCode, MyDelegatorRequest), 
		task(MyActionCode), 
		partners(Partners)
	) &	
	getProposals(task(MyActionCode), MyRequestId, Proposals) & 
	getMyName(Me)
<-	
	!getSocialMetrics(Partners, Assessments);
	!getDelegatees(MyActionCode, Proposals, Assessments, Prospect, ASR);
	!notifyDelegator(
		MyActionCode, 
		MyRequestId, 
		MyDelegator, 
		MyDelegatorActionCode, 
		MyDelegatorRequest,
		ASR,
		Prospect
	);
	.abolish(request(id(MyRequestId),_,_,_));
	.abolish(quotation(requestId(MyRequestId),_,_,_,_));
.
+!getDelegatees(MyActionCode, Proposals, Assessments, Prospect, ASR): debug(Status)
<-	
	if (Status == true) {
		environments.action_getDelegateesDebug(MyActionCode, Proposals, Assessments, product(Prospect, ASR));
	}
	else {
		environments.action_getDelegatees(MyActionCode, Proposals, Assessments, product(Prospect, ASR));	
	}
.

/**
 * In these plans, the delegator computes his delegatees' social image and reputation.
 */
+!getSocialMetrics([partner(Partner, PartnerActionCode)|T], Assessments)
:	getMyImpressions(PartnerActionCode, MyImpressions) & 
	getOtherImpressions(PartnerActionCode, OtherImpressions) 
<-	
	!getSocialMetrics(T, X);
	!mergeImpressions(MyImpressions, ImageMeasure);
	!mergeImpressions(OtherImpressions, ReputationMeasure);
	.concat(X, [assessment(PartnerActionCode, img(ImageMeasure), rep(ReputationMeasure))], Assessments);
.
+!getSocialMetrics([], Assessments)
<-	
	.concat([], Assessments);
.

/**
 * In this plan, each delegator is notified about the proposals of his delegatees.
 */
+!notifyDelegator(
	MyActionCode, 
	MyRequestId, 
	MyDelegator, 
	MyDelegatorActionCode,
	MyDelegatorRequest,
	ASR,
	prospect(Result, Satisfaction, Regret, Delegatees))
:	
	getMyName(Me) &
	getMyReferences(Me, MyActionCode, References) &
	debug(Status)
<-	
	if (MyDelegator == none) {
		.send(manager, tell, 
			status(finished, chain(MyActionCode, prospect(Result, Satisfaction, Regret, Delegatees)))
		);	
	} 
	else {
		!mergeImpressions(References, ReferenceMeasure)
		environments.action_addOfferTax(
			MyActionCode, Delegatees, return(promise(Promise), outcome(Outcome), failure(Position))
		);
		
		if (Status == true) {
			.print("[OFFER] for task (", MyActionCode , "): Promise: ", Promise, "; Outcome: ", Outcome);
		}
		
		!countBid(MyDelegatorActionCode, MyActionCode);
		
		if (Outcome == []){
			.send(MyDelegator, tell, 
				quotation(
					requestId(MyDelegatorRequest), 
					task(MyDelegatorActionCode), 
					asr(ASR),
					proposal(
						action(Me, MyActionCode), 
						promise(Promise), 
						outcome(Outcome),
						failure(Position),
						ref(ReferenceMeasure)
					),
					[prospect(result(failure), Satisfaction, Regret, Delegatees)]
				)
			);
		}
		else {
			.send(MyDelegator, tell, 
				quotation(
					requestId(MyDelegatorRequest), 
					task(MyDelegatorActionCode), 
					asr(ASR), 
					proposal(
						action(Me, MyActionCode), 
						promise(Promise), 
						outcome(Outcome),
						failure(Position),
						ref(ReferenceMeasure)
					),
					[prospect(result(success), Satisfaction, Regret, Delegatees)]
				)
			);			
		}
	}
.

/**
 * This plan ends the current iteration for a given delegator.
 */
+!processing(RootActionCode, Prospect)
:
	debug(Status)
<-	
	if (Status == true) {
		.print("[END] delegatee for task (", RootActionCode , "):", Prospect);
	}
	!evaluateProspect(RootActionCode, Prospect);
	.send(manager, tell, processed(RootActionCode));
.
/**
 * The rest of the plans process a delegation chain and produce the simulator results.
 */
+!evaluateProspect(
	DelegatorActionCode, 
	prospect(
		result(Result), 
		satisfaction(Satisfaction), 
		regret(Regret), 
		Delegatees
	)
)
<-	
	!updateDelegatorStatistics(DelegatorActionCode, Result, Satisfaction, Regret);
	!evaluateDelegatees(DelegatorActionCode, Delegatees);
.
+!evaluateProspect(_, prospect(_,_,_,[])).

@ag_p2 [atomic]
+!updateDelegatorStatistics(DelegatorActionCode, Result, Satisfaction, Regret)
<-	
	environments.action_evaluateProspect(DelegatorActionCode, Result, Satisfaction, Regret);
.
+!evaluateDelegatees(
	DelegatorActionCode, [delegatee(action(_, DelegateeActionCode), 
	promise(Promise), outcome(Outcome), failure(Position), SubDelegatees) | T])
<-	
	!newDelegationInstance(DelegateeActionCode, SubDelegatees);
	!updateDelegateeStatistics(
		DelegatorActionCode, 
		DelegateeActionCode, 
		Promise, 
		Outcome, 
		Position, 
		Delegator, 
		Rating
	);
	.send(Delegator, achieve, generateSocialEvaluations(Rating));
	!evaluateDelegatees(DelegatorActionCode, T);
.
+!evaluateDelegatees(_,[]).

@ag_p3 [atomic]
+!updateDelegateeStatistics(
	DelegatorActionCode, 
	DelegateeActionCode, 
	Promise, 
	Outcome, 
	Position, 
	Delegator, 
	Rating
)
<-	
	environments.action_evaluateDelegatee(DelegatorActionCode, DelegateeActionCode, 
		Promise, Outcome, Position, assessment(Delegator, Rating));
.
+!newDelegationInstance(DelegateeActionCode, [SubDelegation |T])
<-	
	!evaluateProspect(DelegateeActionCode, SubDelegation);
	!newDelegationInstance(DelegateeActionCode, T);
.
+!newDelegationInstance(_, []).

/**
 * This plan generates social evaluations.
 */
+!generateSocialEvaluations(rating(action(Delegatee, DelegateeActionCode), Iteration, Rating))
:
	debug(Status)
<-	
	!getNeighbors(DelegateeActionCode, Neighbors);
	+imp(action(Delegatee, DelegateeActionCode), Iteration, Rating);
	.send(Neighbors, tell, imp(action(Delegatee, DelegateeActionCode), Iteration, Rating));
	.send(Delegatee, tell, imp(action(Delegatee, DelegateeActionCode), Iteration, Rating));
	if (Status == true) {
		.print("[IMPRESSION] (imp(action(", Delegatee , ",", DelegateeActionCode, "),", Iteration,",", Rating, ")");
	}
.

{ include("src/asl/socialModule.asl") }
{ include("src/asl/agent_auxiliaryPlans.asl") }
{ include("src/asl/agent_rules.asl") }