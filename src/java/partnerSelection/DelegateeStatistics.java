package partnerSelection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import environments.DefaultEnvironment;

public class DelegateeStatistics {
	private String actionCode;
	
	private Integer actionCounter;
	private Integer successCounter;
	private Integer bidCounter;
	
	private List<Double> successHistory;
	private List<Double> failureHistory;
	
	private final double rangeIni = DefaultEnvironment.simulatorParameters.accuracyRangeStart;
	private final double rangeEnd = DefaultEnvironment.simulatorParameters.accuracyRangeEnd;
	private final double maxItr = DefaultEnvironment.simulatorParameters.accuracyMaxIteration;
	
	public DelegateeStatistics(String actionCode) {
		this.actionCode = actionCode;
		this.actionCounter = 0;
		this.successCounter = 0;
		this.bidCounter = 0;
		this.successHistory = new ArrayList<Double>();
		this.failureHistory = new ArrayList<Double>();
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
	
	public int getBidCounter() {
		return this.bidCounter;
	}
	
	public List<Double> getSuccessHistory() {
		return Collections.unmodifiableList(this.successHistory);
	}

	public List<Double> getFailureHistory() {
		return Collections.unmodifiableList(this.failureHistory);
	}
	
	public double getSuccessProbability() {
		if (actionCounter == 0.0) {
			return DefaultEnvironment.simulatorParameters.startupValue;
		} 
		else if (successCounter == 0) {
			if (actionCounter < maxItr) {
				return 1 - computeAccuracy();
			}
			return 1 - this.rangeEnd;
		} 
		else {
			double sr = (successCounter / (double) actionCounter);
			
			if (actionCounter < maxItr) {
				return sr * computeAccuracy();
			}
			return sr;
		}
	}

	public double computeAccuracy() {
		return rangeEnd + (rangeIni - rangeEnd) * Math.pow((maxItr - actionCounter) / maxItr, 2);
	}
	
	public double getFailureProbability() {
		return 1 - getSuccessProbability();
	}
	
	public void countSuccess() {
		this.successCounter++;
		this.actionCounter++;
		this.successHistory.add(getSuccessProbability());
		this.failureHistory.add(getFailureProbability());
	}

	public void countFailure() {
		this.actionCounter++;
		this.successHistory.add(getSuccessProbability());
		this.failureHistory.add(getFailureProbability());
	}
	
	public void countBid() {
		this.bidCounter++;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject obj = new JSONObject();
        obj.put("actionCode", this.actionCode);
        obj.put("actionCounter", this.actionCounter);
        obj.put("successCounter", this.successCounter);
        obj.put("bidCounter", this.bidCounter);
        
        JSONArray successlist = new JSONArray();
        
        if(this.successHistory.isEmpty()) {
        	successlist.add(DefaultEnvironment.simulatorParameters.startupValue);
        }
        else {
        	for (double value : this.successHistory) {
        		successlist.add(value);        	
        	}
        }
        obj.put("successProb", successlist);
        
        JSONArray failurelist = new JSONArray();
        
        if(this.failureHistory.isEmpty()) {
        	failurelist.add(1 - DefaultEnvironment.simulatorParameters.startupValue);
        }
        else {
        	for (double value : this.failureHistory) {
        		failurelist.add(value);        	
        	}
        }
        obj.put("failureProb", failurelist);
        
        return obj;
	}
	
	public String toJsonString() {
        return toJson().toJSONString();
	}
	
	@Override
	public String toString() {
		return "DELEGATEE'S STATISTICS: \n"
			+ "\tAction code: " + this.actionCode + "\n"
			+ "\tNumber of performed tasks: " + this.actionCounter + "\n"
			+ "\tNumber of successes: " + this.successCounter + "\n"
			+ "\tNumber of bids: " + this.bidCounter + "\n"
			+ "\tCurrent sucess probability: " + this.getSuccessProbability() + "\n"
		;
	}
}