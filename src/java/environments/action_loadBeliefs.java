package environments;

import core.model.agents.Agent;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;

/**
 * This action initializes the agent's belief base.
 * 
 * Action input::
 * 	args[0] (Atom): agent's name. 
 */
public class action_loadBeliefs extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		Atom agentName = (Atom) args[0];
		
		Agent agent = DefaultEnvironment.getAgentByName(agentName.toString());
		
		Structure sType = new Structure("type");
		sType.addTerm(new Atom(agent.getType().getName().toLowerCase()));
		ts.getAg().addBel(Literal.parseLiteral(sType.toString()));
		
		return true;
	}
}