package environments;

import beliefs.Tuple;
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
 * This action merges a list of impressions into a scalar value.
 * 
 * Action input: 
 *	args[0] (List of terms): a list of impressions = [
 *		imp(action(AgentName, AgentActionCode), Cycle, Rating)[source(Source)], 
 *		...
 *	].
 *
 * Action output: 
 * 	args[1]: {
 * 		(Number term): the value of the merged impressions; 
 * 		(Atom) none, in case there are no impressions to merge.
 * 	}
 */
public class action_mergeImpressions extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;
	
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        ListTerm imps = (ListTerm) args[0];
        
        if (imps.isEmpty()) {
        	return un.unifies(new Atom("none"), args[1]);
        }
        
        double sum = 0;
        double currentTime = DefaultEnvironment.timeOffset + DefaultEnvironment.maxTaskTime;
        
        for (Term imp : imps) {
    		Structure impression = (Structure) imp;
    		NumberTerm impressionTime = (NumberTerm) impression.getTerm(1);
    		ListTerm ratings = (ListTerm) impression.getTerm(2);	
    		
    		sum += timeDecay(currentTime, impressionTime.solve()) * mergeRating(Tuple.parseTuple(ratings));    		
    	}
        
        // SQUASHING FUNCTION:
        return un.unifies(new NumberTermImpl(1 - Math.exp(-(7.5 / currentTime) * sum)), args[1]);
    }
    
    /**
     * This method calculates the time discount for an impression based on the elapsed time since it was created.
     * @see https://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=8465141
     * 
     * @param currentTime the number of the current iteration.
     * @param impressionTime the number of the iteration in which the impression was created.
     * @return a time discount value.
     */
    private double timeDecay(double currentTime, double impressionTime) {
    	return Math.exp(-(currentTime - impressionTime) / currentTime);
    }
    
    /**
     * This method merges a set of ratings into a single scalar value.
     * 
     * @param ratings the ratings associated with an impression.
     * @return the merged value of the impressions.
     */
    private double mergeRating(Tuple ratings) {
		double score = 0.0;
		
		for (Criterion criterion : ratings.getCriteria()) {
			score += ratings.getValueOf(criterion);
		}
		return score / ratings.getCriteria().size();
	}
}