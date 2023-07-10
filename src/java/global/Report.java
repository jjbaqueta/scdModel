package global;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONObject;

/**
 * A report stores the agents' success, satisfaction, and regret averages calculated for each iteration.
 * @author baqueta
 *
 */
public class Report {
	private List<String> actionCodes;
	private List<Double> successRates;
	private List<Double> satisfactions;
	private List<Double> regrets;
	private List<Boolean> actives;
	
	public Report() {
		this.actionCodes = new ArrayList<String>();
		this.successRates = new ArrayList<Double>();
		this.satisfactions = new ArrayList<Double>();
		this.regrets = new ArrayList<Double>();
		this.actives = new ArrayList<Boolean>();
	}
	
	public void addEntry(String actionCode, double successRate, double satisfaction, double regret, boolean active) {
		this.actionCodes.add(actionCode);
		this.successRates.add(successRate);
		this.satisfactions.add(satisfaction);
		this.regrets.add(regret);
		this.actives.add(active);
	}
	
	public List<String> getActionCodes() {
		return Collections.unmodifiableList(this.actionCodes);
	}
	
	public List<Double> getSuccessRates() {
		return Collections.unmodifiableList(this.successRates);
	}
	
	public List<Double> getSatisfactions() {
		return Collections.unmodifiableList(this.satisfactions);
	}
	
	public List<Double> getRegrets() {
		return Collections.unmodifiableList(this.regrets);
	}
	
	public List<Boolean> getActives() {
		return Collections.unmodifiableList(this.actives);
	}
	
	public double computeSuccessRateAverageOfAgents() {
		return computeAverage(this.successRates);
	}
	
	public double computeSatisfactionAverageOfAgents() {
		return computeAverage(this.satisfactions);
	}
	
	public double computeRegretAverageOfAgents() {
		return computeAverage(this.regrets);
	}
	
	public double computeSuccessRateAverageOfActives() {
		List<Double> values = new ArrayList<Double>();
		
		for (int i = 0; i < this.successRates.size(); i++) {
			if (this.actives.get(i)) {
				values.add(this.successRates.get(i));
			}
		}
		return computeAverage(values);
	}
	
	public double computeSatisfactionAverageOfActives() {
		List<Double> values = new ArrayList<Double>();
		
		for (int i = 0; i < this.satisfactions.size(); i++) {
			if (this.actives.get(i)) {
				values.add(this.satisfactions.get(i));
			}
		}
		return computeAverage(values);
	}
	
	public double computeRegretAverageOfActives() {
		List<Double> values = new ArrayList<Double>();
		
		for (int i = 0; i < this.regrets.size(); i++) {
			if (this.actives.get(i)) {
				values.add(this.regrets.get(i));
			}
		}
		return computeAverage(values);
	}
	
	private double computeAverage(List<Double> values) {
		if (values.isEmpty()) {
			return 0;
		}
		
		double average = 0;
		
		for (Double value : values) {
			average += value;
		}
		return average / values.size();
	}
	
	@SuppressWarnings("unchecked")
	public String toJsonString(
			int iteration, 
			double activesSuccessRateAvg, 
			double activesSatisfactionAvg, 
			double activesRegretAvg,
			double agentsSuccessRateAvg, 
			double agentsSatisfactionAvg, 
			double agentsRegretAvg
	) {
		JSONObject series = new JSONObject();
		
		series.put("activesSuccessRateAvg", activesSuccessRateAvg);
    	series.put("activesSatisfactionAvg", activesSatisfactionAvg);
    	series.put("activesRegretAvg", activesRegretAvg);
    	series.put("agentsSuccessRateAvg", agentsSuccessRateAvg);
    	series.put("agentsSatisfactionAvg", agentsSatisfactionAvg);
    	series.put("agentsRegretAvg", agentsRegretAvg);
    	
    	JSONObject obj = new JSONObject();
    	obj.put("iteration", iteration);
    	obj.put("series", series);
    	return obj.toJSONString();
	}
	
	@Override
	public String toString() {
		return "REPORT: " + "\n"
			+ "Delegators' codes: " + Arrays.toString(this.actionCodes.toArray()) + "\n"
			+ "Success rates: " + Arrays.toString(this.successRates.toArray()) + "\n" 
			+ "Satisfactions: " + Arrays.toString(this.satisfactions.toArray()) + "\n" 
			+ "Regrets: " + Arrays.toString(this.regrets.toArray()) + "\n";
	}
}