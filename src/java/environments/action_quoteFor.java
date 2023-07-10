package environments;

import beliefs.Tuple;
import core.model.tasks.CriteriaFactory;
import core.model.tasks.Criterion;
import core.model.tasks.Task;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.Term;

/**
 * This action generates a quote for a task. Quotes are sent from a partner to a delegator.
 * 
 * Action input:
 * 	args[0] (Atom): the partner's action code.
 * 
 * Action output: 
 * 	args[1] (List of terms): the partner's promise = [criterion(cost,400), criterion(time,6), criterion(quality,0.8), ...].
 */
public class action_quoteFor extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;
	
    @Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		Atom partnerActionCode = (Atom) args[0];
		
		Tuple promise = new Tuple();
		Task task = DefaultEnvironment.getTaskFromActionCode(partnerActionCode.toString());
		
		for (Criterion criterion : task.getCriteria().getCriteria()) {
			promise.addCriterion(criterion, CriteriaFactory.generateValue(criterion));
		}
		return un.unifies(promise.toTermList(), args[1]);
	}
}