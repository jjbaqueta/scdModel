package global;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import core.measures.NumericalMeasures;

public class SimulatorLog {
	private Long startTime;
	private Double executionTime;
	
	private NumericalMeasures successRateMeasuresActives;
	private NumericalMeasures satisfactionMeasuresActives;
	private NumericalMeasures regretMeasuresActives;
	
	private NumericalMeasures successRateMeasuresAgents;
	private NumericalMeasures satisfactionMeasuresAgents;
	private NumericalMeasures regretMeasuresAgents;
	
	public SimulatorLog() {
		this.startTime = System.currentTimeMillis();
	}
	
	public void computeExecutionTime() {
		this.executionTime = (System.currentTimeMillis() - this.startTime) / 1000d;
	}
	
	public void setSuccessRateMeasuresActives(NumericalMeasures successRateMeasuresActives) {
		this.successRateMeasuresActives = successRateMeasuresActives;
	}

	public void setSatisfactionMeasuresActives(NumericalMeasures satisfactionMeasuresActives) {
		this.satisfactionMeasuresActives = satisfactionMeasuresActives;
	}

	public void setRegretMeasuresActives(NumericalMeasures regretMeasuresActives) {
		this.regretMeasuresActives = regretMeasuresActives;
	}
	
	public void setSuccessRateMeasuresAgents(NumericalMeasures successRateMeasuresAgents) {
		this.successRateMeasuresAgents = successRateMeasuresAgents;
	}

	public void setSatisfactionMeasuresAgents(NumericalMeasures satisfactionMeasuresAgents) {
		this.satisfactionMeasuresAgents = satisfactionMeasuresAgents;
	}

	public void setRegretMeasuresAgents(NumericalMeasures regretMeasuresAgents) {
		this.regretMeasuresAgents = regretMeasuresAgents;
	}

	@SuppressWarnings("unchecked")
	public String toJsonString() {
		JSONObject log = new JSONObject();
		log.put("executionTime", this.executionTime);
		
		JSONArray list = new JSONArray();
		for (Double value : this.successRateMeasuresActives.getValues()) {
			list.add(value);
		}
		log.put("activesSuccessValues", list);
		
		list = new JSONArray();
		for (Double value : this.satisfactionMeasuresActives.getValues()) {
			list.add(value);
		}
		log.put("activesSatisfactionValues", list);
		
		list = new JSONArray();
		for (Double value : this.regretMeasuresActives.getValues()) {
			list.add(value);
		}
		log.put("activesRegretValues", list);
		
		
		list = new JSONArray();
		for (Double value : this.successRateMeasuresAgents.getValues()) {
			list.add(value);
		}
		log.put("agentsSuccessValues", list);
		
		list = new JSONArray();
		for (Double value : this.satisfactionMeasuresAgents.getValues()) {
			list.add(value);
		}
		log.put("agentsSatisfactionValues", list);
		
		list = new JSONArray();
		for (Double value : this.regretMeasuresAgents.getValues()) {
			list.add(value);
		}
		log.put("agentsRegretValues", list);
		
		return log.toJSONString();
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("General measures:\n");
		sb.append("* Execution time").append(": ").append(this.executionTime).append("\n");
		
		sb.append("\n**** Delegators' Performance ****\n");
		
		sb.append("\n* Success rate:\n");
		sb.append(this.successRateMeasuresActives.toString());
		
		sb.append("\n* Satisfaction:\n");
		sb.append(this.satisfactionMeasuresActives.toString());
		
		sb.append("\n* Regret:\n");
		sb.append(this.regretMeasuresActives.toString());
		
		sb.append("\n**** Agents' Performance ****\n");
		
		sb.append("\n* Success rate:\n");
		sb.append(this.successRateMeasuresAgents.toString());
		
		sb.append("\n* Satisfaction:\n");
		sb.append(this.satisfactionMeasuresAgents.toString());
		
		sb.append("\n* Regret:\n");
		sb.append(this.regretMeasuresAgents.toString());
		
		return sb.toString();
	}
}