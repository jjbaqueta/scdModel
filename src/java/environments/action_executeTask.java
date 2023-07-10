package environments;

import beliefs.Tuple;
import core.model.agents.Agent;
import core.model.agents.AgentBehavior;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Term;

/**
 * This action generates an outcome based on the execution of a given task.
 * 
 * Action inputs:
 * 	args[0] (Atom): agent's action code.
 * 	args[1] (List of terms): promise = [criterion(cost,405), criterion(time,7), criterion(quality,0.8), ...].
 * 
 * Action output: 
 * 	args[2] (List of terms): outcome = {
 * 		success: [criterion(cost,505),criterion(time,10),criterion(quality,0.8), ...];
 * 		failure: [].
 *  }
 */
public class action_executeTask extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		Atom agentActionCode = (Atom) args[0];
		ListTerm lPromise = (ListTerm) args[1];

		Agent agent = DefaultEnvironment.getAgentFromActionCode(agentActionCode.toString());
		Tuple promise = Tuple.parseTuple(lPromise);		
		AgentBehavior behavior = agent.getBehaviorByActionCode(agentActionCode.toString());
		
		return un.unifies(TaskExecutor.generateOutcome(
				behavior.getExecutionProfile().getExpertiseDegree(), 
				behavior.getExecutionProfile().getMistakeProbability(), 
				promise
		).toTermList(), args[2]);
	}
}