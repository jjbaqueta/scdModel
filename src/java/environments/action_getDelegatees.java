package environments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import beliefs.Tuple;
import core.model.agents.Agent;
import core.model.tasks.Criterion;
import core.structures.dsTree.DSTreeProduct;
import core.structures.dsTree.DSTreeVariable;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import partnerSelection.DelegateeStatistics;
import partnerSelection.DelegatorStatistics;

/**
 * This action implements the partner selection process employed by delegators.
 * 
 * Action inputs:
 * 	args[0] (Atom): delegator's action code.
 * 	args[1] (List of terms): proposals =  [proposal(
 * 			action(Partner, ActionCode)
 * 			promise(Promise),
 * 			outcome(Outcome),
 * 			failure(Position),
 * 			ref(Measure),
 * 			asr(ASR),
 * 			SubPartners	
 * 	), ...].
 * args[2] (List of terms): social assessments = [assessment(ag2r2t2, img(0.9339139642404738), rep(none)), ...]
 * 
 * Action return: 
 * 	args[3] (List of terms): delegatees = prospect(
 * 			result(Result),
 * 			satisfaction(Satisfaction), 
 * 			regret(Regret)
 * 			[
 * 				delegatee(
 * 					action(Delegatee, ActionCode), 
 * 					promise(Promise),
 * 					outcome(Outcome),
 * 					failure(Position),
 * 					SubDelegatees
 * 				),
 * 				...
 * 			]
 * 		)
 */
public class action_getDelegatees extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;

	private HashMap<String, Tuple> promises;
	private HashMap<String, Tuple> outcomes;
	
	private HashMap<String, Double> baseASRs;

	private HashMap<String, Double> normSRs;
	private HashMap<String, Double> normASRs;
	private HashMap<String, Double> normExpectations;
	private HashMap<String, Double> normCompetences;
	
	private HashMap<String, Double> refMeasures;
	private HashMap<String, Double> satisfactionDgrs;
	private HashMap<String, Double> failurePositions;
	private HashMap<Criterion, Double> bestPromiseValues;

	private Double maxSR;
	private Double maxASR;
	private Double maxCompetence;
	
	private Double minSR;
	private Double minASR;
	private Double minCompetence;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		this.promises = new HashMap<String, Tuple>();
		this.outcomes = new HashMap<String, Tuple>();

		this.normSRs = new HashMap<String, Double>();
		this.baseASRs = new HashMap<String, Double>();
		this.normASRs = new HashMap<String, Double>();
		this.normExpectations = new HashMap<String, Double>();
		this.normCompetences = new HashMap<String, Double>();
		this.refMeasures = new HashMap<String, Double>();
		this.satisfactionDgrs = new HashMap<String, Double>();
		this.failurePositions = new HashMap<String, Double>();
		this.bestPromiseValues = new HashMap<Criterion, Double>();

		this.maxSR = 0.0;
		this.maxASR = 0.0;
		this.maxCompetence = 0.0;
		
		this.minSR = 1.0;
		this.minASR = 1.0;
		this.minCompetence = 1.0;

		HashMap<String, String> partnersNames = new HashMap<String, String>();
		HashMap<String, ListTerm> subPartners = new HashMap<String, ListTerm>();

		Atom delegatorActionCode = (Atom) args[0];
		ListTerm lProposals = (ListTerm) args[1];
		ListTerm lSocialEvaluations = (ListTerm) args[2];

		Agent delegator = DefaultEnvironment.getAgentFromActionCode(delegatorActionCode.toString());
		DelegatorStatistics delegatorStatistics = DefaultEnvironment.statistics.get(delegatorActionCode.toString());

		// Parsing proposals
		for (Term term : lProposals) {
			Structure sProposal = (Structure) term;
			Structure sAction = (Structure) sProposal.getTerm(0);
			Structure sPromise = (Structure) sProposal.getTerm(1);
			Structure sOutcome = (Structure) sProposal.getTerm(2);
			Structure sFailurePosition = (Structure) sProposal.getTerm(3);
			Structure sRefMeasure = (Structure) sProposal.getTerm(4);
			Structure sASR = (Structure) sProposal.getTerm(5);
			ListTerm lSubPartners = (ListTerm) sProposal.getTerm(6);

			Atom partnerName = (Atom) sAction.getTerm(0);
			Atom partnerActionCode = (Atom) sAction.getTerm(1);
			ListTerm lPromise = (ListTerm) sPromise.getTerm(0);
			ListTerm lOutcome = (ListTerm) sOutcome.getTerm(0);
			NumberTerm ntFailurePosition = (NumberTerm) sFailurePosition.getTerm(0);
			NumberTerm ntASR = (NumberTerm) sASR.getTerm(0);
			Tuple promise = Tuple.parseTuple(lPromise);
			Tuple outcome = Tuple.parseTuple(lOutcome);

			NumberTerm ntRefMeasure;

			if (sRefMeasure.getTerm(0).isNumeric()) {
				ntRefMeasure = (NumberTerm) sRefMeasure.getTerm(0);
			} 
			else {
				ntRefMeasure = new NumberTermImpl(Double.NaN);
			}
			this.refMeasures.put(partnerActionCode.toString(), ntRefMeasure.solve());

			this.promises.put(partnerActionCode.toString(), promise);
			this.outcomes.put(partnerActionCode.toString(), outcome);
			this.satisfactionDgrs.put(partnerActionCode.toString(), computeSatisfactionDegree(promise, outcome));
			this.failurePositions.put(partnerActionCode.toString(), ntFailurePosition.solve());

			double currentASR = ntASR.solve();
			this.baseASRs.put(partnerActionCode.toString(), currentASR);
			this.normASRs.put(partnerActionCode.toString(), currentASR);

			if (this.maxASR < currentASR) {
				this.maxASR = currentASR;
			}
			if (this.minASR > currentASR) {
				this.minASR = currentASR;
			}

			double currentSR = delegatorStatistics.getPartnerStatistics(partnerActionCode.toString()).getSuccessProbability();
			this.normSRs.put(partnerActionCode.toString(), currentSR);

			if (this.maxSR < currentSR) {
				this.maxSR = currentSR;
			}
			if (this.minSR > currentSR) {
				this.minSR = currentSR;
			}

			partnersNames.put(partnerActionCode.toString(), partnerName.toString());
			subPartners.put(partnerActionCode.toString(), lSubPartners);
		}

		// Parsing social measures
		for (Term term : lSocialEvaluations) {
			Structure sSocialEvaluation = (Structure) term;
			Atom partnerActionCode = (Atom) sSocialEvaluation.getTerm(0);
			Structure sImgMeasure = (Structure) sSocialEvaluation.getTerm(1);
			Structure sRepMeasure = (Structure) sSocialEvaluation.getTerm(2);

			NumberTerm ntImgMeasure;

			if (sImgMeasure.getTerm(0).isNumeric()) {
				ntImgMeasure = (NumberTerm) sImgMeasure.getTerm(0);
			} 
			else {
				ntImgMeasure = new NumberTermImpl(Double.NaN);
			}

			NumberTerm ntRepMeasure;

			if (sRepMeasure.getTerm(0).isNumeric()) {
				ntRepMeasure = (NumberTerm) sRepMeasure.getTerm(0);
			} 
			else {
				ntRepMeasure = new NumberTermImpl(Double.NaN);
			}

			double currentCompetence = computeCompetence(
					partnerActionCode.toString(),
					ntImgMeasure.solve(),
					ntRepMeasure.solve(),
					this.refMeasures.get(partnerActionCode.toString()),
					DefaultEnvironment.simulatorParameters.imageWeight,
					DefaultEnvironment.simulatorParameters.reputationWeight,
					DefaultEnvironment.simulatorParameters.referenceWeight
			);
			
			this.normCompetences.put(partnerActionCode.toString(), currentCompetence);

			if (this.maxCompetence < currentCompetence) {
				this.maxCompetence = currentCompetence;
			}
			if (this.minCompetence > currentCompetence) {
				this.minCompetence = currentCompetence;
			}
		}

		// Normalizing dimensions
		if (DefaultEnvironment.simulatorParameters.dimensionSR.getStatus()) {
			normalizeSRs();
		}
		if (DefaultEnvironment.simulatorParameters.dimensionASR.getStatus()) {
			normalizeASRs();
		}
		if (DefaultEnvironment.simulatorParameters.dimensionCompetence.getStatus()) {
			normalizeCompetences();
		}
		if (DefaultEnvironment.simulatorParameters.dimensionExpectation.getStatus()) {
			normalizeExpectation();
		}

		// Partner selection process
		List<DSTreeProduct> products = delegator.getGoalByActionCode(delegatorActionCode.toString()).getProducts();

		double wSR = DefaultEnvironment.simulatorParameters.dimensionSR.getWeight();
		double wASR = DefaultEnvironment.simulatorParameters.dimensionASR.getWeight();
		double wExpectation = DefaultEnvironment.simulatorParameters.dimensionExpectation.getWeight();
		double wCompetence = DefaultEnvironment.simulatorParameters.dimensionCompetence.getWeight();
		double wRisk = DefaultEnvironment.simulatorParameters.dimensionRisk.getWeight();
		double wGoal = DefaultEnvironment.simulatorParameters.dimensionGoal.getWeight();
		double maxSatisfaction = 0.0;

		for (DSTreeProduct product : products) {
			double uSR = 1.0;
			double uASR = 1.0;
			double uCompetence = 0.0;
			double uExpectation = 0.0;
			double uRisk = 0.0;
			double uGoal = 0.0;
			double satisfaction = 0.0;

			for (DSTreeVariable variable : product.getVariables()) {
				satisfaction += satisfactionDgrs.get(variable.getActionCode());

				if (DefaultEnvironment.simulatorParameters.dimensionSR.getStatus()) {
					uSR *= this.normSRs.get(variable.getActionCode());
				}
				if (DefaultEnvironment.simulatorParameters.dimensionASR.getStatus()) {
					uASR *= this.normASRs.get(variable.getActionCode());
				}
				if (DefaultEnvironment.simulatorParameters.dimensionExpectation.getStatus()) {
					uExpectation += this.normExpectations.get(variable.getActionCode());
				}
				if (DefaultEnvironment.simulatorParameters.dimensionCompetence.getStatus()) {
					uCompetence += this.normCompetences.get(variable.getActionCode());
				}
				if (DefaultEnvironment.simulatorParameters.dimensionRisk.getStatus()) {
					uRisk += 0;
				}
				if (DefaultEnvironment.simulatorParameters.dimensionGoal.getStatus()) {
					uGoal += 0;
				}
			}

			satisfaction /= product.size();
			uCompetence /= product.size();
			uExpectation /= product.size();
			uRisk /= product.size();
			uGoal /= product.size();

			product.setScore(
				(uSR * wSR) + 
				(uASR * wASR) + 
				(uCompetence * wCompetence) + 
				(uExpectation * wExpectation) + 
				(uRisk * wRisk) + 
				(uGoal * wGoal)
			);

			if (satisfaction >= maxSatisfaction) {
				maxSatisfaction = satisfaction;
			}
		}

		DSTreeProduct selectedProduct;

		switch (DefaultEnvironment.simulatorParameters.partnerSelectionAlg) {
			case UCB:
				selectedProduct = ucbBestProduct(delegatorStatistics, products);
				break;
			case DYNAMIC_EGREEDY:
				selectedProduct = dynamicEGreedyBestProduct(delegatorStatistics, products);
				break;
			default:
				selectedProduct = staticEGreedyBestProduct(products);
				break;
		}

		double productASR = computeProductASR(selectedProduct);
		double productSatisfaction = computeProductSatisfaction(selectedProduct);
		double regret = maxSatisfaction - productSatisfaction;;

		// Creating return structure
		Structure sResult = new Structure("result");
		Structure sSatisfaction = new Structure("satisfaction");
		Structure sRegret = new Structure("regret");
		ListTerm lDelegatees = new ListTermImpl();

		sResult.addTerm(new Atom(computeProductResult(selectedProduct)));
		sSatisfaction.addTerm(new NumberTermImpl(productSatisfaction));
		sRegret.addTerm(new NumberTermImpl(regret));

		for (DSTreeVariable variable : selectedProduct.getVariables()) {
			Structure sDelegatee = new Structure("delegatee");
			Structure sAction = new Structure("action");
			Structure sPromise = new Structure("promise");
			Structure sOutcome = new Structure("outcome");
			Structure sFPosition = new Structure("failure");

			sAction.addTerm(new Atom(partnersNames.get(variable.getActionCode())));
			sAction.addTerm(new Atom(variable.getActionCode()));
			sPromise.addTerm(promises.get(variable.getActionCode()).toTermList());
			sOutcome.addTerm(outcomes.get(variable.getActionCode()).toTermList());
			sFPosition.addTerm(new NumberTermImpl(failurePositions.get(variable.getActionCode())));

			sDelegatee.addTerm(sAction);
			sDelegatee.addTerm(sPromise);
			sDelegatee.addTerm(sOutcome);
			sDelegatee.addTerm(sFPosition);
			sDelegatee.addTerm(subPartners.get(variable.getActionCode()));
			lDelegatees.add(sDelegatee);
		}
		Structure sProspect = new Structure("prospect");
		sProspect.addTerm(sResult);
		sProspect.addTerm(sSatisfaction);
		sProspect.addTerm(sRegret);
		sProspect.addTerm(lDelegatees);

		Structure sProduct = new Structure("product");
		sProduct.addTerm(sProspect);
		sProduct.addTerm(new NumberTermImpl(productASR));
		return un.unifies(sProduct, args[3]);
	}
	
	private double computeCompetence(
			String partnerActionCode, 
			Double imgMeasure, Double repMeasure, Double refMeasure,
			double wImg, double wRep, double wRef) 
	{
		double competence;

		if (imgMeasure.isNaN() && repMeasure.isNaN() && refMeasure.isNaN()) {
			competence = DefaultEnvironment.simulatorParameters.startupValue;
		} 
		else if (!imgMeasure.isNaN() && repMeasure.isNaN() && refMeasure.isNaN()) {
			competence = imgMeasure * (wImg + wRep + wRef);
		} 
		else if (imgMeasure.isNaN() && !repMeasure.isNaN() && refMeasure.isNaN()) {
			competence = repMeasure * (wImg + wRep + wRef);
		} 
		else if (imgMeasure.isNaN() && repMeasure.isNaN() && !refMeasure.isNaN()) {
			competence = refMeasure * (wImg + wRep + wRef);
		} 
		else if (!imgMeasure.isNaN() && !repMeasure.isNaN() && refMeasure.isNaN()) {
			double adjs = wRef / 2;
			competence = (imgMeasure * (wImg + adjs)) + (repMeasure * (wRep + adjs));
		} 
		else if (!imgMeasure.isNaN() && repMeasure.isNaN() && !refMeasure.isNaN()) {
			double adjs = wRep / 2;
			competence = (imgMeasure * (wImg + adjs)) + (refMeasure * (wRef + adjs));
		} 
		else if (imgMeasure.isNaN() && !repMeasure.isNaN() && !refMeasure.isNaN()) {
			double adjs = wImg / 2;
			competence = (repMeasure * (wRep + adjs)) + (refMeasure * (wRef + adjs));
		} 
		else {
			competence = (imgMeasure * wImg) + (repMeasure * wRep) + (refMeasure * wRef);
		}
		return competence;
	}

	private double normalizeValue(double value, double maxValue, double minValue) {
		if (maxValue == 0) {
			return 0;
		}
		return value / maxValue;
	}
	
	public double normalizeMinMaxValue(double value, double maxValue, double minValue) {
		if (maxValue == value && minValue == value) {
			return 1;
		}

		double deltaV = maxValue - minValue;
		
		if (deltaV > 0) {
			return (value - minValue) / deltaV;
		}
		return 0;
	}
	
	private void normalizeSRs() {
		for (String partnerActionCode : this.normSRs.keySet()) {
			this.normSRs.replace(
				partnerActionCode, 
				normalizeValue(this.normSRs.get(partnerActionCode), this.maxSR, this.minSR)
			);
		}
	}

	private void normalizeASRs() {
		for (String partnerActionCode : this.normASRs.keySet()) {
			this.normASRs.replace(
				partnerActionCode, 
				normalizeValue(this.normASRs.get(partnerActionCode), this.maxASR, this.minASR)
			);
		}
	}

	private void normalizeCompetences() {
		for (String partnerActionCode : this.normCompetences.keySet()) {
			this.normCompetences.replace(
				partnerActionCode, 
				normalizeValue(this.normCompetences.get(partnerActionCode), this.maxCompetence, this.minCompetence)
			);
		}
	}

	private void normalizeExpectation() {
		for (Tuple promise : this.promises.values()) {
			for (Criterion criterion : promise.getCriteria()) {
				if (this.bestPromiseValues.containsKey(criterion)) {
					double oldValue = this.bestPromiseValues.get(criterion);
					double newValue = promise.getValueOf(criterion);

					if (criterion.isMaxCriterion()) {
						if (oldValue < newValue) {
							this.bestPromiseValues.put(criterion, newValue);
						}
					} else if (oldValue > newValue) {
						this.bestPromiseValues.put(criterion, newValue);
					}
				} else {
					this.bestPromiseValues.put(criterion, promise.getValueOf(criterion));
				}
			}
		}
		for (Entry<String, Tuple> entry : this.promises.entrySet()) {
			this.normExpectations.put(entry.getKey(), this.computePromiseUtility(entry.getValue()));
		}
	}

	private double computePromiseUtility(Tuple promise) {
		Tuple tuple = new Tuple();

		for (Criterion criterion : promise.getCriteria()) {
			if (!criterion.isMaxCriterion()) {
				if (promise.getValueOf(criterion) == 0) {
					tuple.addCriterion(criterion, 1.0);
				} else {
					tuple.addCriterion(criterion,
							this.bestPromiseValues.get(criterion) / promise.getValueOf(criterion));
				}
			} else {
				if (this.bestPromiseValues.get(criterion) == 0) {
					tuple.addCriterion(criterion, 0.0);
				} else {
					tuple.addCriterion(criterion,
							promise.getValueOf(criterion) / this.bestPromiseValues.get(criterion));
				}
			}
		}
		double offerUtility = 0;

		for (Criterion criterion : tuple.getCriteria()) {
			offerUtility += tuple.getValueOf(criterion);
		}
		return offerUtility / tuple.getCriteria().size();
	}

	private double computeSatisfactionDegree(Tuple promise, Tuple outcome) {
		if (outcome.getCriteria().isEmpty()) {
			return 0.0;
		}

		double satisfactionDegree = 0;

		for (Criterion criterion : promise.getCriteria()) {
			double vOutcome = outcome.getValueOf(criterion);
			double vPromise = promise.getValueOf(criterion);
			
			if (criterion.isMaxCriterion() && (vOutcome < vPromise)) {
				satisfactionDegree += vOutcome / vPromise;
			} 
			else if (!criterion.isMaxCriterion() && (vOutcome > vPromise)) {
				satisfactionDegree += vPromise / vOutcome;
			}
			else {
				satisfactionDegree += 1;
			}
		}
		return satisfactionDegree / promise.getCriteria().size();
	}

	private double computeProductSatisfaction(DSTreeProduct product) {
		double satisfaction = 0;

		for (DSTreeVariable variable : product.getVariables()) {
			satisfaction += satisfactionDgrs.get(variable.getActionCode());
		}
		return satisfaction / product.size();
	}

	private double computeProductASR(DSTreeProduct product) {
		double asr = 1;

		for (DSTreeVariable variable : product.getVariables()) {
			asr *= this.baseASRs.get(variable.getActionCode());
		}
		return asr;
	}

	private String computeProductResult(DSTreeProduct product) {
		for (DSTreeVariable variable : product.getVariables()) {

			Tuple outcome = outcomes.get(variable.getActionCode());

			if (outcome.getCriteria().isEmpty()) {
				return "failure";
			}
		}
		return "success";
	}

	/**
	 * This method implements the epsilon-greedy algorithm using a static epsilon.
	 * @see: https://medium.com/analytics-vidhya/multi-armed-bandits-part-1-epsilon-greedy-algorithm-with-python-code-534b9e2abc9
	 *
	 */
	public DSTreeProduct staticEGreedyBestProduct(List<DSTreeProduct> products) {
		Random rand = new Random();

		if (rand.nextDouble() < 0.1) {
			return products.get(rand.nextInt(products.size()));
		} 
		else {
			return getMaximumScoreProduct(products);
		}
	}

	/**
	 * This method implements the epsilon-greedy algorithm using a dynamic epsilon.
	 * @see: https://medium.com/analytics-vidhya/multi-armed-bandits-part-1-epsilon-greedy-algorithm-with-python-code-534b9e2abc9
	 *
	 */
	public DSTreeProduct dynamicEGreedyBestProduct(DelegatorStatistics delegatorStatistic, List<DSTreeProduct> products) {
		Random rand = new Random();

		if (rand.nextDouble() < (1 / (1 + delegatorStatistic.getActionCounter()))) {
			return products.get(rand.nextInt(products.size()));
		} 
		else {
			return getMaximumScoreProduct(products);
		}
	}
	
	/**
	 * This method implements the epsilon-greedy algorithm using a dynamic epsilon.
	 * @see: https://medium.com/analytics-vidhya/multi-armed-bandits-part-1-epsilon-greedy-algorithm-with-python-code-534b9e2abc9
	 *
	 */
	public DSTreeProduct ucbBestProduct(DelegatorStatistics delegatorStatistic, List<DSTreeProduct> products) {
		
		for (DSTreeProduct product : products) {
			double score = product.getScore();
			double actions = 0;
			double bids = 0;
			
			for (DSTreeVariable variable : product.getVariables()) {
				DelegateeStatistics delegateeStatistic = delegatorStatistic.getPartnerStatistics(variable.getActionCode());
				actions += delegateeStatistic.getActionCounter();
				bids += delegateeStatistic.getBidCounter();
			}
			
			actions /= product.size();
			bids /= product.size();
			
			product.setScore(score + Math.sqrt((2 * Math.log(actions)) / bids));
		}
		return getMaximumScoreProduct(products);
	}

	/**
	 * @param products a list of DSTree products.
	 * @return the product with the highest score.
	 */
	private DSTreeProduct getMaximumScoreProduct(List<DSTreeProduct> products) {
		List<Integer> indexes = new ArrayList<Integer>();
		indexes.add(0);

		double maxScore = products.get(0).getScore();

		for (int i = 1; i < products.size(); i++) {
			if (products.get(i).getScore() > maxScore) {
				maxScore = products.get(i).getScore();
				indexes.clear();
				indexes.add(i);
			}
			if (products.get(i).getScore() == maxScore) {
				indexes.add(i);
			}
		}
		
		if (indexes.size() == 1) {
			return products.get(indexes.get(0));
		}
		
		Random rand = new Random();
		return products.get(indexes.get(rand.nextInt(indexes.size())));
	}
}