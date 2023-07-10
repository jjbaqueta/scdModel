package environments;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import jason.asSyntax.Atom;
import jason.asSyntax.Structure;

/**
 * This class implements the agents' memory. 
 * 
 * @author baqueta
 *
 */
public class Memory {
	private Integer usedSlots;
	private Integer capacity;
	private Queue<Structure> slots;
	
	public Memory(int capacity) {
		this.usedSlots = 0;
		this.capacity = capacity;
		this.slots = new LinkedList<Structure>();
	}
	
	public Structure addImpression(Structure newImpression) {
		Structure status = new Structure("status");
		
		if (this.capacity <= 0) {
			status.addTerm(new Atom("unavailable"));
		} 
		else if (this.usedSlots < this.capacity) {
			this.slots.add(newImpression);
			this.usedSlots++;
			status.addTerm(new Atom("inserted"));
		}
		else {
			Structure olderImpression = this.slots.poll();
			this.slots.add(newImpression);
			status.addTerm(olderImpression);
		}
		return status;
	}

	public Integer getNumberOfUsedSlots() {
		return this.usedSlots;
	}
	
	public Integer getCapacity(){
		return this.capacity;
	}

	public List<Object> getSlots() {
		return Collections.unmodifiableList(Arrays.asList(this.slots.toArray()));
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Structure impression : this.slots) {
			sb.append(impression.toString()).append("\n");
		}
		return "Memory (capacity:" + this.capacity 
			+ ", used slots: " + this.usedSlots + "):\n" + sb.toString();
	}
}
