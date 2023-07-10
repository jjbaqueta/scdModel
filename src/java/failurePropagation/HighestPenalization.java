package failurePropagation;

import java.util.Collections;
import java.util.List;

public class HighestPenalization implements FailurePropagationAlgorithm{

	/**
	 * @return the lowest position on the list. 
	 */
	public int computeFailurePosition(List<Integer> childrenPositions) {
		return Collections.min(childrenPositions);
	}
}