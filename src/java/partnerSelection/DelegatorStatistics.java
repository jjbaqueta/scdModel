package partnerSelection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import environments.DefaultEnvironment;

public class DelegatorStatistics {
	private String actionCode;
	
	private Integer actionCounter;
	private Integer successCounter;
	
	private Double currentSuccessRate;
	private Double currentSatisfaction;
	private Double currentRegret;
	private Double accSatisfaction;
	private Double accRegret;
	
	private Boolean active; 
	
	private List<Double> successHistory;
	private List<Double> satisfactionHistory;
	private List<Double> regretHistory;
	
	private Map<String, DelegateeStatistics> partnersActions;
	
	public DelegatorStatistics(String actionCode) {
		this.actionCode = actionCode;
		this.actionCounter = 0;
		this.successCounter = 0;
		this.currentSuccessRate = DefaultEnvironment.simulatorParameters.startupValue;
		this.currentSatisfaction = 0.0;
		this.currentRegret = 0.0;
		this.accSatisfaction = 0.0;
		this.accRegret = 0.0;
		this.active = false;
		this.successHistory = new ArrayList<Double>();
		this.satisfactionHistory = new ArrayList<Double>();
		this.regretHistory = new ArrayList<Double>();
		this.partnersActions = new HashMap<String, DelegateeStatistics>();
	}
	
	public String getActionCode() {
		return this.actionCode;
	}
	
	public int getActionCounter() {
		return this.actionCounter;
	}
	
	public int getSuccessCounter() {
		return this.successCounter;
	}
	
	public int getFailureCounter() {
		return this.actionCounter - this.successCounter;
	}
	
	public double getSuccessRate() {
		return this.currentSuccessRate;
	}
	
	public double getSatisfaction() {
		return this.currentSatisfaction;
	}
	
	public double getRegret() {
		return this.currentRegret;
	}
	
	private double computeSuccessRate() {
		if (this.actionCounter == 0) {
			return DefaultEnvironment.simulatorParameters.startupValue;
		} 
		else {
			return this.successCounter / (double) this.actionCounter;
		}
	}
	
	private double computeSatisfaction() {
		if (this.actionCounter == 0) {
			return 0.0;
		} 
		else {
			return this.accSatisfaction / (double) this.actionCounter;
		}
	}
	
	private double computeRegret() {
		if (this.actionCounter == 0) {
			return 0.0;
		} 
		else {
			return this.accRegret / (double) this.actionCounter;
		}
	}
	
	public void countSuccess() {
		this.active = true;
		this.actionCounter++;
		this.successCounter++;
		this.currentSuccessRate = computeSuccessRate();
	}
	
	public void countFailure() {
		this.active = true;
		this.actionCounter++;
		this.currentSuccessRate = computeSuccessRate();
	}
	
	public void updateSatisfaction(double satisfactionDegree) {
		this.accSatisfaction += satisfactionDegree;
		this.currentSatisfaction = computeSatisfaction();
	}
	
	public void updateRegret(double regretDegree) {
		this.accRegret += regretDegree;
		this.currentRegret = computeRegret();
	}
	
	public void updateHistory() {
		this.successHistory.add(this.currentSuccessRate);
		this.satisfactionHistory.add(this.currentSatisfaction);
		this.regretHistory.add(this.currentRegret);
	}
	
	public void setAsActive() {
		this.active = true;
	}
	
	public void setAsPassive() {
		this.active = false;
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public void addPartnerStatistics(String partnerActionCode) {
		this.partnersActions.put(partnerActionCode, new DelegateeStatistics(partnerActionCode));
	}
	
	public DelegateeStatistics getPartnerStatistics(String partnerActionCode) {
		return this.partnersActions.get(partnerActionCode);
	}
	
	public List<Double> getSuccessHistory() {
		return Collections.unmodifiableList(this.successHistory);
	}
	
	public List<Double> getSatisfactionHistory() {
		return Collections.unmodifiableList(this.satisfactionHistory);
	}

	public List<Double> getRegretHistory() {
		return Collections.unmodifiableList(this.regretHistory);
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject obj = new JSONObject();
		obj.put("actionCode", this.actionCode);
		
		JSONArray delegateesList = new JSONArray();
		
        for (String delegateeActionCode : this.partnersActions.keySet()) {
        	delegateesList.add(this.partnersActions.get(delegateeActionCode).toJson());
        }
        obj.put("delegatees", delegateesList);
		        
    	JSONArray list = new JSONArray();
    	
    	for (double value : this.successHistory) {
    		list.add(value);
    	}
    	obj.put("successHistory", list);
        
    	list = new JSONArray();
    	
    	for (double value : this.satisfactionHistory) {
    		list.add(value);
    	}
    	obj.put("satisfactionHistory", list);
        
    	list = new JSONArray();
    	
    	for (double value : this.regretHistory) {
    		list.add(value);
    	}
    	obj.put("regretHistory", list);
        
		return obj;
	}
	
	public String toJsonString() {
		return toJson().toJSONString();
	}
	
	@Override
	public String toString() {
		return "DELEGATOR'S STATISTICS: \n"
			+ "\tAction code: " + this.actionCode + "\n"
			+ "\tNumber of performed tasks: " + this.actionCounter + "\n"
			+ "\tNumber of successes: " + this.successCounter + "\n"
			+ "\tCurrent sucess rate: " + this.currentSuccessRate + "\n"
			+ "\tCurrent satisfaction degree: " + this.currentSatisfaction + "\n"
			+ "\tCurrent regret degree: " + this.currentRegret + "\n"
		;
	}
}