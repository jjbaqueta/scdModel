package global;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import failurePropagation.FailurePropagationType;
import partnerSelection.PartnerSelectionType;

public final class SimulatorParameters {
	public final Integer numberOfIterations;
	public final Integer memoryCapacity;
	public final Integer accuracyMaxIteration;
	
	public final Double imageWeight;
	public final Double reputationWeight;
	public final Double referenceWeight;
	public final Double penalizationDiscount;
	public final Double startupValue;
	public final Double accuracyRangeStart;
	public final Double accuracyRangeEnd;
	
	public final Boolean debugMode;
	
	public final FailurePropagationType failurePropagationAlg;
	public final PartnerSelectionType partnerSelectionAlg;
	public final AccumulationType accumulationAlg;
	
	public final Dimension dimensionSR;
	public final Dimension dimensionASR;
	public final Dimension dimensionCompetence;
	public final Dimension dimensionExpectation;
	public final Dimension dimensionRisk;
	public final Dimension dimensionGoal;
	
	public SimulatorParameters() {
		JSONParser parser = new JSONParser();
		JSONObject parameters = null;
		
		try {
			parameters = (JSONObject) parser.parse(new FileReader(Paths.INPUT_CONFIG_FILE.getPath()));
		} 
		catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
		catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
		catch (ParseException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		this.numberOfIterations = Math.toIntExact((Long) parameters.get("numberOfIterations"));
		this.memoryCapacity = Math.toIntExact((Long) parameters.get("memoryCapacity"));
		this.accuracyMaxIteration = Math.toIntExact((Long) parameters.get("accuracyMaxIteration"));
		
		this.imageWeight = (Double) parameters.get("imageWeight");
		this.reputationWeight = (Double) parameters.get("reputationWeight");
		this.referenceWeight = (Double) parameters.get("referenceWeight");
		this.penalizationDiscount = (Double) parameters.get("penalizationDiscount");
		this.startupValue = (Double) parameters.get("startupValue");
		this.accuracyRangeStart = (Double) parameters.get("accuracyRangeStart");
		this.accuracyRangeEnd = (Double) parameters.get("accuracyRangeEnd");
		
		this.debugMode = (Boolean) parameters.get("debugMode");
		
		this.failurePropagationAlg = FailurePropagationType.valueOf((String) parameters.get("failurePropagationAlg"));
		this.partnerSelectionAlg = PartnerSelectionType.valueOf((String) parameters.get("partnerSelectionAlg"));
		this.accumulationAlg = AccumulationType.valueOf((String) parameters.get("accumulationAlg"));
		
		JSONObject dimension = (JSONObject) parameters.get("dimensionSR");
		
		this.dimensionSR = new Dimension(
			(Boolean) dimension.get("status"), 
			(Double) dimension.get("weight")
		);
		
		dimension = (JSONObject) parameters.get("dimensionASR");
		
		this.dimensionASR = new Dimension(
			(Boolean) dimension.get("status"), 
			(Double) dimension.get("weight")
		);
		
		dimension = (JSONObject) parameters.get("dimensionCompetence");
		
		this.dimensionCompetence = new Dimension(
			(Boolean) dimension.get("status"), 
			(Double) dimension.get("weight")
		);
		
		dimension = (JSONObject) parameters.get("dimensionExpectation");
		
		this.dimensionExpectation = new Dimension(
			(Boolean) dimension.get("status"), 
			(Double) dimension.get("weight")
		);
		
		dimension = (JSONObject) parameters.get("dimensionRisk");
		
		this.dimensionRisk = new Dimension(
			(Boolean) dimension.get("status"), 
			(Double) dimension.get("weight")
		);
		
		dimension = (JSONObject) parameters.get("dimensionGoal");
		
		this.dimensionGoal = new Dimension(
			(Boolean) dimension.get("status"), 
			(Double) dimension.get("weight")
		);
	}
	
	public void showParameters() {
		System.out.println(this);
	}
	
	@Override
	public String toString() {
		return "Configuration parameters: \n"
		+ "\tNumber of iterations: " + this.numberOfIterations + "\n"
		+ "\tMemory capacity: " + this.memoryCapacity + "\n"
		+ "\tImage weight: " + this.imageWeight + "\n"
		+ "\tReputation weight: " + this.reputationWeight + "\n"
		+ "\tReference weight: " + this.referenceWeight + "\n"
		+ "\tPenalization discount: " + this.penalizationDiscount + "\n"
		+ "\tFailure propagation algorithm: " + this.failurePropagationAlg + "\n"
		+ "\tPartner selection algorithm: " + this.partnerSelectionAlg + "\n"
		+ "\tAccumulation algorithm: " + this.accumulationAlg + "\n"
		+ "\tstartup value: " + this.startupValue + "\n"
		+ "\taccuracy range start: " + this.accuracyRangeStart + "\n"
		+ "\taccuracy range end: " + this.accuracyRangeEnd + "\n"
		+ "\taccuracy max iteration: " + this.accuracyMaxIteration + "\n"
		+ "\tdebug mode: " + this.debugMode + "\n"
		+ "\tSR-dimension: {"
			+ "status: " + this.dimensionSR.getStatus() 
			+ ", weight: " + this.dimensionSR.getWeight() 
		+ "}\n"
		+ "\tASR-dimension: {"
			+ "status: " + this.dimensionASR.getStatus() 
			+ ", weight: " + this.dimensionASR.getWeight() 
		+ "}\n"
		+ "\tCompetence-dimension: {"
			+ "status: " + this.dimensionCompetence.getStatus() 
			+ ", weight: " + this.dimensionCompetence.getWeight() 
		+ "}\n"
		+ "\tExpectation-dimension: {"
			+ "status: " + this.dimensionExpectation.getStatus() 
			+ ", weight: " + this.dimensionExpectation.getWeight() 
		+ "}\n"
		+ "\tRisk-dimension: {"
			+ "status: " + this.dimensionRisk.getStatus() 
			+ ", weight: " + this.dimensionRisk.getWeight() 
		+ "}\n"
		+ "\tGoal-dimension: {"
			+ "status: " + this.dimensionGoal.getStatus() 
			+ ", weight: " + this.dimensionGoal.getWeight() 
		+ "}\n";
	}
	
	public class Dimension {
		private Boolean status;
		private Double weight;
		
		public Dimension(boolean status, double weight) {
			this.status = status;
			this.weight = weight;
		}
		
		public boolean getStatus() {
			return this.status;
		}
		
		public double getWeight() {
			return this.weight;
		}
	}
}