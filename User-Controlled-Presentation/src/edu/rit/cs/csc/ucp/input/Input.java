package edu.rit.cs.csc.ucp.input;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu.rit.cs.csc.ucp.Actions;


public abstract class Input {
	
	private final Set<Actions> listeners = new HashSet<Actions>(3);
	
	public void addListener(Actions actionHandler) {
		if(actionHandler == null) {
			return;
		}
		
		listeners.add(actionHandler);
	}
	
	public void removeListener(Actions actionHandler) {
		if(actionHandler == null) {
			return;
		}
		
		listeners.remove(actionHandler);
	}
	
	protected Set<Actions> getListeners() {
		//TODO document that it's an unmodifiable set
		return Collections.unmodifiableSet(listeners);
	}
	
}
