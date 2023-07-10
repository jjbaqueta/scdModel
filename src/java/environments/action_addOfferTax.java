// Internal action code for project delegationNet

package environments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import beliefs.Tuple;
import core.model.agents.Agent;
import core.model.agents.AgentBehavior;
import core.model.tasks.CriteriaFactory;
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

/**
 * This action generates promises and outcomes based on the offers sent to an agent (from his delegatees).
 * 
 * Action inputs:
 * 	args[0] (Atom): the agent's action code.
 * 	args[1] (List of terms): the agent's delegatees = [delegatee(
 * 			action(Delegatee, ActionCode), 
 * 			promise(Promise),
 * 			outcome(Outcome),
 * 			failure(Position),
 * 			SubDelegatees
 * 		), ...]
 *	.
 * 
 * Action output: 
 * 	args[2] (Structure): return(Promise, Outcome, failure(Position))
 */
public class action_addOfferTax extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;
	
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	Atom agentActionCode = (Atom) args[0];
    	ListTerm lDelegatees = (ListTerm) args[1];
    	
		if (lDelegatees.size() == 1) {
			Structure sDelegatee = (Structure) lDelegatees.get(0);
			Structure sAction = (Structure) sDelegatee.getTerm(0);
			Atom delegateeActionCode = (Atom) sAction.getTerm(1);
		
			if (agentActionCode.toString().equals(delegateeActionCode.toString())) {
				Structure sPromise = (Structure) sDelegatee.getTerm(1);
				Structure sOutcome = (Structure) sDelegatee.getTerm(2);
				Structure sFailurePosition = (Structure) sDelegatee.getTerm(3);
				
				Structure sReturn = new Structure("return");
				sReturn.addTerm(sPromise);
				sReturn.addTerm(sOutcome);
				sReturn.addTerm(sFailurePosition);
				
				return un.unifies(sReturn, args[2]);
			}
		}
		
		Agent agent = DefaultEnvironment.getAgentFromActionCode(agentActionCode.toString());
    	List<Tuple> delegateesPromises = new ArrayList<Tuple>();
    	List<Integer> failurePositions = new ArrayList<Integer>();
    	
    	boolean delegateeFailure = false;
    	
    	for (Term term : lDelegatees) {
    		Structure sDelegatee = (Structure) term;
    		Structure sPromise = (Structure) sDelegatee.getTerm(1);
    		Structure sOutcome = (Structure) sDelegatee.getTerm(2);
    		Structure sFailurePosition = (Structure) sDelegatee.getTerm(3);
    		ListTerm lPromise = (ListTerm) sPromise.getTerm(0);
    		ListTerm lOutcome = (ListTerm) sOutcome.getTerm(0);
    		NumberTerm ntFailurePosition = (NumberTerm) sFailurePosition.getTerm(0);
    		delegateesPromises.add(Tuple.parseTuple(lPromise));
    		
    		if (!delegateeFailure && lOutcome.isEmpty()) {
    			delegateeFailure = true;
    		}
    		failurePositions.add((int) ntFailurePosition.solve());
    	}
    	
    	double failurePosition = DefaultEnvironment.failurePropagationApproach
    			.computeFailurePosition(failurePositions);
    			
    	HashMap<Criterion, List<Double>> criteriaMap = new HashMap<Criterion, List<Double>>();
		
    	for (Tuple promise : delegateesPromises) {
			for (Criterion criterion : promise.getCriteria()) {
				if (criteriaMap.containsKey(criterion)) {
					criteriaMap.get(criterion).add(promise.getValueOf(criterion));
				} 
				else {
					List<Double> values = new ArrayList<Double>();
					values.add(promise.getValueOf(criterion));
					criteriaMap.put(criterion, values);
				}
			}
		}
		
    	Tuple agentPromise = new Tuple();
		
		for (Criterion criterion : criteriaMap.keySet()) {
			agentPromise.addCriterion(criterion, 
					CriteriaFactory.mergeValues(criterion, criteriaMap.get(criterion)));
		}
		
		Structure sReturn = new Structure("return");
		Structure sPromise = new Structure("promise");
		Structure sOutcome = new Structure("outcome");
		Structure sFailurePosition = new Structure("failure");
		sPromise.addTerm(agentPromise.toTermList());
		
		/*
		 * The agent's outcome relies on his delegatee's failures, his profile, and his promise.
		 * If any delegatee fails, the agent cannot achieve his goal, so he also fails.
		 */
		if (delegateeFailure) {
			failurePosition++;
			sFailurePosition.addTerm(new NumberTermImpl(failurePosition));
			sOutcome.addTerm(new Tuple().toTermList());
		} 
		else {
			AgentBehavior behavior = agent.getBehaviorByActionCode(agentActionCode.toString());
			Tuple agentOutcome = TaskExecutor.generateOutcome(
					behavior.getDeliveryProfile().getExpertiseDegree(), 
					behavior.getDeliveryProfile().getMistakeProbability(), 
					agentPromise
			);
			
			if (agentOutcome.getCriteria().isEmpty()) {
				sFailurePosition.addTerm(new NumberTermImpl(1));
			} 
			else {
				sFailurePosition.addTerm(new NumberTermImpl(0));
			}
			sOutcome.addTerm(agentOutcome.toTermList());
		}
		sReturn.addTerm(sPromise);
		sReturn.addTerm(sOutcome);
		sReturn.addTerm(sFailurePosition);
		return un.unifies(sReturn, args[2]);
    }
}
