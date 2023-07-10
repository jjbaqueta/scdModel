package environments;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;

/**
 * This action inserts a new impression in the agent's memory.
 * 
 * Action inputs:
 * 	args[0] (Atom): the agent's name. 
 * 	args[2] (Structure): an impression = imp(action(Delegatee, DelegateeActionCode), Iteration, Rating, Source).
 */
public class action_addImpToMemory extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		Atom agentName = (Atom) args[0];
		Structure newImpression = (Structure) args[1];
		
		Structure status = DefaultEnvironment.agsMemory.get(agentName.toString()).addImpression(newImpression);
		
		if (!status.getTerm(0).isAtom()) {
			Structure olderImpression = (Structure) status.getTerm(0);
			Structure action = (Structure) olderImpression.getTerm(0);
			Atom delegatee = (Atom) action.getTerm(0);
			Atom delegateeActionCode = (Atom) action.getTerm(1);
			NumberTerm ntIteration = (NumberTerm) olderImpression.getTerm(1);
			ListTerm rating = (ListTerm) olderImpression.getTerm(2);
			Atom source = (Atom) olderImpression.getTerm(3);
			
			ts.getAg().delBel(Literal.parseLiteral(
				"imp(action(" + delegatee.toString() + "," + delegateeActionCode.toString() + ")," 
						+ ntIteration.solve() + "," + rating.toString() + ")[source(" + source.toString() + ")]" ));
		}		
		return true;
	}
}