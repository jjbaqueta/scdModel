package failurePropagation;

import java.util.Collections;
import java.util.List;

public class LowestPenalization implements FailurePropagationAlgorithm{

	/**
	 * @return the highest position on the list. 
	 */
	public int computeFailurePosition(List<Integer> childrenPositions) {
		return Collections.max(childrenPositions);
	}
}