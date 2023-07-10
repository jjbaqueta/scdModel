package environments;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;
import partnerSelection.DelegatorStatistics;

/**
 * This action updates the delegator's statistics.
 * 
 * Action inputs:
 *	args[0] (Atom): the delegator's action code.
 *	args[1] (Atom): the product's result.
 *	args[2] (NumberTerm): the satisfaction degree.
 *	args[3] (NumberTerm): the regret degree.
 */
public class action_evaluateProspect extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;
	
    @Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		Atom delegatorActionCode = (Atom) args[0];
		Atom result = (Atom) args[1];
		NumberTerm ntSatisfaction = (NumberTerm) args[2];
		NumberTerm ntRegret = (NumberTerm) args[3];
		
		DelegatorStatistics delegatorStatistics = DefaultEnvironment.statistics.get(delegatorActionCode.toString());
		
		if (result.toString().equals("success")) {
			delegatorStatistics.countSuccess();
		} 
		else {
			delegatorStatistics.countFailure();
		}
		delegatorStatistics.updateSatisfaction(ntSatisfaction.solve());
		delegatorStatistics.updateRegret(ntRegret.solve());
		
		return true;
	}
}