/*
 * This module contains the rules used by agents.
 * 
 * @author: Baqueta. 
 */

/**
 * This rule returns the agent's name.
 */
getMyName(Name) 
:-	.my_name(Name)
.

/**
 * This rule returns true if all partners have sent their quotations; otherwise, false.
 */
checkQuotations(MyActionCode, MyRequestId)
:-	
	request(id(MyRequestId), _, task(MyActionCode), partners(Partners)) &
	.length(Partners, N_partners) &
	.count(quotation(requestId(MyRequestId), task(MyActionCode), _, 
		proposal(_,_,_,_,_),_)[source(_)], N_offers) &
 	N_partners == N_offers
.

/**
 * This rule returns all proposals sent to the agent.
 */
getProposals(task(MyActionCode), MyRequestId, Proposals)
:-	.findall(
		proposal(
			action(Partner, PartnerActionCode),			
			promise(Promise),
 			outcome(Outcome),
 			failure(Position),
 			ref(Measure),
 			asr(ASR),
 			SubPartners	
		), 
		quotation(
			requestId(MyRequestId), 
			task(MyActionCode), 
			asr(ASR),
			proposal(
				action(Partner, PartnerActionCode), 
				promise(Promise),
 				outcome(Outcome),
				failure(Position),
				ref(Measure)
			),
			SubPartners
		)[source(Partner)],
		Proposals
	)
.