package com.yahoo.labs.samoa.topology.impl;

import com.yahoo.labs.samoa.topology.Topology;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadsEngine {
	
	private static final List<ExecutorService> threadPool = new ArrayList<ExecutorService>();
	
	public static void setNumberThreads(int numThreads) {
		if (threadPool.size() > numThreads)
			throw new IllegalStateException("You cannot set a numThreads smaller than the current size of the threads pool.");
		
		if (threadPool.size() < numThreads) {
			for (int i=threadPool.size(); i<numThreads; i++) {
				threadPool.add(Executors.newSingleThreadExecutor());
			}
		}
	}
	
	public static int getNumberThreads() {
		return threadPool.size();
	}
	
	public static ExecutorService getThreadWithIndex(int index) {
		return threadPool.get(index);
	}
	
	public static void submitTopology(Topology topology, int delay) {
		ThreadsTopology tTopology = (ThreadsTopology) topology;
		tTopology.start(delay);
	}
	
	public static void submitTopology(Topology topology, int delay, int numThreads) {
		ThreadsEngine.setNumberThreads(numThreads);
		ThreadsEngine.submitTopology(topology, delay);
	}

}
