package environments;

import core.model.agents.Agent;
import core.structures.dsTree.DSTreeVariable;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;

/**
 * This action returns the delegator's partners.
 * 
 * Action input:
 * 	args[0] (Atom): delegator's action code.
 * 
 * Action output: 
 * 	args[1] (List of terms): partners = [partner(ag3,ag3r2t1), partner(ag5,ag5r2t2), ...].
 */
public class action_getPartners extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		Atom delegatorActionCode = (Atom) args[0];
		
		ListTerm partners = new ListTermImpl();
		Agent delegator = DefaultEnvironment.getAgentFromActionCode(delegatorActionCode.toString());

		for (DSTreeVariable variable : delegator.getGoalByActionCode(delegatorActionCode.toString()).getDSTree().getVariables()) {
			Structure sPartner = new Structure("partner");
			sPartner.addTerm(new Atom(DefaultEnvironment.getAgentFromActionCode(variable.getActionCode()).getName()));
			sPartner.addTerm(new Atom(variable.getActionCode()));
			partners.add(sPartner);			
		}
		return un.unifies(partners, args[1]);
	}
}