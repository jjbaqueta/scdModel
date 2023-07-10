package environments;

import core.model.agents.Agent;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Term;

/**
 * This action returns the neighborhood of a given agent. Two agents are neighbors when they assess the same partner.
 * 
 * Action inputs:
 * 	args[0] (Atom): the agent's name.
 * 	args[1] (Atom): the agent's action code.
 *	args[2] (ListTerm): the list of assessors of the agent (possible neighbors). 
 * 
 * Action output: 
 * 	args[3] (ListTerm): the neighborhood of assessors regarding the agent.
 */
public class action_getNeighbors extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		Atom agentName = (Atom) args[0];
		Atom partnerActionCode = (Atom) args[1];
		ListTerm assessors = (ListTerm) args[2];

		Agent partner = DefaultEnvironment.getAgentFromActionCode(partnerActionCode.toString());
		ListTerm neighbors = new ListTermImpl();
		
		for (Term assessor : assessors) {
			if (!agentName.toString().equals(assessor.toString()) && !partner.getName().equals(assessor.toString())) {
				neighbors.add(assessor);
			}
		}
		return un.unifies(neighbors, args[3]);
	}
}