package environments;

import core.model.agents.Agent;
import core.model.goals.Goal;
import core.structures.dsTree.DSTreeVariable;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Term;

/**
 * This action returns the list of action codes of an agent.
 * 
 * Action input:
 * 	args[0] (Atom): agent's name.
 * 
 * Action output: 
 * 	args[1] (ListTerm): agent's action codes.
 */
public class action_getAgentActions extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		Atom agentName = (Atom) args[0];
		
		Agent agent = DefaultEnvironment.getAgentByName(agentName.toString());
		ListTerm actions = new ListTermImpl();
		
		for (Goal agentGoal : agent.getGoals()) {
			for (DSTreeVariable variable : agentGoal.getDSTree().getVariables()) {
				actions.add(new Atom(variable.getActionCode()));
			}
		}
		return un.unifies(actions, args[1]);
	}
}