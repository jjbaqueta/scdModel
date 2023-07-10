package partnerSelection;

public enum PartnerSelectionType {
	STATIC_EGREEDY,
	DYNAMIC_EGREEDY,
	UCB;
	
	public String getName() {
		return name();
	}
}