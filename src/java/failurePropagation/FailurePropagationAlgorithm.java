package failurePropagation;

import java.util.List;

public interface FailurePropagationAlgorithm {

	/**
	 * This method computes the position of an agent at a failure chain based on the
	 * positions of his children.
	 * 
	 * @param childrenPositions the position of each agent's child at the failure position.
	 * @return the agent's position at the failure chain.
	 */
	public int computeFailurePosition(List<Integer> childrenPositions);
}