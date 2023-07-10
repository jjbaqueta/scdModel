package environments;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;

/**
 * This action changes the agent behavior.
 */
public class action_nextEnvironmentState extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		DefaultEnvironment.updateTime();
		DefaultEnvironment.executionState();
		DefaultEnvironment.nextDSNet();
		return true;
	}
}