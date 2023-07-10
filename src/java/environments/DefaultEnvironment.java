package environments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.measures.NumericalMeasures;
import core.model.agents.Agent;
import core.model.roles.Role;
import core.model.tasks.Task;
import core.structures.dsNet.DSNet;
import core.structures.dsTree.DSTreeVariable;
import failurePropagation.FailurePropagationAlgorithm;
import failurePropagation.HighestPenalization;
import failurePropagation.LowestPenalization;
import global.Paths;
import global.Report;
import global.SimulatorLog;
import global.SimulatorParameters;
import global.services.ActionGenerator;
import global.services.FileOperations;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;
import openMas.DSNetStreaming;
import partnerSelection.DelegatorStatistics;

public class DefaultEnvironment extends Environment{
	
	public static SimulatorParameters simulatorParameters = new SimulatorParameters();
	public static Map<String, DelegatorStatistics> statistics = new HashMap<String, DelegatorStatistics>();
	public static Map<String, Memory> agsMemory = new HashMap<String, Memory>();
	public static List<Report> reports = new ArrayList<Report>();
	
	public static Integer currentIteration = 1;
	public static FailurePropagationAlgorithm failurePropagationApproach;

	public static Double maxTaskTime = 0.0;
	public static Double timeOffset = 1.0;
	
	private SimulatorLog log;
	private static Long itrStartTime;
	private static Map<String, Integer> agsNamesMap = new HashMap<String, Integer>();
	private static DSNetStreaming dsStreaming = new DSNetStreaming();
	private static DSNet dsNet = dsStreaming.getCurrentDSNet();
	
	@Override
	public void init(String[] args) {				
		super.init(args);
		
		File itrFile = new File(Paths.OUTPUT_ITR.getPath());
		if (itrFile.exists()) {
			itrFile.delete();
		}
		
		File debugFile = new File(Paths.OUTPUT_DEBUG.getPath());
		if (debugFile.exists()) {
			debugFile.delete();
		}
		
		try {
			switch (simulatorParameters.failurePropagationAlg) {
				case LOWEST_PENALIZATION:
					failurePropagationApproach = new LowestPenalization();
					break;
				case HIGHEST_PENALIZATION:
					failurePropagationApproach = new HighestPenalization();
					break;
				default:
					throw new Error("Parameter is not valid: " + simulatorParameters.failurePropagationAlg);
				}
			
			log = new SimulatorLog();
			updatePercepts();
		} 
		catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * This method sends perceptions to agents.
	 */
	private void updatePercepts() throws Exception {
		clearPercepts("manager");
		
		addPercept("manager", Literal.parseLiteral("max(" + simulatorParameters.numberOfIterations + ")"));
		
		if (simulatorParameters.debugMode) {
			addPercept("manager", Literal.parseLiteral("debug(true)"));
		}
		else {
			addPercept("manager", Literal.parseLiteral("debug(false)"));
		}
		
		for (Agent agent : dsNet.getAgents()) {
			switch (agent.getType()) {
				case DELEGATOR:
					addPercept("manager", Literal.parseLiteral("delegator(" + agent.getName() + ")"));
					break;
				case DELEGATEE:
					addPercept("manager", Literal.parseLiteral("delegatee(" + agent.getName() + ")"));
					break;
				default:
					addPercept("manager", Literal.parseLiteral("hybrid(" + agent.getName() + ")"));
					break;
			}
			
			agsNamesMap.put(agent.getName(), agent.getId());
			agsMemory.put(agent.getName(), new Memory(simulatorParameters.memoryCapacity));
			
			for (String actionCode : agent.getActionCodes()){
				statistics.put(actionCode, new DelegatorStatistics(actionCode));				
				for (DSTreeVariable variable : agent.getGoalByActionCode(actionCode).getDSTree().getVariables()) {
					statistics.get(actionCode).addPartnerStatistics(variable.getActionCode());
				}
			}
		}
		itrStartTime = System.currentTimeMillis();
		addPercept("manager", Literal.parseLiteral("start_delegations"));
	}
	
	/**
	 * This method receives messages from the agents and answers them.
	 */
	@Override
	public boolean executeAction(String agName, Structure action) {
		try {
			if (action.equals(Literal.parseLiteral("new(iteration)"))) {
				currentIteration++;
				return true;
			}
			if (action.equals(Literal.parseLiteral("experiment(finished)"))) {
				List<Agent> agents = dsNet.getAgents();
				List<Object> actionCodes = Arrays.asList(agents.get(0).getActionCodes().toArray());
				
				StringBuffer data = new StringBuffer();
				data.append("data=\'[");
				data.append(statistics.get(actionCodes.get(0)).toJsonString());
				
				for (int j = 1; j < actionCodes.size(); j++){
					data.append(",").append(statistics.get(actionCodes.get(j)).toJsonString());
				}
				for (int i = 1; i < agents.size(); i++) {
					actionCodes = Arrays.asList(agents.get(i).getActionCodes().toArray());
					data.append(",");
					data.append(statistics.get(actionCodes.get(0)).toJsonString());
					
					for (int j = 1; j < actionCodes.size(); j++){
						data.append(",").append(statistics.get(actionCodes.get(j)).toJsonString());
					}
				}
				data.append("]\'");
				FileOperations.writeFile(data.toString(), Paths.OUTPUT_JSON_AGENTS.getPath());
				
				List<Double> itrSuccessRateAverageActives = new ArrayList<Double>();
				List<Double> itrSatisfactionAverageActives = new ArrayList<Double>();
				List<Double> itrRegretAverageActives = new ArrayList<Double>();
				
				itrSuccessRateAverageActives.add(reports.get(0).computeSuccessRateAverageOfActives());
				itrSatisfactionAverageActives.add(reports.get(0).computeSatisfactionAverageOfActives());
				itrRegretAverageActives.add(reports.get(0).computeRegretAverageOfActives());

				List<Double> itrSuccessRateAverageAgents = new ArrayList<Double>();
				List<Double> itrSatisfactionAverageAgents = new ArrayList<Double>();
				List<Double> itrRegretAverageAgents = new ArrayList<Double>();
				
				itrSuccessRateAverageAgents.add(reports.get(0).computeSuccessRateAverageOfAgents());
				itrSatisfactionAverageAgents.add(reports.get(0).computeSatisfactionAverageOfAgents());
				itrRegretAverageAgents.add(reports.get(0).computeRegretAverageOfAgents());
				
				data = new StringBuffer();
				
				data.append("data=\'[");
				data.append(reports.get(0).toJsonString(
					1,
					itrSuccessRateAverageActives.get(0),
					itrSatisfactionAverageActives.get(0),
					itrRegretAverageActives.get(0),
					itrSuccessRateAverageAgents.get(0),
					itrSatisfactionAverageAgents.get(0),
					itrRegretAverageAgents.get(0)
				));
				
				for (int i = 1; i < reports.size(); i++) {					
					itrSuccessRateAverageActives.add(reports.get(i).computeSuccessRateAverageOfActives());
					itrSatisfactionAverageActives.add(reports.get(i).computeSatisfactionAverageOfActives());
					itrRegretAverageActives.add(reports.get(i).computeRegretAverageOfActives());
					
					itrSuccessRateAverageAgents.add(reports.get(i).computeSuccessRateAverageOfAgents());
					itrSatisfactionAverageAgents.add(reports.get(i).computeSatisfactionAverageOfAgents());
					itrRegretAverageAgents.add(reports.get(i).computeRegretAverageOfAgents());
					
					data.append(",").append(reports.get(i).toJsonString(
						i + 1,
						itrSuccessRateAverageActives.get(i),
						itrSatisfactionAverageActives.get(i),
						itrRegretAverageActives.get(i),
						itrSuccessRateAverageAgents.get(i),
						itrSatisfactionAverageAgents.get(i),
						itrRegretAverageAgents.get(i)
					));					
				}
				data.append("]\'");
				FileOperations.writeFile(data.toString(), Paths.OUTPUT_JSON_SERIES.getPath());
								
				this.log.setSuccessRateMeasuresActives(new NumericalMeasures(itrSuccessRateAverageActives));
				this.log.setSatisfactionMeasuresActives(new NumericalMeasures(itrSatisfactionAverageActives));
				this.log.setRegretMeasuresActives(new NumericalMeasures(itrRegretAverageActives));
				
				this.log.setSuccessRateMeasuresAgents(new NumericalMeasures(itrSuccessRateAverageAgents));
				this.log.setSatisfactionMeasuresAgents(new NumericalMeasures(itrSatisfactionAverageAgents));
				this.log.setRegretMeasuresAgents(new NumericalMeasures(itrRegretAverageAgents));
				
				this.log.computeExecutionTime();				
				
				FileOperations.writeFile(this.log.toString(), Paths.OUTPUT_TXT_LOG.getPath());
				FileOperations.writeFile("data=\'" + this.log.toJsonString() + "\'", Paths.OUTPUT_JSON_LOG.getPath());
				
				if (simulatorParameters.debugMode) {
					return true;					
				}
				else {
					System.exit(0);
				}
			}
		} 
		catch (NumberFormatException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
		catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void stop() {
		super.stop();
	}
	
	public static List<Agent> getAgents() {
		return dsNet.getAgents();
	}
	
	public static Agent getAgentFromActionCode(String actionCode) {
		int agentId = ActionGenerator.getAgentIdFromActionCode(actionCode);
		return dsNet.getAgentById(agentId);
	}
	
	public static Role getRoleFromActionCode(String actionCode) {
		int roleId = ActionGenerator.getRoleIdFromActionCode(actionCode);
		return dsNet.getRoleById(roleId);
	}
	
	public static Task getTaskFromActionCode(String actionCode) {
		int tasktId = ActionGenerator.getTaskIdFromActionCode(actionCode);
		return dsNet.getTaskById(tasktId);
	}
	
	public static Agent getAgentByName(String agentName) {
		return dsNet.getAgentById(agsNamesMap.get(agentName));
	}
	
	public static void saveCurrentReport() {
		List<Agent> agents = dsNet.getAgents();
		Report report = new Report();
		
		for (Agent agent : agents) {
			for (Object actionCode : Arrays.asList(agent.getActionCodes().toArray())) {
				statistics.get(actionCode.toString()).updateHistory();
				
				report.addEntry(
					actionCode.toString(),
					statistics.get(actionCode.toString()).getSuccessRate(), 
					statistics.get(actionCode.toString()).getSatisfaction(), 
					statistics.get(actionCode.toString()).getRegret(),
					statistics.get(actionCode.toString()).isActive()
				);
				statistics.get(actionCode.toString()).setAsPassive();
			}
		}
		reports.add(report);
	}
	
	public static void executionState() {
		if (currentIteration == 1 || (currentIteration % 10) == 0) {
			double itrExecutionTime = (System.currentTimeMillis() - itrStartTime) / 1000d;
			
			FileWriter fileWriter;
			try {
				if (currentIteration == 1) {
					fileWriter = new FileWriter(Paths.OUTPUT_ITR.getPath());					
				} 
				else {
					fileWriter = new FileWriter(Paths.OUTPUT_ITR.getPath(), true);
				}
				BufferedWriter buffer = new BufferedWriter(fileWriter);
				buffer.write("iteration: " + currentIteration + ";\ttime elapsed (sec): " + itrExecutionTime + "\n");
				buffer.close();
				fileWriter.close();
			} 
			catch (IOException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			itrStartTime = System.currentTimeMillis();	
		}
	}
	
	public static void showDSNet() {
		System.out.println(dsNet);
	}
	
	public static void showReports() {
		for (int i = 0; i < reports.size(); i++) {
			System.out.println("\nIteration: " + (i + 1));
			System.out.println("Success rates: " + Arrays.toString(reports.get(i).getSuccessRates().toArray()));
			System.out.println("Satisfactions: " + Arrays.toString(reports.get(i).getSatisfactions().toArray()));
			System.out.println("Regrets: " + Arrays.toString(reports.get(i).getRegrets().toArray()));	
		}		
	}
	
	public static void nextDSNet() {
		int numberOfNets = dsStreaming.getNumberOfDSNets();
		
		if (numberOfNets > 1) {
			int numberOfIterations = simulatorParameters.numberOfIterations;
			
			if (numberOfIterations <= numberOfNets) {
				dsNet = dsStreaming.getNextDSNet();
			}
			else if (currentIteration % (numberOfIterations / numberOfNets) == 0) {
				dsNet = dsStreaming.getNextDSNet();			
			}
		}
	}
	
	public static void updateTime() {
		if (maxTaskTime == 0.0) {
			timeOffset++;
		}
		else {
			timeOffset += maxTaskTime;
			maxTaskTime = 0.0;
		}
	}
}