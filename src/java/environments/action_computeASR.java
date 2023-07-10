package environments;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;
import partnerSelection.DelegateeStatistics;

/**
 * This action computes the accumulated success rate (ASR) for a pair of agents.
 * 
 * Action inputs:
 * 	args[0] (Atom): delegator's action code;
 *	args[1] (Atom): partner's action code;
 *	args[2] (Number term): accumulated success rate until the partner.
 *
 * Action return: 
 * 	args[3] (Number term): the accumulated success rate considering the partner's success probability.
 */
public class action_computeASR extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;
	
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		Atom delegatorActionCode = (Atom) args[0];
		Atom partnerActionCode = (Atom) args[1];
				
		DelegateeStatistics partnerStatistics = 
				DefaultEnvironment.statistics.get(delegatorActionCode.toString())
				.getPartnerStatistics(partnerActionCode.toString());
		
		double partnerSR = partnerStatistics.getSuccessProbability();
		
		if (args[2].isNumeric()) {
			NumberTerm ntAccSR = (NumberTerm) args[2];
			
			switch (DefaultEnvironment.simulatorParameters.accumulationAlg) {
				case SUM:
					return un.unifies(new NumberTermImpl(ntAccSR.solve() + partnerSR), args[3]);
				case PRODUCT:
					return un.unifies(new NumberTermImpl(ntAccSR.solve() * partnerSR), args[3]);
				default:
					return un.unifies(new NumberTermImpl((ntAccSR.solve() + partnerSR) - (ntAccSR.solve() * partnerSR)), args[3]);
				}
		}
		else {
			return un.unifies(new NumberTermImpl(partnerSR), args[3]);
		}
    }
}