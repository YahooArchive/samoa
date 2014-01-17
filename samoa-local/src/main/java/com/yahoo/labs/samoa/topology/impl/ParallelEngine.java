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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.yahoo.labs.samoa.topology.Topology;

/**
 * Multithreaded Engine in local mode
 * Written based on SimpleEngine
 * TODO: consider refactoring Simple/Parallel Engine, Topology, 
 * ComponentFactory and EntranceProcessingItem since they
 * are very similar to each other.
 * @author Anh Thu Vu
 */
public class ParallelEngine {
	
	/*
	 * Executors (Thread pool)
	 */
	private static List<ExecutorService> executors = new ArrayList<ExecutorService>();

	public static void setNumberOfThreads(int numThreads) {
		if (executors.size() < numThreads) {
			for (int i = executors.size(); i<numThreads; i++) {
				executors.add(Executors.newSingleThreadExecutor());
			}
		}
	}
	
	public static int getNumberOfThreads() {
		return executors.size();
	}
	
	public static ExecutorService getExecutorService(int index) {
		index = index % getNumberOfThreads();
		return executors.get(index);
	}
	
	/*
	 * Start topology
	 */
	public static void submitTopology(Topology topology) {
		if (getNumberOfThreads() <= 1)
			setNumberOfThreads(2);
		
        ParallelTopology st = (ParallelTopology) topology;
        st.createWorkerProcessingItems();
        ParallelEntranceProcessingItem epi = st.getEntranceProcessingItem();
        epi.getProcessor().onCreate(0);
        st.getTopologyStarter().start();
    }
}
