package sanjay;

import java.util.*;

/**
 * Abstraction of a context-retrieval memory system
 */

public abstract class ContextMemoryBank {
	Vector memories = new Vector();
	protected ContextMemoryBank() {
	}

	private static class Memory{
		Content content;
		Context context;
	}

	protected static interface Context{
		abstract double distanceFromTarget(Context context);
	}

	protected static interface Content{

	}

	protected synchronized void createMemory(Content content , Context context){
		Memory m = new Memory();
		m.content = content;
		m.context = context;
		memories.add(m);
	}

	/**
	 * Simple retrieval, that gets the memory whose context is the
	 * closest to the specified context.
	 */
	public synchronized Content retrieveMemory(Context context){
		double nearestDistance = Double.MAX_VALUE;
		Memory nearestMemory = null;
		for(int i=0; i<memories.size(); i++){
			Memory m = (Memory)memories.get(i);
			double d = m.context.distanceFromTarget(context);
			if(d < nearestDistance){
				nearestDistance = d;
				nearestMemory = m;
			}
		}
		return nearestMemory.content;
	}
}