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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.yahoo.labs.samoa.topology.Topology;
import com.yahoo.labs.samoa.utils.PriorityThreadPoolExecutor;

/**
 * Multithreaded Engine in local mode
 * Written based on SimpleEngine
 * @author Anh Thu Vu
 */
public class ParallelEngine {
	
	/*
	 * Executors (Thread pool)
	 */
	private static List<ExecutorService> executors = new ArrayList<ExecutorService>();
	
	public static void setNumberOfThreadsAndQueueLimit(int numThreads, int limit) {
		if (executors.size() < numThreads) {
			for (int i = executors.size(); i<numThreads; i++) {
				executors.add(new PriorityThreadPoolExecutor(limit));
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
        ParallelTopology st = (ParallelTopology) topology;
        st.createWorkerProcessingItems();
        st.setupPriorityLevel();
        st.setupEntranceProcessingItem();
        ParallelEntranceProcessingItem epi = st.getEntranceProcessingItem();
        epi.getProcessor().onCreate(0);
        st.getTopologyStarter().start();
    }
}
