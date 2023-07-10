package environments;

import java.util.Random;

import beliefs.Tuple;
import core.model.agents.AgentExpertise;
import core.model.tasks.Criterion;

/**
 * This class implements a task executor. 
 */
public abstract class TaskExecutor {
	
	/**
	 * This method calculates the agent's outcome based on his profile and promise.
	 * 
	 * @param profile the agent's profile, it indicates the agent's expertise level and mistake frequency.
	 * @param promise the promise by the agent to his delegator.
	 * @return the agent's outcome.
	 */
	public static Tuple generateOutcome(AgentExpertise expertiseDegree, double mistakeFrequency, Tuple promise) {
		Random rand = new Random();
		Tuple outcome = new Tuple();

		if (rand.nextDouble() >= mistakeFrequency) {
			for (Criterion criterion : promise.getCriteria()) {
				double value = promise.getValueOf(criterion);
				
				switch (expertiseDegree) {
					case BEGINNER:
						if (criterion.isMaxCriterion()) {
							outcome.addCriterion(criterion, value - (value * (0.9 + (1.0 - 0.9) * rand.nextDouble())));
						} 
						else {
							outcome.addCriterion(criterion, value * (5.0 + (100 - 5.0) * rand.nextDouble())); //0.01 -> 0,2
						}
					break;
					
					case NOVICE:
						if (criterion.isMaxCriterion()) {
							outcome.addCriterion(criterion, value - (value * (0.7 + (0.9 - 0.7) * rand.nextDouble())));
						} 
						else {
							outcome.addCriterion(criterion, value * (2.5 + (5.0 - 2.5) * rand.nextDouble())); //0.2 -> 0,4
						}
					break;
					
					case COMPETENT:
						if (criterion.isMaxCriterion()) {
							outcome.addCriterion(criterion, value - (value * (0.5 + (0.7 - 0.5) * rand.nextDouble())));
						} 
						else {
							outcome.addCriterion(criterion, value * (1.6666 + (2.5 - 1.6666) * rand.nextDouble())); //0,4 -> 0,6
						}
					break;
					
					case PROFICIENT:
						if (criterion.isMaxCriterion()) {
							outcome.addCriterion(criterion, value - (value * (0.3 + (0.5 - 0.3) * rand.nextDouble())));
						} 
						else {
							outcome.addCriterion(criterion, value * (1.25 + (1.6666 - 1.25) * rand.nextDouble())); //0,6 -> 0,8
						}
					break;
					
					case EXPERT:
						if (criterion.isMaxCriterion()) {
							outcome.addCriterion(criterion, value - (value * (0.1 + (0.3 - 0.1) * rand.nextDouble())));
						} 
						else {
							outcome.addCriterion(criterion, value * (1.0001 + (1.25 - 1.0001) * rand.nextDouble())); //0,8 -> 0,9999
						}
					break;
					
					default: // master
						outcome.addCriterion(criterion, promise.getValueOf(criterion));
				}
			}
		}
		return outcome;
	}
}