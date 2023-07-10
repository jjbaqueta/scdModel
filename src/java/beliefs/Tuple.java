package beliefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.model.tasks.Criterion;
import jason.NoValueException;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;

/**
 * Tuples are composed of criteria that are associated with the agents' tasks.
 * Tuple's belief format: [criterion(CriterionName, CriterionValue), ...]
 */
public class Tuple {
	protected final Map<Criterion, Double> criteriaValues;
	
	public Tuple() {
		this.criteriaValues = new HashMap<Criterion, Double>();
	}
	
	/**
	 * This method converts a list of criteria into a Tuple.
	 * 
	 * @param criteria list of criteria.
	 * @return a Tuple.
	 */
	public static Tuple parseTuple(ListTerm criteria) {
		Tuple tuple = new Tuple();
		try {			
			for (Term term : criteria) {
				Structure sCriterion = (Structure) term;
				Atom criterion = (Atom) sCriterion.getTerm(0);
				NumberTerm value = (NumberTerm) sCriterion.getTerm(1);
				tuple.addCriterion(Criterion.valueOf(criterion.toString().toUpperCase()), value.solve());
			}
		} catch (NoValueException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return tuple;
	}
	
	public void addCriterion(Criterion criterion, double value) {
		this.criteriaValues.put(criterion, value);
	}
	
	public double getValueOf(Criterion criterion) {
		return this.criteriaValues.get(criterion);
	}

	public List<Criterion> getCriteria() {
		List<Criterion> criteria = new ArrayList<Criterion>();
		criteria.addAll(this.criteriaValues.keySet());
		return Collections.unmodifiableList(criteria);
	}

	public List<Double> getValues() {
		List<Double> values = new ArrayList<Double>();
		values.addAll(this.criteriaValues.values());
		return Collections.unmodifiableList(values);
	}

	public ListTerm toTermList() {
		ListTerm terms = new ListTermImpl();
		for (Criterion criterion : getCriteria()) {
			Structure sCriterion = new Structure("criterion");
			sCriterion.addTerm(new Atom(criterion.toString().toLowerCase()));
			sCriterion.addTerm(new NumberTermImpl(this.criteriaValues.get(criterion)));
			terms.add(sCriterion);
		}
		return terms;
	}

	public Literal toBelief() {
		return Literal.parseLiteral(this.toTermList().toString());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.criteriaValues == null) ? 0 : toString().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple other = (Tuple) obj;
		if (this.criteriaValues == null) {
			if (other.criteriaValues != null)
				return false;
		} 
		else if (!toString().equals(other.toString()))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return this.toTermList().toString();
	}
}