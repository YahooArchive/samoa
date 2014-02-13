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

/**
 * @author Anh Thu Vu
 *
 */
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
	
	private static void submitTopology(Topology topology, int delay) {
		ThreadsTopology tTopology = (ThreadsTopology) topology;
		tTopology.start(delay);
	}
	
	public static void submitTopology(Topology topology, int delay, int numThreads) {
		ThreadsEngine.setNumberThreads(numThreads);
		ThreadsEngine.submitTopology(topology, delay);
	}

}
