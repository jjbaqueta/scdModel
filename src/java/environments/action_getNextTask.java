package environments;

import java.util.Collection;
import java.util.Random;

import core.model.agents.Agent;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;

/**
 * This action defines the next task of a delegator.
 * 
 * Action input:
 * 	args[0] (Atom): delegator's name.
 * 
 * Action output: 
 * 	args[1] (Structure): task(ActionCode).
 */
public class action_getNextTask extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;

	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	Atom delegatorName = (Atom) args[0];
    	
    	Agent delegator = DefaultEnvironment.getAgentByName(delegatorName.toString());
    	Collection<String> actionCodes = delegator.getActionCodes();
    	
		if (actionCodes.isEmpty()) {
			throw new Error("The delegator does not have tasks, delegator: " + delegator);
		}
		
		Random rand = new Random();
		Structure sTask = new Structure("task");
		String actionCode = (String) actionCodes.toArray()[rand.nextInt(actionCodes.size())];
		sTask.addTerm(new Atom(actionCode));
		
		return un.unifies(Literal.parseLiteral(sTask.toString()), args[1]);
    }
}