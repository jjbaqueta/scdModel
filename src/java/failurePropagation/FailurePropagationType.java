package failurePropagation;

public enum FailurePropagationType {
	LOWEST_PENALIZATION, 
	HIGHEST_PENALIZATION;
	
	public String getName() {
		return name();
	}
}