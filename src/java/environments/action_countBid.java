package environments;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.Term;
import partnerSelection.DelegatorStatistics;

/**
 * This action increments the bid counter of a delegatee.
 * 
 * Action input:
 * 	args[0] (Atom): delegator's action code.
 * 	args[1] (Atom): delegatee's action code.
 */
public class action_countBid extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		Atom delegatorActionCode = (Atom) args[0];
		Atom delegateeActionCode = (Atom) args[1];
		
		DelegatorStatistics delegatorStatistics = DefaultEnvironment.statistics.get(delegatorActionCode.toString());
		delegatorStatistics.getPartnerStatistics(delegateeActionCode.toString()).countBid();
		
		return true;
	}
}