package com.yahoo.labs.samoa.topology.impl;

import com.yahoo.labs.samoa.topology.IProcessingItem;

public class DestinationPIWrapper {
	private IProcessingItem pi;
	private int parallelism;
	private EventAllocationType type;
	
	public DestinationPIWrapper(IProcessingItem pi, int parallelismHint, EventAllocationType type) {
		this.pi = pi;
		this.parallelism = parallelismHint;
		this.type = type;
	}
	
	public IProcessingItem getProcessingItem() {
		return this.pi;
	}
	
	public int getParallelism() {
		return this.parallelism;
	}
	
	public EventAllocationType getEventAllocationType() {
		return this.type;
	}

}
