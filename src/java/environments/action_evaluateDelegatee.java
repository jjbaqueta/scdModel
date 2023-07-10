package environments;

import beliefs.Tuple;
import core.model.agents.Agent;
import core.model.tasks.Criterion;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import partnerSelection.DelegateeStatistics;

/**
 * This action generates an impression based on the delegatee's behavior.
 * 
 * Action inputs:
 *	args[0] (Atom): the delegator's action code.
 *	args[1] (Atom): the delegatee's action code.
 *	args[2] (List of terms): the delegatee's promise.
 *	args[3] (List of terms): the delegatee's outcome.
 *	args[4] (Number term): the delegation's failure position.
 *
 * Action output: 
 *	args[5] (Structure): assessment(Delegator, rating(action(Delegatee, DelegateeAcionCode), Iteration, Scores))
 */
public class action_evaluateDelegatee extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;
	
    @Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		Atom delegatorActionCode = (Atom) args[0];
		Atom delegateeActionCode = (Atom) args[1];
		ListTerm lPromise = (ListTerm) args[2];
		ListTerm lOutcome = (ListTerm) args[3];
		NumberTerm ntFailurePosition = (NumberTerm) args[4];
		
		Agent delegator = DefaultEnvironment.getAgentFromActionCode(delegatorActionCode.toString());
		Agent delegatee = DefaultEnvironment.getAgentFromActionCode(delegateeActionCode.toString());
		Tuple promise = Tuple.parseTuple(lPromise);
		Tuple outcome = Tuple.parseTuple(lOutcome);
    	Tuple rating = new Tuple();
    	
    	double taskTime = DefaultEnvironment.timeOffset + DefaultEnvironment.maxTaskTime;
    	
    	DelegateeStatistics delegateeStatistics = 
				DefaultEnvironment.statistics.get(delegatorActionCode.toString())
				.getPartnerStatistics(delegateeActionCode.toString());
    	
    	if (outcome.getCriteria().isEmpty()) {
    		delegateeStatistics.countFailure();
    		double discountFactor = DefaultEnvironment.simulatorParameters.penalizationDiscount;
    		
    		if(discountFactor < 0) {
    			discountFactor = delegateeStatistics.getSuccessProbability();
    		}
    		for (Criterion criterion : promise.getCriteria()) {
    			rating.addCriterion(criterion, (1 - (1 / ntFailurePosition.solve())) * discountFactor);
    		}
    	} 
    	else {
    		delegateeStatistics.countSuccess();
    		
    		for (Criterion criterion : promise.getCriteria()) {
    			double vOutcome = outcome.getValueOf(criterion);
    			double vPromise = promise.getValueOf(criterion);
    			
    			if (!criterion.isMaxCriterion() && (vOutcome > vPromise)) {
    				rating.addCriterion(criterion, vPromise / vOutcome);
    			} 
    			else if (criterion.isMaxCriterion() && (vOutcome < vPromise)) {
					rating.addCriterion(criterion, vOutcome / vPromise);
				}
				else {
					rating.addCriterion(criterion, 1);
				}
    		}
    		
    		taskTime = outcome.getValueOf(Criterion.TIME);
    		
    		if (DefaultEnvironment.maxTaskTime < taskTime) {
    			DefaultEnvironment.maxTaskTime = taskTime;
    		}
    		
    		taskTime += DefaultEnvironment.timeOffset;
    	}
    	
		Structure sAction = new Structure("action");
		sAction.addTerm(new Atom(delegatee.getName()));
		sAction.addTerm(new Atom(delegateeActionCode.toString()));

		Structure sRating = new Structure("rating");
		sRating.addTerm(sAction);
		sRating.addTerm(new NumberTermImpl(taskTime));
		sRating.addTerm(rating.toTermList());
		
		Structure sAssessment = new Structure("assessment");
		sAssessment.addTerm(new Atom(delegator.getName()));
		sAssessment.addTerm(sRating);
		
		return un.unifies(sAssessment, args[5]);
	}
}