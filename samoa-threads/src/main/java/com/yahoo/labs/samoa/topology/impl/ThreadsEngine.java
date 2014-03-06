package com.yahoo.labs.samoa.topology.impl;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 Yahoo! Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.yahoo.labs.samoa.topology.Topology;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Multithreaded engine.
 * @author Anh Thu Vu
 *
 */
public class ThreadsEngine {
	
	private static final List<ExecutorService> threadPool = new ArrayList<ExecutorService>();
	
	/*
	 * Create and manage threads
	 */
	public static void setNumberOfThreads(int numThreads) {
		if (numThreads < 1)
			throw new IllegalStateException("Number of threads must be a positive integer.");
		
		if (threadPool.size() > numThreads)
			throw new IllegalStateException("You cannot set a numThreads smaller than the current size of the threads pool.");
		
		if (threadPool.size() < numThreads) {
			for (int i=threadPool.size(); i<numThreads; i++) {
				threadPool.add(Executors.newSingleThreadExecutor());
			}
		}
	}
	
	public static int getNumberOfThreads() {
		return threadPool.size();
	}
	
	public static ExecutorService getThreadWithIndex(int index) {
		if (threadPool.size() <= 0 )
			throw new IllegalStateException("Try to get ExecutorService from an empty pool.");
		index %= threadPool.size();
		return threadPool.get(index);
	}
	
	/*
	 * Submit topology and start
	 */
	private static void submitTopology(Topology topology) {
		ThreadsTopology tTopology = (ThreadsTopology) topology;
		tTopology.run();
	}
	
	public static void submitTopology(Topology topology, int numThreads) {
		ThreadsEngine.setNumberOfThreads(numThreads);
		ThreadsEngine.submitTopology(topology);
	}
	
	/* 
	 * Stop
	 */
	public static void clearThreadPool() {
		for (ExecutorService pool:threadPool) {
			pool.shutdown();
		}
		
		for (ExecutorService pool:threadPool) {
			try {
				pool.awaitTermination(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		threadPool.clear();
	}

}
